package com.forumdeitroll.markup.util;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ConcurrentHashMap;

//import com.forumdeitroll.markup.RenderIO;
import com.forumdeitroll.persistence.DAOFactory;

public class Links {
	private static final char[][] initseq = new char[][] {
		"www.".toCharArray(), // the only initseq element that need to be http protocol prefixed, do not move!
		"http://".toCharArray(),
		"https://".toCharArray(),
		"Threads?action=".toCharArray(),
		"Polls?action=".toCharArray(),
		"Messages?action=".toCharArray(),
		"Misc?action=".toCharArray(),
		"ftp://".toCharArray(),
		"mailto:".toCharArray(),
	};

	private static final char[] slash = "/".toCharArray();
	private static final char[] dot = ".".toCharArray();

	private static final char[][] tlds = new char[][] {
		".it/".toCharArray(),
		".com/".toCharArray(),
		".org/".toCharArray(),
		".net/".toCharArray(),
		".info/".toCharArray(),
		".de/".toCharArray(),
		".fr/".toCharArray(),
		".co.uk/".toCharArray(),
		".es/".toCharArray(),
		".eu/".toCharArray(),
		".biz/".toCharArray(),
		".name/".toCharArray(),
		".edu/".toCharArray(),
		".gov/".toCharArray(),
		".mil/".toCharArray(),
	};

	private static final char[][] fdt_domains_initseq = new char[][] {
		"http://www.forumdeitroll.com/".toCharArray(),
		"https://www.forumdeitroll.com/".toCharArray(),
		"http://forumdeitroll.com/".toCharArray(),
		"https://forumdeitroll.com/".toCharArray()
	};

	private static final char[] BR = "<BR>".toCharArray();

	public static boolean isLink(char[] buffer, int offset, int length) {
		if (Chars.containsWhitespaces(buffer, offset, length)) {
			return false;
		}
		if (Chars.indexOf(buffer, offset, length, BR, 0, BR.length, 0, false, false) != -1) {
			return false;
		}
		for (char[] seq : initseq) {
			if (0 == Chars.indexOf(buffer, offset, length, seq, 0, seq.length, 0, false, true)) {
				return length > seq.length;
			}
		}
		int p;
		for (char[] tld: tlds) {
			// .com/
			p = Chars.indexOf(buffer, offset, length, tld, 0, tld.length, 1, true, false);
			if (p != -1) {
				if (p == Chars.indexOf(buffer, offset, length, slash, 0, slash.length, 1, true, false) - tld.length + 1) {
					return Chars.isAlphanum(buffer, offset, p);
				}
			}
			// .com
			p = Chars.indexOf(buffer, offset, length, tld, 0, tld.length - 1, 1, true, false);
			if (p != -1) {
				if (p == Chars.indexOf(buffer, offset, length, dot, 0, dot.length, 0, false, false)) {
					return Chars.isAlphanum(buffer, offset, p);
				}
			}
		}
		return false;
	}

	public static boolean isInternalLink(char[] buffer, int offset, int length) {
		// the buffer region has to be always checked before with isLink, undefined behaviour ensues otherwise
		for (int i = 3; i < 7; i++) {
			if (0 == Chars.indexOf(buffer, offset, length, initseq[i], 0, initseq[i].length, 0, false, true)) {
				return true;
			}
		}
		for (int i = 0; i < fdt_domains_initseq.length; i++) {
			if (0 == Chars.indexOf(buffer, offset, length, fdt_domains_initseq[i], 0, fdt_domains_initseq[i].length, 0, false, true)) {
				return true;
			}
		}
		return false;
	}

	public static void writeUrl(Writer out, char[] buffer, int offset, int length) throws IOException {
		// the buffer region has to be always checked before with isLink, undefined behaviour ensues otherwise
		// www.
		if (0 == Chars.indexOf(buffer, offset, length, initseq[0], 0, initseq[0].length, 0, false, true)) {
			out.write("http://");
			EntityEscaper.writeEscaped(out, buffer, offset, length);
			return;
		}

		for (char[] seq : initseq) {
			if (0 == Chars.indexOf(buffer, offset, length, seq, 0, seq.length, 0, false, true)) {
				EntityEscaper.writeEscaped(out, buffer, offset, length);
				return;
			}
		}

		// tlds without http:// prefix
		int p;
		for (char[] tld: tlds) {
			// .com/
			p = Chars.indexOf(buffer, offset, length, tld, 0, tld.length, 1, true, false);
			if (p != -1) {
				if (p == Chars.indexOf(buffer, offset, length, slash, 0, slash.length, 1, true, false) - tld.length + 1) {
					out.write("http://");
					EntityEscaper.writeEscaped(out, buffer, offset, length);
					return;
				}
			}
			// .com
			p = Chars.indexOf(buffer, offset, length, tld, 0, tld.length - 1, 1, true, false);
			if (p != -1) {
				out.write("http://");
				EntityEscaper.writeEscaped(out, buffer, offset, length);
				out.write("/");
				return;
			}
		}
		// print link as-is
		EntityEscaper.writeEscaped(out, buffer, offset, length);
	}
}