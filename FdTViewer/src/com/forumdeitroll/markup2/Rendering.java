package com.forumdeitroll.markup2;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.ConcurrentHashMap;
import com.forumdeitroll.markup.util.Chars;
import com.forumdeitroll.markup.util.EntityEscaper;
import com.forumdeitroll.markup.util.FaviconWhiteList;
import com.forumdeitroll.markup.util.Images;
import com.forumdeitroll.markup.util.Links;
import com.forumdeitroll.markup.util.YouTube;
import com.forumdeitroll.persistence.DAOFactory;

class Rendering {
	static final int MAX_DESC_LENGTH = 50;

	interface OfTag {
		public void render(final Writer w, final Parser.Status status, final ParserNode.TagDelimited tag) throws Exception;
	}

	static OfTag imgRendering = new OfTag() {
		public void render(final Writer w, final Parser.Status status, final ParserNode.TagDelimited td) throws Exception {
			final ParserNode.TokenNode tn = td.bodyIsOne(Token.Type.LINK);
			if (tn == null) {
				td.writeBadTag(w, status);
				return;
			}
			if (!status.rops.renderImages || (status.immyCount > Images.MAX_IMMYS)) {
				w.write("[img]");
				w.write(tn.t.text.buf, tn.t.text.start, tn.t.text.length());
				w.write("[/img]");
				return;
			}
			++status.immyCount;
			if (status.rops.authorIsAnonymous && status.rops.showImagesPlaceholder) {
				w.write(Images.ANONIMG_START);
				Links.writeUrl(w, tn.t.text.buf, tn.t.text.start, tn.t.text.length());
				w.write(Images.ANONIMG_END);
			} else {
				w.write(Images.EMBEDDED_IMAGE_START);
				Links.writeUrl(w, tn.t.text.buf, tn.t.text.start, tn.t.text.length());
				w.write(Images.EMBEDDED_IMAGE_MID);
				Links.writeUrl(w, tn.t.text.buf, tn.t.text.start, tn.t.text.length());
				w.write(Images.EMBEDDED_IMAGE_END);
			}
			w.write("<a href=\"https://www.google.com/searchbyimage?&image_url=");
			Links.writeUrl(w, tn.t.text.buf, tn.t.text.start, tn.t.text.length());
			w.write("\" alt='Ricerca immagini simili' title='Ricerca immagini simili' rel='nofollow noreferrer' target='_blank'><img src=\"https://www.google.com/favicon.ico\" style='width: 16px; height: 16px;'></a>");
			return;
		}
	};

	static OfTag urlRendering = new OfTag() {
		public void render(final Writer w, final Parser.Status status, final ParserNode.TagDelimited tag) throws Exception {
			if (tag.arg == null)  {
				final ParserNode.TokenNode tn = tag.bodyIsOne(Token.Type.TEXT);
				if (tn != null) {
					writeLinkTag(w, tn.t.text);
				} else {
					tag.writeBadTag(w, status);
				}
			} else {
				final char[] link = tag.arg.toCharArray();
				if (!Links.isLink(link, 0, link.length)) {
					tag.writeBadTag(w, status);
					return;
				}
				String title = tag.arg;
				final ParserNode.TokenNode tn = tag.bodyIsOne(Token.Type.TEXT);
				if (tn != null) {
					title = tn.t.text.toString();
				}
				// per qualche strana ragione i tag url con un argomento non possono avere favicon (bug?)
				writeLinkTagOpen(w, new Substring(tag.arg), title, false);
				if (tn != null) {
					if (tn.t.text.length() > MAX_DESC_LENGTH) {
						EntityEscaper.writeEscaped(w, tn.t.text.buf, tn.t.text.start, MAX_DESC_LENGTH);
						w.write("...");
					} else {
						EntityEscaper.writeEscaped(w, tn.t.text.buf, tn.t.text.start, tn.t.text.length());
					}
				} else {
					++status.autolinkDisableCount;
					tag.body.render3(w, status, false);
					--status.autolinkDisableCount;
				}
				final boolean internalLink = Links.isInternalLink(link, 0, link.length);
				writeLinkTagClose(w, new Substring(tag.arg), !internalLink);
			}
			return;
		}
	};

