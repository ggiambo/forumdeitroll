package com.forumdeitroll.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class GeoIP {
	private static Map<String, Map<String, String>> cache =
		new HashMap<String, Map<String, String>>();
	public static Map<String, String> lookup(String ip) {
		if (cache.containsKey(ip)) {
			return cache.get(ip);
		}
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			URL url = new URL("http://freegeoip.net/json/" + ip);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(3000);
			con.setReadTimeout(3000);
			InputStream in = con.getInputStream();
			String json = IOUtils.toString(in, "UTF-8");
			in.close();
			map = new Gson().fromJson(json, map.getClass());
			cache.put(ip, map);
		} catch (Exception e) {
			Logger.getLogger(GeoIP.class).error("GeoIP failed for " + ip + ": " +e.getMessage(), e);
		}
		return map;
	}
}
