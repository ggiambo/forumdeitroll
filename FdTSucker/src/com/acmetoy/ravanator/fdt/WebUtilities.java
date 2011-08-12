package com.acmetoy.ravanator.fdt;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Source;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.ContentEncodingHttpClient;

public class WebUtilities {

	public static Source getPage(String uri) throws Exception {

		ContentEncodingHttpClient httpclient = new ContentEncodingHttpClient();
		HttpGet httpget = new HttpGet(uri);
		HttpResponse response = httpclient.execute(httpget);

		Source source = new Source(response.getEntity().getContent());
		for (Attribute a : source.getURIAttributes()) {
			if ("action".equals(a.getKey()) && "disclaimer.aspx".equals(a.getValue())) {
				// perform login (get page again)
				response = httpclient.execute(httpget);
				source = new Source(response.getEntity().getContent());
			}
		}
		return source;
	}
	
	public static byte[] getPageAsBytes(String uri) throws Exception {

		ContentEncodingHttpClient httpclient = new ContentEncodingHttpClient();
		HttpGet httpget = new HttpGet(uri);
		HttpResponse response = httpclient.execute(httpget);
		
		BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int count = -1;
		while ((count = bis.read(buffer)) != -1) {
			bos.write(buffer, 0, count);
		}
		bos.flush();
		bos.close();

		return bos.toByteArray();
	}

}
