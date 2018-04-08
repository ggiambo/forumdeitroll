package com.forumdeitroll;

import com.google.gson.stream.JsonReader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

public class ReCaptchaUtils {

	private static final Logger LOG = Logger.getLogger(ReCaptchaUtils.class);

	public static boolean verifyReCaptcha(HttpServletRequest req) {
		String recaptchaResponse = req.getParameter("g-recaptcha-response");
		if (StringUtils.isEmpty(recaptchaResponse)) {
			return false;
		}

		return verifyReCaptcha(recaptchaResponse);
	}

	static boolean verifyReCaptcha(String recaptchaResponse) {

		HttpRequestBase request = getRequest(recaptchaResponse);
		String response = getJsonResponse(request);
		if (StringUtils.isEmpty(response)) {
			return false;
		}

		return checkResponse(response);
	}

	private static HttpPost getRequest(String recaptchaResponse) {
		HttpPost httpPost = new HttpPost("https://www.google.com/recaptcha/api/siteverify");
		Collection<NameValuePair> params = new ArrayList<>(2);
		params.add(new BasicNameValuePair("secret", FdTConfig.getProperty("recaptcha.key.secret")));
		params.add(new BasicNameValuePair("response", recaptchaResponse));
		httpPost.setEntity(new UrlEncodedFormEntity(params));
		return httpPost;
	}

	private static String getJsonResponse(HttpRequestBase request) {
		CloseableHttpClient client = HttpClients.createMinimal();
		try (CloseableHttpResponse response = client.execute(request)) {
			try (InputStream is = response.getEntity().getContent()) {
				return IOUtils.toString(is, Charset.defaultCharset());
			}
		} catch (IOException e) {
			LOG.error(e);
		}
		return null;
	}

	private static boolean checkResponse(String response) {
		try (JsonReader reader = new JsonReader(new StringReader(response))) {
			reader.beginObject();
			String success = reader.nextName();
			if ("success".equals(success)) {
				return reader.nextBoolean();
			}
		} catch (IOException e) {
			LOG.error(e);
		}

		return false;
	}
}
