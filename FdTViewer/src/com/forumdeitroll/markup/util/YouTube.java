package com.forumdeitroll.markup.util;

import java.io.IOException;
import java.io.Writer;

import com.forumdeitroll.markup.RenderIO;
import com.forumdeitroll.markup.RenderOptions;
import com.forumdeitroll.markup.RenderState;

public class YouTube {
	
	private static final char[] YT = "[yt]".toCharArray();
	private static final char[] YT_END = "[/yt]".toCharArray();
	
	public static final int MAX_EMBED = 10;
	
	public static boolean yt(RenderIO io, RenderState state, RenderOptions opts) throws IOException {
		if (state.embedCount >= MAX_EMBED)
			return false;
		if (!opts.renderYoutube)
			return false;
		if (!io.startWith(YT))
			return false;
		int end = io.indexOf(YT_END, YT.length);
		if (end == -1 || end == YT.length)
			return false;
		if (end - YT.length > YOUCODE_MAX_LENGTH) {
			if (! Links.isLink(io.buffer, YT.length, end - YT.length))
				return false;
			int[] boundaries = YouTube.extractYoucode(io.buffer, YT.length, end - YT.length);
			if (boundaries == null)
				return false;
			if (opts.embedYoutube) {
				writeYtEmbed(io.out, io.buffer, boundaries[0], boundaries[1] - boundaries[0]);
			} else {
				writeYTImage(io.out, io.buffer, boundaries[0], boundaries[1] - boundaries[0]);
			}
			io.skip(end + YT_END.length);
			return true;
		}
		if (Links.isLink(io.buffer, YT.length, end - YT.length))
			return false;
		if (opts.embedYoutube) {
			writeYtEmbed(io.out, io.buffer, YT.length, end - YT.length);
		} else {
			writeYTImage(io.out, io.buffer, YT.length, end - YT.length);
		}
		io.skip(end + YT_END.length);
		state.embedCount++;
		return true;
	}
	
	private static final char[] YT_EMBED_START = "<iframe width=\"400\" height=\"329\" src=\"//www.youtube-nocookie.com/embed/".toCharArray();
	private static final char[] YT_EMBED_END = "\" frameborder=\"0\" allowfullscreen></iframe>".toCharArray();
	public static void writeYtEmbed(Writer out, char[] buffer, int offset, int length) throws IOException {
		out.write(YT_EMBED_START);
		EntityEscaper.writeEscaped(out, buffer, offset, length);
		out.write(YT_EMBED_END);
	}
	private static final char[] YT_IMAGE_START = "<a href=\"http://www.youtube.com/watch?v=".toCharArray();
	private static final char[] YT_IMAGE_MID1 = "\" onmouseover='YTCreateScriptTag(this, \"".toCharArray();
	private static final char[] YT_IMAGE_MID2 = "\")'><img src='http://img.youtube.com/vi/".toCharArray();
	private static final char[] YT_IMAGE_END = "/2.jpg'></a>".toCharArray();
	
	public static void writeYTImage(Writer out, char[] buffer, int offset, int length) throws IOException {
		out.write(YT_IMAGE_START);
		EntityEscaper.writeEscaped(out, buffer, offset, length);
		out.write(YT_IMAGE_MID1);
		EntityEscaper.writeEscaped(out, buffer, offset, length);
		out.write(YT_IMAGE_MID2);
		EntityEscaper.writeEscaped(out, buffer, offset, length);
		out.write(YT_IMAGE_END);
	}
	
	public static final int YOUCODE_MAX_LENGTH = 11;
	
	private static final char[] YT_CLASSIC = ".youtube.com/watch?".toCharArray();
	private static final char[] YT_SHORTENED = "youtu.be/".toCharArray();
	private static final char[] INIT_YOUCODE1 = "?v=".toCharArray();
	private static final char[] INIT_YOUCODE2 = "&v=".toCharArray();
	private static final char[] AMPERSAND = "&".toCharArray();
	private static final char[] OCTOTHORPE = "#".toCharArray();
	
	public static int[] extractYoucode(char[] buffer, int offset, int length) {
		if (!Links.isLink(buffer, offset, length)) return null;
		int p = Chars.indexOf(buffer, offset, length, YT_CLASSIC, 0, YT_CLASSIC.length, 0, false, false);
		if (p != -1) {
			p = Chars.indexOf(buffer, offset, length, INIT_YOUCODE1, 0, INIT_YOUCODE1.length, p, false, false);
			if (p != -1) {
				p += INIT_YOUCODE1.length;
			}
			if (p == -1) {
				p = Chars.indexOf(buffer, offset, length, INIT_YOUCODE2, 0, INIT_YOUCODE2.length, YT_CLASSIC.length, false, false);
				if (p != -1) {
					p += INIT_YOUCODE2.length;
				}
			}
			if (p == -1)
				return null;
			int end = Chars.indexOf(buffer, offset, length, AMPERSAND, 0, AMPERSAND.length, p, false, false);
			if (end != -1 && end - p <= YOUCODE_MAX_LENGTH)
				return new int[] {offset + p, offset + end};
			end = Chars.indexOf(buffer, offset, length, OCTOTHORPE, 0, OCTOTHORPE.length, p, false, false);
			if (end != -1 && end - p <= YOUCODE_MAX_LENGTH)
				return new int[] {offset + p, offset + end};
			return new int[] {offset + p, offset + length};
		}
		p = Chars.indexOf(buffer, offset, length, YT_SHORTENED, 0, YT_SHORTENED.length, 0, false, false);
		if (p == -1)
			return null;
		p += YT_SHORTENED.length;
		int end = Chars.indexOf(buffer, offset, length, OCTOTHORPE, 0, OCTOTHORPE.length, p, false, false);
		if (end != -1) {
			return new int[] {offset + p, offset + end};
		}
		return new int[] {offset + p, offset + length};
	}
	
}
