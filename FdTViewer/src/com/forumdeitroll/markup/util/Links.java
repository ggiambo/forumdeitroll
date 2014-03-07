package com.forumdeitroll.markup.util;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.forumdeitroll.markup.RenderIO;
import com.forumdeitroll.persistence.PersistenceFactory;

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
	
	private static final int MAX_DESC_LENGTH = 50;

	private static char[] THREAD_LINK_INITSEQ = "Threads?action=getByThread&threadId=".toCharArray();
	private static char[] MESSAGE_LINK_SEQ = "#msg".toCharArray();
	private static char[] AMPERSAND_SIGN = "&".toCharArray();
	private static char[] OCTOTHORPE_SIGN = "#".toCharArray();

	private static long extractThreadId(RenderIO io, int offset, int length) {
		try {
			int pos = Chars.indexOf(io.buffer, offset, length, THREAD_LINK_INITSEQ, 0, THREAD_LINK_INITSEQ.length, 0, false, false);
			if (pos == -1) {
				return 0;
			}
			offset += pos;
			length -= pos;
			// casistiche:
			// Threads?action=getByThread&threadId=0000000
			offset = offset + THREAD_LINK_INITSEQ.length;
			length = length - THREAD_LINK_INITSEQ.length;
			boolean endsWithThreadId = Chars.isNumeric(io.buffer, offset, length);
			if (endsWithThreadId) {
				return Long.parseLong(new String(io.buffer, offset, length));
			}
			// Threads?action=getByThread&threadId=0000000&...
			int offsetAmp = io.indexOf(AMPERSAND_SIGN, offset);
			if (offsetAmp == -1 || (offsetAmp - offset) > 12) {
				// Threads?action=getByThread&threadId=0000000#...
				int offsetOct = io.indexOf(OCTOTHORPE_SIGN, offset);
				if (offsetOct == -1 || (offsetOct - offset) > 12) {
					return 0;
				} else {
					// Threads?action=getByThread&threadId=0000000#msg000000
					int offsetMsgId = io.indexOf(MESSAGE_LINK_SEQ, offset);
					if (offsetMsgId != -1) {
						offsetMsgId += MESSAGE_LINK_SEQ.length;
						int lengthMsgId = length + pos + THREAD_LINK_INITSEQ.length;
						lengthMsgId -= offsetMsgId;
						if (Chars.isNumeric(io.buffer, offsetMsgId, lengthMsgId)) {
							return Long.parseLong(new String(io.buffer, offsetMsgId, lengthMsgId));
						}
					}
					// non e' link a messaggio, provo a parsare il threadId
					if (Chars.isNumeric(io.buffer, offset, (offsetOct - offset))) {
						return Long.parseLong(new String(io.buffer, offset, (offsetOct - offset)));
					} else {
						return 0;
					}
				}
			} else {
				if (Chars.isNumeric(io.buffer,offset, offsetAmp - offset)) {
					return Long.parseLong(new String(io.buffer, offset, (offsetAmp - offset)));
				} else {
					return 0;
				}
			}
		} catch (NumberFormatException e) {
			// non succede, ma se succede almeno non spacca il render
			return 0;
		}
	}

	private static ConcurrentHashMap<Long, String> titleCache =
		new ConcurrentHashMap<Long, String>();
	private static String getMessageTitle(long id) throws Exception {
		if (titleCache.containsKey(id)) {
			return titleCache.get(id);
		}
		try {
			String title = PersistenceFactory.getInstance().getMessageTitle(id);
			titleCache.put(id, title);
			return title;
		} catch (Exception e) {
			return null;
		}
		
	}

	public static void writeLinkTag(RenderIO io, int offset, int length, int descOffset, int descLength) throws Exception {
		boolean internalLink = isInternalLink(io.buffer, offset, length);
		io.write("<a rel='nofollow noreferrer' target='_blank' href=\"");
		writeUrl(io.out, io.buffer, offset, length);
		String desc = null;
		if (internalLink && offset == descOffset) {
			long id = extractThreadId(io, offset, length);
			if (id != 0) {
				desc = getMessageTitle(id);
			}
		}
		io.write("\" title=\"");
		if (desc != null) {
			EntityEscaper.writeEscaped(io.out, desc.toCharArray(), 0, desc.length());
		} else {
			EntityEscaper.writeEscaped(io.out, io.buffer, descOffset, descLength);
		}
		io.write("\" alt=\"");
		if (desc != null) {
			EntityEscaper.writeEscaped(io.out, desc.toCharArray(), 0, desc.length());
		} else {
			EntityEscaper.writeEscaped(io.out, io.buffer, descOffset, descLength);
		}
		io.write("\">");
		if (offset == descOffset) // autolink
			for (char[] domain : FaviconWhiteList.DOMAINS) {
				if (-1 != Chars.indexOf(io.buffer, offset, length, domain, 0, domain.length, 0, true, false)) {
					io.write("<img src=\"http://");
					if (Chars.equals(domain, "repubblica.it".toCharArray())) {
						io.write("www.repubblica.it"); // workaround repubblica.it non serve favicon senza www. davanti
					} else {
						io.write(domain);
					}
					io.write("/favicon.ico\" class=favicon>");
					break;
				}
			}
		if (desc != null) {
			EntityEscaper.writeEscaped(io.out, desc.toCharArray(), 0, desc.length());
		} else {
			if (descLength > MAX_DESC_LENGTH) {
				EntityEscaper.writeEscaped(io.out, io.buffer, descOffset, MAX_DESC_LENGTH);
				io.write("...");
			} else {
				EntityEscaper.writeEscaped(io.out, io.buffer, descOffset, descLength);
			}
		}
		io.write("</a>");
		if (!internalLink) {
			io.write(" <a rel='nofollow noreferrer' target='_blank' href=\"http://anonym.to/?");
			writeUrl(io.out, io.buffer, offset, length);
			io.write("\" alt='Link anonimizzato(referer)' title='Link anonimizzato(referer)'><img src='images/anonymlink.png'></a>");
		}
	}
}
