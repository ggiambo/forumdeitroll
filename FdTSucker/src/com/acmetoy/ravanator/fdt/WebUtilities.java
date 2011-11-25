package com.acmetoy.ravanator.fdt;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Source;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.util.EntityUtils;

public class WebUtilities {
	
	private static final long MAX_TRIALS = 5;
	
	private static ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();

	/**
	 * Ritorna la pagina con l'uri specificato
	 * @param uri
	 * @return
	 * @throws Exception
	 */
	public static Source getPage(String uri) throws Exception {
		
		Source source = null;
		HttpClient httpClient = new DefaultHttpClient(cm);
		HttpEntity entity = null;
		try {
    		HttpGet httpget = new HttpGet(uri);
    		HttpResponse response = getResponse(httpClient, httpget);
    
    		source = new Source(response.getEntity().getContent());
    		for (Attribute a : source.getURIAttributes()) {
    			if ("action".equals(a.getKey()) && "disclaimer.aspx".equals(a.getValue())) {
    				// perform login (get page again)
    				response = getResponse(httpClient, httpget);
    				entity = response.getEntity();
    				source = new Source(entity.getContent());
    			}
    		}
		} finally {
			EntityUtils.consume(entity);
			
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
	private static HttpResponse getResponse(HttpClient httpclient, HttpGet httpget) throws Exception {
		return getResponse(httpclient, httpget, 1, null);
	}
	
	private static HttpResponse getResponse(HttpClient httpclient, HttpGet httpget, long trial, Exception prevEx) throws Exception {
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
