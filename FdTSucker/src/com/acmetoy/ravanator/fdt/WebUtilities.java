package com.acmetoy.ravanator.fdt;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Source;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class WebUtilities {
	
	private static final long MAX_TRIALS = 5;

	/**
	 * Ritorna la pagina con l'uri specificato
	 * @param uri
	 * @return
	 * @throws Exception
	 */
	public static Source getPage(String uri) throws Exception {
		
		Source source = null;
		
		ContentEncodingHttpClient httpclient = new ContentEncodingHttpClient();
		try {
    		HttpGet httpget = new HttpGet(uri);
    		HttpResponse response = getResponse(httpclient, httpget);
    
    		source = new Source(response.getEntity().getContent());
    		for (Attribute a : source.getURIAttributes()) {
    			if ("action".equals(a.getKey()) && "disclaimer.aspx".equals(a.getValue())) {
    				// perform login (get page again)
    				response = getResponse(httpclient, httpget);
    				source = new Source(response.getEntity().getContent());
    			}
    		}
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		
		return source;
	}
	
	/**
	 * Ritorna la pagina con l'uri specificato come array di bytes
	 * @param uri
	 * @return
	 * @throws Exception
	 */
	public static byte[] getPageAsBytes(String uri) throws Exception {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		ContentEncodingHttpClient httpclient = new ContentEncodingHttpClient();
		try {
    		HttpGet httpget = new HttpGet(uri);
    		HttpResponse response = getResponse(httpclient, httpget);
    		
    		BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
    		byte[] buffer = new byte[1024];
    		int count = -1;
    		while ((count = bis.read(buffer)) != -1) {
    			bos.write(buffer, 0, count);
    		}
    		bis.close();
    		bos.flush();
    		bos.close();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		
		return bos.toByteArray();
	}
	
	/**
	 * Tenta al massimo 5 volte di leggere la response
	 * @param httpclient
	 * @param httpget
	 * @return
	 * @throws Exception
	 */
	private static HttpResponse getResponse(DefaultHttpClient httpclient, HttpGet httpget) throws Exception {
		return getResponse(httpclient, httpget, 1, null);
	}
	
	private static HttpResponse getResponse(DefaultHttpClient httpclient, HttpGet httpget, long trial, Exception prevEx) throws Exception {
		if (trial == MAX_TRIALS) {
			throw prevEx;
		}
		try {
			return httpclient.execute(httpget);
		} catch (HttpHostConnectException e) {
			Thread.sleep(trial*200);
			return getResponse(httpclient, httpget, trial++, e);
		}
	}

}
