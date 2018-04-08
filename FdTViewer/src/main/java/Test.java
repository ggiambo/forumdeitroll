import com.forumdeitroll.FdTConfig;
import com.google.gson.stream.JsonReader;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

public class Test {

	public static void main(String[] args) {
		System.out.println(verifyReCaptcha("xxx"));
	}

	public static boolean verifyReCaptcha(String recaptchaResponse) {
		HttpPost httpPost = new HttpPost("https://www.google.com/recaptcha/api/siteverify");
		Collection<NameValuePair> params = new ArrayList<>(2);
		params.add(new BasicNameValuePair("secret", FdTConfig.getProperty("recaptcha.key.secret")));
		params.add(new BasicNameValuePair("response", recaptchaResponse));
		httpPost.setEntity(new UrlEncodedFormEntity(params));

		InputStreamReader resp;
		try (CloseableHttpResponse response = HttpClients.createMinimal().execute(httpPost)) {
			InputStream is = response.getEntity().getContent();
			resp = new InputStreamReader(is);
		} catch (IOException e) {
			return false;
		}

		try (JsonReader reader = new JsonReader(resp)) {
			reader.beginObject();
			String success = reader.nextName();
			if ("success".equals(success)) {
				return reader.nextBoolean();
			}
		} catch (IOException e) {
			return false;
		}

		return false;
	}

}
