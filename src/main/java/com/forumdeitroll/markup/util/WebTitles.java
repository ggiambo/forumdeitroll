package com.forumdeitroll.markup.util;

import com.forumdeitroll.LRACache;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebTitles {

	private static final Logger LOG = Logger.getLogger(WebTitles.class);

	private static final int BUFFER_SIZE = 8 * 1024;
	private static final Pattern TITLE_REGEX = Pattern.compile("(?i)<title>([^<]*?)</title>");
	private static final int CONNECT_TIMEOUT = 400;
	private static final int READ_TIMEOUT = 1000;
	private static final int MAX_REDIRECT = 3;
	private static final int CACHE_SIZE = 1000;

	public static String get(final String url) {
		try {
			return get(new URL(url));
		} catch (final MalformedURLException e) {
			return "-";
		}
	}

	public static String get(final URL url) {
		final String contents = fetch(url, MAX_REDIRECT);
		if (contents == null) return null;
		final Matcher m = TITLE_REGEX.matcher(contents);
		if (!m.find()) {
			return "";
		}

		return m.group(1).trim();
	}

	public static String fetch(final URL url, final int numRedir) {
		HttpURLConnection c = null;
		InputStream in = null;
		try {
			try {
				c = (HttpURLConnection)url.openConnection();
				c.setRequestMethod("GET");
				c.setDoInput(true);
				c.setDoOutput(false);
				c.setUseCaches(true);
				c.setConnectTimeout(CONNECT_TIMEOUT);
				c.setReadTimeout(READ_TIMEOUT);

				if (c.getResponseCode() == 301) {
					if (numRedir <= 0) return null;
					return fetch(new URL(c.getHeaderField("Location")), numRedir-1);
				}

				in = c.getInputStream();
			} catch (final IOException e) {
				return null;
			}

			final byte[] buf = new byte[BUFFER_SIZE];
			int start = 0;
			int rem = BUFFER_SIZE;

			for (;;) {
				int r;
				try {
					r = in.read(buf, start, rem);
				} catch (final IOException e) {
					System.err.println("Error: " + e);
					break;
				}

				if (r < 0) {
					break;
				}

				start += r;
				rem -= r;

				if (rem <= 0) {
					break;
				}
			}

			try {
				return new String(buf, 0, start, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				return "Java ha le checked exception più stupide mai pensate";
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					LOG.error(e);
				}
			}
			if (c != null) {
				try {
					c.disconnect();
				} catch (Exception e) {
					LOG.error(e);
				}
			}
		}
	}

	public static LRACache<String, String> Cache = new LRACache<String, String>(CACHE_SIZE) {
		@Override protected String retrieve(final String url) {
			String r = WebTitles.get(url);
			return (r == null) ? "" : r;
		}
	};

}