	static OfTag htmlRendering = new OfTag() {
		public void render(final Writer w, final Parser.Status status, final ParserNode.TagDelimited tag) throws Exception {
			w.write("<" + tag.ot + ">");
			tag.body.render3(w, status, false);
			w.write("</" + tag.ot + ">");
			return;
		}
	};

	static OfTag codeRendering = new OfTag() {
		public void render(final Writer w, final Parser.Status status, final ParserNode.TagDelimited tag) throws Exception {
			final ParserNode.TokenNode tn = tag.bodyIsOne(Token.Type.TEXT);
			if (tn == null) {
				tag.writeBadTag(w, status);
				return;
			}

			if ((tag.arg != null) && validCodeArg(tag)) {
				w.write("<pre class='brush: ");
				EntityEscaper.writeEscaped(w, tag.arg.toCharArray(), 0, tag.arg.length());
				w.write("; class-name: code'>");
				w.write(tn.t.text.buf, tn.t.text.start, tn.t.text.length());
				w.write("</pre>");
			} else {
				if (tn.t.multiLine) {
					w.write("<pre class='code'>");
					w.write(tn.t.text.buf, tn.t.text.start, tn.t.text.length());
					w.write("</pre>");
				} else {
					w.write("<span style='font-family: monospace'>");
					w.write(tn.t.text.buf, tn.t.text.start, tn.t.text.length());
					w.write("</span>");
				}
			}
			return;
		}

		boolean validCodeArg(final ParserNode.TagDelimited tag) {
			if (tag.arg == null) return false;
			for (int i = 0; i < tag.arg.length(); ++i) {
				if (!Character.isAlphabetic(tag.arg.charAt(i))) {
					return false;
				}
			}
			return true;
		}
	};

	static OfTag ytRendering = new OfTag() {
		public void render(final Writer w, final Parser.Status status, final ParserNode.TagDelimited tag) throws Exception {
			ParserNode.TokenNode tn = tag.bodyIsOne(Token.Type.LINK);
			if (tn != null) {
				if (!Links.isLink(tn.t.text.buf, tn.t.text.start, tn.t.text.length())) {
					tag.writeBadTag(w, status);
					return;
				}
				if (!doYoutubeLink(w, status, tn.t.text)) {
					w.write("[yt]");
					EntityEscaper.writeEscaped(w, tn.t.text.buf, tn.t.text.start, tn.t.text.length());
					w.write("[/yt]");
					return;
				}
				return;
			}
			tn = tag.bodyIsOne(Token.Type.TEXT);
			if (tn != null) {
				if (status.rops.renderYoutube) {
					if (status.rops.embedYoutube) {
						YouTube.writeYtEmbed(w, tn.t.text.buf, tn.t.text.start, tn.t.text.length());
					} else {
						YouTube.writeYTImage(w, tn.t.text.buf, tn.t.text.start, tn.t.text.length());
					}
				} else {
					w.write("[yt]");
					EntityEscaper.writeEscaped(w, tn.t.text.buf, tn.t.text.start, tn.t.text.length());
					w.write("[/yt]");
				}
				return;
			}
			tag.writeBadTag(w, status);
			return;
		}
	};

