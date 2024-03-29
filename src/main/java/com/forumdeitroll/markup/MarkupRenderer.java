package com.forumdeitroll.markup;

import com.forumdeitroll.markup.util.FaviconWhiteList;
import com.forumdeitroll.markup.util.WebTitles;
import com.forumdeitroll.persistence.DAOFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.net.URLEncoder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkupRenderer implements TokenListener {

	private static int URL_MAX_DESC_LENGTH = 50;
	private static String[] DOMAINS_STR = new String[FaviconWhiteList.DOMAINS.length];
	static {
		int index = 0;
		for (char[] domain : FaviconWhiteList.DOMAINS) {
			DOMAINS_STR[index++] = new String(domain);
		}
	}

	private Tokenizer tokenizer = new Tokenizer();
	private String text;
	private RenderOptions opts;
	private StringBuilder out;

	private int quoteLevel;
	private boolean multiLineQuoteStarted;
	private boolean codeTagOpen;
	private boolean preTagCode;
	private int[] tagsCounter;
	private String urlHref;
	private String urlHrefType;
	private StringBuilder alternateOut;
	private int emotiCount;

	public String render(String text, RenderOptions opts) throws Exception {
		reset(text, opts);
		tokenizer.tokenize(text, this);
		finalizeRender();
		return out.toString();
	}

	private void reset(String text, RenderOptions opts) {
		this.text = text;
		this.out = new StringBuilder();
		this.opts = opts;
		this.quoteLevel = 0;
		this.multiLineQuoteStarted = false;
		this.codeTagOpen = false;
		this.preTagCode = false;
		this.tagsCounter = new int[7]; // bisu + spoiler + color + carets
		this.urlHref = null;
		this.urlHrefType = null;
		this.alternateOut = null;
		this.emotiCount = 0;
	}

	@Override public void on(TokenMatcher token, TokenMatcher additional) {
		if (token.name.startsWith("QUOTES")) {
			onQuotes(token);
		} else if (token.name.equals("BR")) {
			onBr();
		} else if (token.name.startsWith("CODE")) {
			onCode(token);
		} else if (token.name.startsWith("TAG")) {
			onTag(token);
		} else if (token.name.equals("IMG")) {
			onImg(token, additional);
		} else if (token.name.startsWith("URL")) {
			onUrl(token, additional);
		} else if (token.name.equals("YT")) {
			onYoutube(token, additional);
		} else if (token.name.startsWith("SPOILER")) {
			onSpoiler(token);
		} else if (token.name.startsWith("COLOR")) {
			onColor(token);
		} else if (token.name.startsWith("LINK")) {
			onLink(token);
		} else if (token.name.startsWith("TEXT")) {
			onText(token);
		} else if (token.name.equals("VIDEO")) {
			onVideo(additional);
		}
	}

	private void finalizeRender() {
		finalizeQuotes();
		finalizeCode();
		finalizeTags();
		finalizeSpoiler();
		finalizeColor();
	}

	private void onQuotes(TokenMatcher token) {
		int newQuoteLevel = countQuoteLevel(token);
		boolean scrittoda = token.name.endsWith("SCRITTO_DA");
		if (quoteLevel != 0 || newQuoteLevel != 0) {
			if (quoteLevel != 0 && quoteLevel == newQuoteLevel) {
				if (!scrittoda && !multiLineQuoteStarted && opts.collapseQuotes) {
					emitCloseTags();
					emitCloseQuote();
					emitOpenMultiLineQuote();
					multiLineQuoteStarted = true;
					emitOpenQuote();
					emitOpenTags();
					emitQuoteText(token);
				} else {
					emitQuoteText(token);
				}
			} else if (newQuoteLevel == 0) {
				emitCloseTags();
				emitCloseQuote();
				if (multiLineQuoteStarted) {
					emitCloseMultiLineQuote();
					multiLineQuoteStarted = false;
				}
				emitOpenTags();
				quoteLevel = 0;
			} else if (quoteLevel == 0) {
				if (!scrittoda && opts.collapseQuotes && !multiLineQuoteStarted) {
					emitOpenMultiLineQuote();
					multiLineQuoteStarted = true;
				}
				quoteLevel = newQuoteLevel;
				emitCloseTags();
				emitOpenQuote();
				emitOpenTags();
				emitQuoteText(token);
			} else {
				quoteLevel = newQuoteLevel;
				emitCloseTags();
				emitCloseQuote();
				if (!scrittoda && opts.collapseQuotes && !multiLineQuoteStarted) {
					emitOpenMultiLineQuote();
					multiLineQuoteStarted = true;
				}
				emitOpenQuote();
				emitOpenTags();
				emitQuoteText(token);
			}
		}
	}

	private void finalizeQuotes() {
		if (quoteLevel > 0) {
			emitCloseQuote();
		}
		if (multiLineQuoteStarted) {
			emitCloseMultiLineQuote();
			multiLineQuoteStarted = false;
		}
	}

	private void onBr() {
		emitBr();
	}

	private void emitBr() {
		if (codeTagOpen && preTagCode) {
			out.append("\n");
		} else {
			out.append("<BR>");
		}
	}

	private int countQuoteLevel(TokenMatcher token) {
		if (token.name.equals("QUOTES_EMPTY")) {
			return 0;
		}
		int position = token.start();
		int count = token.name.endsWith("_SCRITTO_DA") ? 1 : 0;
		while (text.startsWith("&gt;", position)) {
			count++;
			position += 5;
		}
		return count;
	}

	private void emitOpenMultiLineQuote() {
		out.append("<div class='quote-container'><div>");
	}

	private void emitCloseMultiLineQuote() {
		out.append("</div></div>");
	}

	private void emitOpenQuote() {
		out.append(String.format("<span class='quoteLvl%d'>", (quoteLevel % 4 == 0 ? 4 : quoteLevel % 4)));
	}

	private void emitQuoteText(TokenMatcher token) {
		if (token.name.equals("QUOTES_SCRITTO_DA")) {
			out.append(text, token.start(), token.start(4));
			String nick = token.group(4);
			if (nick == null || nick.isEmpty()) {
				out.append("<BR>");
				return;
			}
			out.append("<a href=\"User?action=getUserInfo&amp;nick=");
			out.append(urlencode(escape(nick)));
			out.append("\">");
			out.append(escape(nick));
			out.append("</a><BR>");
		} else {
			out.append(text, token.start(), token.end());
		}
	}

	private void emitCloseQuote() {
		out.append("</span>");
	}

	private void emitCloseTags() {
		int n = tagsCounter[0];
		while (n --> 0) {
			out.append("</b>");
		}
		n = tagsCounter[1];
		while (n --> 0) {
			out.append("</i>");
		}
		n = tagsCounter[2];
		while (n --> 0) {
			out.append("</s>");
		}
		n = tagsCounter[3];
		while (n --> 0) {
			out.append("</u>");
		}
	}

	private void emitOpenTags() {
		int n = tagsCounter[0];
		while (n --> 0) {
			out.append("<b>");
		}
		n = tagsCounter[1];
		while (n --> 0) {
			out.append("<i>");
		}
		n = tagsCounter[2];
		while (n --> 0) {
			out.append("<s>");
		}
		n = tagsCounter[3];
		while (n --> 0) {
			out.append("<u>");
		}
	}

	private void onCode(TokenMatcher token) {
		if (token.name.contains("_OPEN")) {
			boolean multiline = token.name.endsWith("_MULTILINE");
			boolean withLang = token.name.startsWith("CODE_OPEN_WITH_LANG");
			codeTagOpen = true;
			if (multiline || withLang) {
				preTagCode = true;
				if (withLang) {
					String lang = token.group(2);
					out.append(String.format("<pre class='brush: %s; class-name: code'>", lang));
				} else {
					out.append("<pre class='code'>");
				}
			} else {
				out.append("<span style='font-family: monospace'>");
			}
		}
		if (token.name.endsWith("_CLOSE")) {
			finalizeCode();
		}
		if (token.name.equals("CODE_CONTENT")) {
			out.append(text, token.start(), token.end());
		}
	}

	private void finalizeCode() {
		if (codeTagOpen) {
			if (preTagCode) {
				out.append("</pre>");
				preTagCode = false;
			} else {
				out.append("</span>");
			}
			codeTagOpen = false;
		}
	}

	private void onTag(TokenMatcher token) {
		if (token.name.equals("TAG_OPEN")) {
			switch (text.charAt(token.start()+1)) {
			case 'b': case 'B': tagsCounter[0]++; break;
			case 'i': case 'I': tagsCounter[1]++; break;
			case 's': case 'S': tagsCounter[2]++; break;
			case 'u': case 'U': tagsCounter[3]++; break;
			}
		}
		if (token.name.equals("TAG_CLOSE")) {
			switch (text.charAt(token.start()+2)) {
			case 'b': case 'B': tagsCounter[0]--; break;
			case 'i': case 'I': tagsCounter[1]--; break;
			case 's': case 'S': tagsCounter[2]--; break;
			case 'u': case 'U': tagsCounter[3]--; break;
			}
		}
		out.append(text, token.start(), token.end());
	}

	private void finalizeTags() {
		while (tagsCounter[0] > 0) {
			out.append("</b>");
			tagsCounter[0]--;
		}
		while (tagsCounter[1] > 0) {
			out.append("</i>");
			tagsCounter[1]--;
		}
		while (tagsCounter[2] > 0) {
			out.append("</s>");
			tagsCounter[2]--;
		}
		while (tagsCounter[3] > 0) {
			out.append("</u>");
			tagsCounter[3]--;
		}
	}

	private void onImg(TokenMatcher token, TokenMatcher additional) {
		if (!opts.renderImages) {
			out.append(text, token.start(), token.end());
			return;
		}
		String link = escape(additional.group());
		if (additional.name.equals("LINK_WWW") || additional.name.equals("LINK_TLD")) {
			link = "http://" + link;
		}
		if (opts.authorIsAnonymous && opts.showImagesPlaceholder) {
			out.append(String.format(
				"<a rel='nofollow noreferrer' target='_blank' href=\"%s\">" +
				"Immagine postata da ANOnimo</a>"
			, link));
		} else {
			out.append(String.format(
				"<a rel='nofollow noreferrer' target='_blank' class='preview' href=\"%s\">"
				+ "<img class='userPostedImage' alt='Immagine postata dall&#39;utente' src=\"%s\">"
				+ "</a>"
			, link, link));
		}
		out.append(String.format(
			"<a href=\"https://www.google.com/searchbyimage?image_url=%s\""
			+ " title='Ricerca immagini simili'"
			+ " rel='nofollow noreferrer' target='_blank'>"
			+ "<img src=\"https://www.google.com/favicon.ico\" alt='' style='width: 16px; height: 16px;'></a>", link));
	}

	private void onUrl(TokenMatcher token, TokenMatcher additional) {
		switch (token.name) {
			case "URL":
				emitLink(additional.group(), additional.name, null, false);
				break;
			case "URL_OPEN_WITH_LINK":
				alternateOut = out;
				out = new StringBuilder();
				urlHref = additional.group();
				urlHrefType = additional.name;
				break;
			case "URL_CLOSE":
				String desc = out.toString();
				out = alternateOut;
				emitLink(urlHref, urlHrefType, desc, false);
				urlHref = null;
				urlHrefType = null;
				alternateOut = null;
				break;
			default:
				out.append(text, token.start(), token.end());
				break;
		}
	}

	private void emitLink(String link, String type, String desc, boolean autolink) {
		boolean internal = type.contains("INTERNAL");
		char lastCh = link.charAt(link.length()-1);
		if (lastCh == '.' || lastCh == ',' || lastCh == ':' || lastCh == ';') {
			link = link.substring(0, link.length()-1);
		}
		String orig = link;
		if (type.equals("LINK_WWW") || type.equals("LINK_TLD")) {
			link = "http://" + link;
		}
		if (type.contains("LINK_YOUTUBE")) {
			if (!link.startsWith("http")) {
				link = "http://" + link;
			}
		}
		if (type.equals("LINK_TLD") && !orig.contains("/")) {
			link += "/";
		}
		link = escape(link);
		out.append(String.format(
				"<a rel='nofollow noreferrer' target='_blank' href=\"%s\" title=\"%s\" >"
			, link, desc != null ? escape(desc) : escape(orig)));
		if (autolink) {
			for (String domain : DOMAINS_STR) {
				if (link.contains(domain)) {
					boolean wwwWorkaround =domain.equals("repubblica.it");
					out.append(String.format("<img src=\"http://%s%s/favicon.ico\" class=favicon>", wwwWorkaround ? "www." : "", domain));
					break;
				}
			}
		}
		if (desc == null) {
			desc = orig;
		}
		int limit = URL_MAX_DESC_LENGTH;
		if (autolink && desc.length() > limit) {
			out.append(desc, 0, limit).append("...");
		} else {
			out.append(desc);
		}
		out.append("</a>");
		if (!internal) {
			out.append(String.format(
				" <a rel='nofollow noreferrer' target='_blank'"
				+ " href=\"http://anonym.to/?%s\" "
				+ " title='Link anonimizzato(referer)'><img src='images/anonymlink.png'></a>"
				, link));
		}
		if (lastCh == '.' || lastCh == ',' || lastCh == ':' || lastCh == ';') {
			out.append(lastCh);
		}
	}

	private void onYoutube(TokenMatcher token, TokenMatcher additional) {
		if (!opts.renderYoutube) {
			out.append(text, token.start(), token.end());
			return;
		}
		String youcode = null;
		if (additional == null) {
			youcode = token.group(2);
		} else if (additional.name.equals("LINK_YOUTUBE_SHORTENED")) {
			youcode = additional.group(2);
		} else if (additional.name.equals("LINK_YOUTUBE_REGULAR")) {
			youcode = additional.group(4);
		}
		emitYoutube(youcode, additional != null ? additional.group(1) : null);
	}

	private void emitYoutube(String youcode, String link) {
		youcode = escape(youcode);
		String t = link != null ? extractTFromYoutubeUrl(link) : null;
		if (link != null && !link.startsWith("http")) {
			link = "http://" + link;
		}
		int start = 0;
		if (t != null) {
			if (t.indexOf('m') != -1) { // XXmXXs | XXm
				String m = t.substring(0, t.indexOf('m'));
				start += 60 * Integer.parseInt(m);
				if (t.indexOf('s') != -1) {
					String s = t.substring(t.indexOf('m') + 1, t.indexOf('s'));
					start += Integer.parseInt(s);
				}
			} else if (t.indexOf('s') != -1) { // XXs
				start += Integer.parseInt(t.substring(0, t.indexOf('s')));
			} else { // XX
				start += Integer.parseInt(t);
			}
		}
		String page = String.format("http://www.youtube.com/watch?v=%s", youcode);
		String title = WebTitles.Cache.lookup(page);
		if (title != null && title.endsWith(" - YouTube")) {
			title = title.substring(0, title.length() - 10);
		}
		out.append(String.format(
			"<div class=youtube-video-box style=\"background-image: url(http://img.youtube.com/vi/%s/0.jpg)\">" +
				"<div class=youtube-video-title>%s</div>" +
				"<div class='youtube-buttons'>" +
					"<a href=\"%s\" target=_blank>Vai alla pagina</a>" +
					"<br>" +
					"<a href=# onclick=\"youtube_embed(this, '%s', '%s'); return false\">Visualizza embed</a>" +
				"</div>" +
			"</div>"
		, youcode
		, title != null ? title : ""
		, link != null ? link : page
		, youcode
		, "?start=" + start));
	}

	private static Pattern t_pattern = Pattern.compile("[&?#]t=([0-9]+m[0-9]+s|[0-9]+[ms]|[0-9]+)");
	private String extractTFromYoutubeUrl(String link) {
		Matcher matcher = t_pattern.matcher(link);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	private void onSpoiler(TokenMatcher token) {
		if (token.name.equals("SPOILER_OPEN")) {
			out.append("<div class='spoiler'><span class='spoilerWarning'>SPOILER!!!</span> ");
			tagsCounter[4]++;
		} else {
			out.append("</div>");
			tagsCounter[4]--;
		}
	}

	private void finalizeSpoiler() {
		while (tagsCounter[4] > 0) {
			out.append("</div>");
			tagsCounter[4]--;
		}
	}

	private void onColor(TokenMatcher token) {
		if (token.name.equals("COLOR_OPEN")) {
			String color = token.group(2);
			out.append(String.format("<span style='color: %s'>", escape(color)));
			tagsCounter[5]++;
		} else {
			out.append("</span>");
			tagsCounter[5]--;
		}
	}

	private void finalizeColor() {
		while (tagsCounter[5] > 0) {
			out.append("</span>");
			tagsCounter[5]--;
		}
	}

	private void onVideo(TokenMatcher additional) {
		String videoUrl = additional.group();
		String cleanVideoUrl = escape(videoUrl);
		out.append(String.format(
			"<span style='border: 1px solid black; padding: 2px;'>"
			+ "&#9654; Video"
			+ " <a href=\"%s\" class=videolink target=_blank>Vai alla pagina</a>"
			+ " <a href=# onclick=\"var video = document.createElement('video'); video.src = $(this.parentNode).find('.videolink').attr('href'); video.controls = true; this.parentNode.appendChild(video); this.onclick = null; return false\">Visualizza embed</a>"
			+ "</span>", cleanVideoUrl));
	}

	private static ConcurrentHashMap<Long, String> titleCache = new ConcurrentHashMap<>();
		private static String getMessageTitle(long id) {
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

	private void onLink(TokenMatcher token) {
		if (opts.renderYoutube && token.name.startsWith("LINK_YOUTUBE")) {
			String youcode = null;
			if (token.name.equals("LINK_YOUTUBE_SHORTENED")) {
				youcode = token.group(2);
			}
			if (token.name.equals("LINK_YOUTUBE_REGULAR")) {
				youcode = token.group(4);
			}
			emitYoutube(youcode, token.group(1));
		} else {
			String desc = null;
			if (token.name.equals("LINK_INTERNAL_MSGID")) {
				long msgId =
					StringUtils.isEmpty(token.group(6))
					? Long.parseLong(token.group(4))
					: Long.parseLong(token.group(6));
				desc = getMessageTitle(msgId);
			}
			String link = token.group();
			if (token.name.contains("OLD_FORUM")) {
				if (token.name.equals("LINK_OLD_FORUM_MESSAGE")) {
					long msgId = Long.parseLong(token.group(2));
					link = "Threads?action=getByMessage&msgId=" + msgId;
					desc = getMessageTitle(msgId);
				}
				if (token.name.equals("LINK_OLD_FORUM_THREAD")) {
					long msgId = Long.parseLong(token.group(2));
					link = "Threads?action=getByThread&threadId=" + msgId;
					desc = getMessageTitle(msgId);
				}
			}
			emitLink(link, token.name, desc, true);
		}

	}

	private void onText(TokenMatcher token) {
		if (token.name.startsWith("TEXT_SNIPPET")) {
			String snippetSeq = token.group().toUpperCase();
			for (Snippet snippet : Snippet.list) {
				if (snippet.sequenceUpcase.equals(snippetSeq)) {
					out.append(snippet.htmlReplacement);
					return;
				}
			}
		} else if (token.name.startsWith("TEXT_EMOTICON")) {
			if (emotiCount >= Emoticons.MAX_EMOTICONS) {
				out.append(text, token.start(), token.end());
				return;
			}
			String emoSeq = token.group().toUpperCase();
			for (Emoticon emoticon : Emoticons.tutte) {
				if (emoticon.initialSequenceUpcase.equals(emoSeq)) {
					out.append(emoticon.htmlReplacement);
					emotiCount++;
					return;
				}
			}
		} else {
			if (-1 != TokenMatcher.Section.indexOf(text, token.start(), token.end() - token.start(), "^", 0, 1, 0, false, false)) {
				// gestione caret
				for (int i = token.start(); i < token.end(); i++) {
					char c = text.charAt(i);
					if (c == '^') {
						tagsCounter[6]++;
						out.append("<sup>");
					} else if (c == ' ') {
						while (tagsCounter[6] --> 0) {
							out.append("</sup>");
						}
						tagsCounter[6] = 0;
						out.append(c);
					} else {
						out.append(c);
					}
				}
				while (tagsCounter[6] --> 0) {
					out.append("</sup>");
				}
				tagsCounter[6] = 0;
			} else {
				out.append(text, token.start(), token.end());
			}
		}
	}

	private static String escape(String string) {
		return StringEscapeUtils.escapeHtml4(string);
	}

	private static String urlencode(String string) {
		try {
			return URLEncoder.encode(string, "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}
}
