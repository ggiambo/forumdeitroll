package com.forumdeitroll.markup.util;

import com.forumdeitroll.markup.RenderIO;
import com.forumdeitroll.markup.RenderState;

public class Url {
	
	private static final char[] URL = "[url]".toCharArray();
	private static final char[] URL_OPEN = "[url=".toCharArray();
	private static final char[] URL_END = "[/url]".toCharArray();
	
	private static final char[] CLOSE_SQUARE_BRACKET = "]".toCharArray();
	
	public static boolean url(RenderIO io, RenderState state) throws Exception {
		if (io.startWith(URL)) {
			int end = io.indexOf(URL_END, URL.length);
			if (end == -1)
				return false;
			if (!Links.isLink(io.buffer, URL.length, end - URL.length))
				return false;
			// [url]$link[/url]
			Links.writeLinkTag(io, URL.length, end - URL.length, URL.length, end - URL.length);
			io.skip(end + URL_END.length);
			return true;
		}
		if (io.startWith(URL_OPEN)) {
			int end = io.indexOf(CLOSE_SQUARE_BRACKET, URL_OPEN.length);
			if (end == -1)
				return false;
			if (!Links.isLink(io.buffer, URL_OPEN.length, end - URL_OPEN.length))
				return false;
			int endDesc = io.indexOf(URL_END, end + 1);
			if (endDesc == -1)
				return false;
			if (endDesc == end + 1)
				// [url=$link][/url]
				Links.writeLinkTag(io, URL_OPEN.length, end - URL_OPEN.length, URL_OPEN.length, end - URL_OPEN.length);
			else
				// [url=$link]$desc[/url]
				Links.writeLinkTag(io, URL_OPEN.length, end - URL_OPEN.length, end + 1, endDesc - end - 1);
			io.skip(endDesc + URL_END.length);
			return true;
		}
		return false;
	}
	

}