	static OfTag colorRendering = new OfTag() {
		public void render(final Writer w, final Parser.Status status, final ParserNode.TagDelimited tag) throws Exception {
			if (validColorArg(tag)) {
				w.write("<span style='color: " + tag.arg + "'>");
				tag.body.render3(w, status, false);
				w.write("</span>");
				return;
			} else {
				tag.writeBadTag(w, status);
				return;
			}
		}

		boolean validColorArg(final ParserNode.TagDelimited tag) {
			if (tag.arg == null) return false;
			if (tag.arg.length() != 7) return false;
			if (tag.arg.charAt(0) != '#') return false;

			for (int i = 1; i < tag.arg.length(); ++i) {
				final char c = Character.toLowerCase(tag.arg.charAt(i));
				if (!Character.isDigit(c) && (c != 'a') && (c != 'b') && (c != 'c') && (c != 'd') && (c != 'e') && (c != 'f')) {
					return false;
				}
			}
			return true;
		}
	};


	static OfTag spoilerRendering = new OfTag() {
		public void render(final Writer w, final Parser.Status status, final ParserNode.TagDelimited tag) throws Exception {
			w.write("<div class='spoiler'><span class='spoilerWarning'>SPOILER!!!</span> ");
			tag.body.render3(w, status, false);
			w.write("</div>");
			return;
		}
	};

	static boolean doYoutubeLink(final Writer w, final Parser.Status status, final Substring link)  throws IOException {
		if (!status.rops.renderYoutube || (status.embedCount > YouTube.MAX_EMBED)) return false;
		int[] ytbounds = YouTube.extractYoucode(link.buf, link.start, link.length());
		if (ytbounds == null) return false;
		++status.embedCount;
		if (status.rops.embedYoutube) {
			YouTube.writeYtEmbed(w, link.buf, ytbounds[0], ytbounds[1] - ytbounds[0]);
		} else {
			YouTube.writeYTImage(w, link.buf, ytbounds[0], ytbounds[1] - ytbounds[0]);
		}
		return true;
	}

	static void writeLinkTagOpen(final Writer w, final Substring link, final String title, final boolean doFavicon) throws Exception {
		w.write("<a rel='nofollow noreferrer' target='_blank' href=\"");
		Links.writeUrl(w, link.buf, link.start, link.length());
		w.write("\" title=\"");
		EntityEscaper.writeEscaped(w, title.toCharArray(), 0, title.length());
		w.write("\" alt=\"");
		EntityEscaper.writeEscaped(w, title.toCharArray(), 0, title.length());
		w.write("\">");
		if (doFavicon) {
			for (char[] domain : FaviconWhiteList.DOMAINS) {
				if (-1 != Chars.indexOf(link.buf, link.start, link.length(), domain, 0, domain.length, 0, true, false)) {
					w.write("<img src=\"http://");
					if (Chars.equals(domain, "repubblica.it".toCharArray())) {
						w.write("www.repubblica.it"); // workaround repubblica.it non serve favicon senza www. davanti
					} else {
						w.write(domain);
					}
					w.write("/favicon.ico\" class=favicon>");
					break;
				}
			}
		}
	}
	static void writeLinkTagClose(final Writer w, final Substring link, final boolean anonymize) throws Exception {
		w.write("</a>");
		if (anonymize) {
			w.write(" <a rel='nofollow noreferrer' target='_blank' href=\"http://anonym.to/?");
			Links.writeUrl(w, link.buf, link.start, link.length());
			w.write("\" alt='Link anonimizzato(referer)' title='Link anonimizzato(referer)'><img src='images/anonymlink.png'></a>");
		}
	}

	static void writeLinkTag(final Writer w, final Substring link) throws Exception {
		boolean internalLink = Links.isInternalLink(link.buf, link.start, link.length());
		String title = null;
		if (internalLink) {
			long id = extractThreadId(link);
			if (id != 0) {
				title = getMessageTitle(id);
			}
		}
		boolean useTitleForContent = true;
		if (title == null) {
			useTitleForContent = false;
			title = new String(link.buf, link.start, link.length());
		}
		writeLinkTagOpen(w, link, title, true);
		if (useTitleForContent) {
			EntityEscaper.writeEscaped(w, title.toCharArray(), 0, title.length());
		} else {
			if (link.length() > MAX_DESC_LENGTH) {
				EntityEscaper.writeEscaped(w, link.buf, link.start, MAX_DESC_LENGTH);
				w.write("...");
			} else {
				EntityEscaper.writeEscaped(w, link.buf, link.start, link.length());
			}
		}
		writeLinkTagClose(w, link, !internalLink);
	}

	static void indent(final PrintWriter w, final int depth) {
		for (int i = 0; i < depth; ++i) {
			w.print("\t");
		}
	}

	static void quote(final Writer w, final int depth) throws Exception {
		for (int i = 0; i < depth; ++i) {
			w.write("&gt; ");
		}
	}

	static int indexOf(final char[] link, final char[] needle, final int start) {
		for (int i = start; i < link.length; ++i) {
			boolean match = true;
			for (int j = 0; (j < needle.length) && ((i + j) < link.length); ++j) {
				if (link[i + j] != needle[j]) {
					match = false;
					break;
				}
			}
			if (match) {
				return i;
			}
		}
		return -1;
	}

	private static ConcurrentHashMap<Long, String> titleCache =
		new ConcurrentHashMap<Long, String>();
	private static String getMessageTitle(long id) throws Exception {
		if (titleCache.containsKey(id)) {
			return titleCache.get(id);
		}
		try {
			String title = DAOFactory.getMessagesDAO().getMessageTitle(id);
			titleCache.put(id, title);
			return title;
		} catch (Exception e) {
			return null;
		}

	}

	static long extractThreadId(final Substring link) {
		int offset = link.start;
		int length = link.length();
		try {
			int pos = Chars.indexOf(link.buf, offset, length, Links.THREAD_LINK_INITSEQ, 0, Links.THREAD_LINK_INITSEQ.length, 0, false, false);
			if (pos == -1) {
				return 0;
			}
			offset += pos;
			length -= pos;
			// casistiche:
			// Threads?action=getByThread&threadId=0000000
			offset = offset + Links.THREAD_LINK_INITSEQ.length;
			length = length - Links.THREAD_LINK_INITSEQ.length;
			boolean endsWithThreadId = Chars.isNumeric(link.buf, offset, length);
			if (endsWithThreadId) {
				return Long.parseLong(new String(link.buf, offset, length));
			}
			// Threads?action=getByThread&threadId=0000000&...
			int offsetAmp = indexOf(link.buf, Links.AMPERSAND_SIGN, offset);
			if (offsetAmp == -1 || (offsetAmp - offset) > 12) {
				// Threads?action=getByThread&threadId=0000000#...
				int offsetOct = indexOf(link.buf, Links.OCTOTHORPE_SIGN, offset);
				if (offsetOct == -1 || (offsetOct - offset) > 12) {
					return 0;
				} else {
					// Threads?action=getByThread&threadId=0000000#msg000000
					int offsetMsgId = indexOf(link.buf, Links.MESSAGE_LINK_SEQ, offset);
					if (offsetMsgId != -1) {
						offsetMsgId += Links.MESSAGE_LINK_SEQ.length;
						int lengthMsgId = link.start + link.length();
						lengthMsgId -= offsetMsgId;
						if (Chars.isNumeric(link.buf, offsetMsgId, lengthMsgId)) {
							return Long.parseLong(new String(link.buf, offsetMsgId, lengthMsgId));
						}
					}
					// non e' link a messaggio, provo a parsare il threadId
					if (Chars.isNumeric(link.buf, offset, (offsetOct - offset))) {
						return Long.parseLong(new String(link.buf, offset, (offsetOct - offset)));
					} else {
						return 0;
					}
				}
			} else {
				if (Chars.isNumeric(link.buf, offset, offsetAmp - offset)) {
					return Long.parseLong(new String(link.buf, offset, (offsetAmp - offset)));
				} else {
					return 0;
				}
			}
		} catch (NumberFormatException e) {
			// non succede, ma se succede almeno non spacca il render
			return 0;
		}
	}
}
