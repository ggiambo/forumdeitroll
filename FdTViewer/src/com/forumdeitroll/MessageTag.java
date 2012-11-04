package com.forumdeitroll;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.servlets.MainServlet;
import com.forumdeitroll.servlets.Messages;
import com.forumdeitroll.servlets.User;

public class MessageTag extends BodyTagSupport {
	private static final long serialVersionUID = -4382505626768797422L;
	private static final Logger LOG = Logger.getLogger(MessageTag.class);

	// ----- BodyTagSupport -----
	private String search;
	private AuthorDTO author;
	public void setSearch(String search) {
		this.search = search;
	}
	public String getSearch() {
		return search;
	}
	public void setAuthor(AuthorDTO author) {
		this.author = author;
	}
	public AuthorDTO getAuthor() {
		return author;
	}

	public int doAfterBody() throws JspTagException {
		try {
			loggedUser = (AuthorDTO) pageContext.getRequest().getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
			body = getBodyContent().getString().toCharArray();
			getBodyContent().getEnclosingWriter().write(getMessage().toString());
		} catch (Exception e) {
			LOG.error("Errore durante il rendering del post "+e.getMessage(), e);
			LOG.error("BODY:\n"+getBodyContent().getString());
			throw new JspTagException(e);
		}
		return SKIP_BODY;
	}

	// ----- message parsing -----

	// per la preview
	public static String getMessageStatic(String body, String search, AuthorDTO author, AuthorDTO loggedUser) throws Exception {
		MessageTag messageTag = new MessageTag();
		messageTag.setSearch(search);
		messageTag.setAuthor(author);
		messageTag.body = body.toCharArray();
		messageTag.loggedUser = loggedUser;
		try {
			return messageTag.getMessage().toString();
		} catch (Exception e) {
			LOG.error("Errore durante il rendering del post "+e.getMessage(), e);
			LOG.error("BODY:\n"+new String(messageTag.body));
			throw new JspTagException(e);
		}
	}

	private AuthorDTO loggedUser;
	private char[] body;
	private char[] ibody;
	private int p;
	private StringBuilder out, word, line;
	private String[] searches;
	private static String[] EMPTY_STRING_ARRAY = new String[0];
	int open_b = 0, open_i = 0, open_s = 0, open_u = 0;
	int open_spoiler = 0;
	private String collapseQuotes;
	private boolean multiLineQuoteStarted;

	private static final int MAX_EMOTICONS = 200;
	private static final int MAX_EMBED = 10;
	private static final int MAX_IMMYS = 15;

	private int emotiCount = 0;
	private int embedCount = 0;
	private int immysCount = 0;

	private StringBuilder getMessage() throws Exception {
		ibody = new String(body).toLowerCase().toCharArray();
		out = new StringBuilder((int) (body.length * 1.3));
		word = new StringBuilder();
		line = new StringBuilder();
		searches = search != null && !search.equals("") ? search.split(" ") : EMPTY_STRING_ARRAY;
		open_b = 0; open_i = 0; open_s = 0; open_u = 0;
		open_spoiler = 0;
		p = -1;
		multiLineQuoteStarted = false;

		emotiCount = 0;
		embedCount = 0;
		immysCount = 0;

		collapseQuotes = loggedUser != null ?loggedUser.getPreferences().get(User.PREF_COLLAPSE_QUOTES) : null;

		while (++p < body.length) {
			char c = body[p];

			if (ifound(TAG_B)) {
				on_tag(TAG_B);
			} else if (ifound(TAG_B_END)) {
				on_tag(TAG_B_END);
			} else if (ifound(TAG_I)) {
				on_tag(TAG_I);
			} else if (ifound(TAG_I_END)) {
				on_tag(TAG_I_END);
			} else if (ifound(TAG_S)) {
				on_tag(TAG_S);
			} else if (ifound(TAG_S_END)) {
				on_tag(TAG_S_END);
			} else if (ifound(TAG_U)) {
				on_tag(TAG_U);
			} else if (ifound(TAG_U_END)) {
				on_tag(TAG_U_END);
			} else if (found(CODE)) {
				on_word();
				code();
			} else if (found(URL)) {
				on_word();
				url();
			} else if (found(IMG)) {
				on_word();
				img();
			} else if (found(YT)) {
				on_word();
				youtube();
			} else if (found(COLOR)) {
				on_word();
				start_color();
			} else if (found(COLOR_END)) {
				on_word();
				line.append("</span>");
				p += COLOR_END.length - 1;
			} else if (found(SPOILER)) {
				on_word();
				line.append("<div class='spoiler'><span class='spoilerWarning'>SPOILER!!!</span> ");
				open_spoiler++;
				p += SPOILER.length - 1;
			} else if (found(SPOILER_END)) {
				on_word();
				if (open_spoiler > 0) {
					line.append("</div>");
					open_spoiler--;
				}
				p += SPOILER_END.length - 1;
			} else if (c == ' ') {
				on_word();
				line.append(' ');
			} else if (ifound(TAG_BR)) {
				on_line();
				out.append(TAG_BR);
				p += TAG_BR.length - 1;
			} else {
				word.append(c);
			}
		}

		on_line();

		for (int i=0;i<open_b;++i) out.append(TAG_B_END);
		for (int i=0;i<open_i;++i) out.append(TAG_I_END);
		for (int i=0;i<open_s;++i) out.append(TAG_S_END);
		for (int i=0;i<open_u;++i) out.append(TAG_U_END);
		for (int i=0;i<open_spoiler;++i) out.append("</div>");

		if (multiLineQuoteStarted && "checked".equals(collapseQuotes)) {
			// Se multiLineQuoteStarted qui è vero allora non ho chiuso l'ultimo <div>
			// del quote-container... ma perché non se ne è occupato "on_line" a riga 154?
			out.append("</div></div>");
		}

		return out;
	}

	private void on_tag(char[] tag) {
		on_word();
		switch (tag[1]){
		case 'b': open_b++; break;
		case 'i': open_i++; break;
		case 's': open_s++; break;
		case 'u': open_u++; break;
		default:
			switch(tag[2]){
			case 'b':
				if (open_b > 0)
					open_b--;
				break;
			case 'i':
				if (open_i > 0)
					open_i--;
				break;
			case 's':
				if (open_s > 0)
					open_s--;
				break;
			case 'u':
				if (open_u > 0)
					open_u--;
				break;
			}
		}
		line.append(tag);
		p += tag.length - 1;
	}

	private void on_word() {
		if (!emoticons()) {
			if (!link()) {
				search();
			}
		}
		line.append(word);
		word.setLength(0);
	}

	private void on_line() {
		on_word();
		color_collapse_quote();
		out.append(line);
		line.setLength(0);
	}
	private static final class Emo implements Comparable<Emo> {
		public final String sequence, replacement, sequenceToUpper;
		public final char[] sequenceCh;
		public final int length;
		public Emo(String emoSequence, String emoReplacement) {
			super();
			this.sequence = emoSequence;
			this.sequenceToUpper = emoSequence.toUpperCase();
			this.replacement = emoReplacement;
			this.sequenceCh = emoSequence.toCharArray();
			this.length = sequenceCh.length;
		}
		@Override
		public int compareTo(Emo o) {
			return o.length - this.length;
		}
	}
	private static final Emo[] emos = load_emos();
	private static Emo[] load_emos() {
		Map<String, String[]> emoMap = Messages.getEmoMap();
		Emo[] emos = new Emo[Messages.getEmoMap().size() + Messages.getEmoExtendedMap().size()];
		int i = 0;
		for (Map.Entry<String, String[]> entry : emoMap.entrySet()) {
			String imgName = entry.getKey();
			String emoSequence = entry.getValue()[0];
			String altText = entry.getValue()[1];
			String emoReplacement = String.format("<img alt='%s' title='%s' class='emoticon' src='images/emo/%s.gif'>", altText, altText, imgName);
			emos[i++] = new Emo(emoSequence, emoReplacement);
		}
		emoMap = Messages.getEmoExtendedMap();
		for (Map.Entry<String, String[]> entry : emoMap.entrySet()) {
			String imgName = entry.getKey();
			String emoSequence = entry.getValue()[0];
			String altText = entry.getValue()[1];
			String emoReplacement = String.format("<img alt='%s' title='%s' class='emoticon' src='images/emoextended/%s.gif'>", altText, altText, imgName);
			emos[i++] = new Emo(emoSequence, emoReplacement);
		}
		Arrays.sort(emos);
		return emos;
	}
	
	private boolean emoticons() {
		word.insert(0, ' ');
		int wlen = word.length();
		for (Emo emo: emos) {
			simpleReplaceAllEmoticons(word, emo.sequence, emo.replacement);
			simpleReplaceAllEmoticons(word, emo.sequenceToUpper, emo.replacement);
		}
		if (wlen != word.length()) {
			return true;
		} else {
			word.delete(0, 1);
			return false;
		}
	}

	private static boolean isLink(StringBuilder candidate) {
		return candidate.length() > 5 &&
				(candidate.indexOf("http://") == 0 ||
				candidate.indexOf("https://") == 0 ||
				candidate.indexOf("www.") == 0 ||
				candidate.indexOf("ftp://") == 0 ||
				candidate.indexOf("mailto:") == 0 ||
				(candidate.indexOf(".com/") != -1 && candidate.indexOf("/") == candidate.indexOf(".com/") + 4) ||
				(candidate.indexOf(".it/") != -1 && candidate.indexOf("/") == candidate.indexOf(".it/") + 3) ||
				candidate.indexOf("Threads?action=") == 0 ||
				candidate.indexOf("Polls?action=") == 0) ||
				candidate.indexOf("Messages?action=") == 0 ||
				candidate.indexOf("Misc?action=") == 0;
	}
	private static String addHttpProtocol(String url) {
		if (url.startsWith("www.")
			|| url.indexOf("/") == url.indexOf(".com/") + 4
			|| url.indexOf("/") == url.indexOf(".it/") + 3) {
			return "http://" + url;
		} else {
			return url;
		}
	}
	private boolean link() {
		if (isLink(word)) {
			String url = escape(word);
			String desc = word.toString();
			url = addHttpProtocol(url);
			if (url.startsWith("http://www.forumdeitroll.it/m.aspx") || url.startsWith("http://www.forumdeitroll.it/ms.aspx")) {
				int pm;
				try {
					String m_id = url.substring(
							(pm=url.indexOf("m_id=")+5), (pm=url.indexOf("&", pm)) != -1
								? pm
								: url.length());
					Integer.parseInt(m_id);
					if (url.indexOf("m.aspx") != -1) {
						// link a messaggio
						url = "Messages?action=getById&msgId=" + m_id;	
					} else {
						// link a discussione
						url = "Threads?action=getByMessage&msgId=" + m_id;
					}
				}
				catch (NumberFormatException e) {}
				catch (IndexOutOfBoundsException e) {}
			} else if (url.indexOf(".youtube.com/watch?") == 9 ||
						url.indexOf(".youtube.com/watch?") == 10 ||
						url.indexOf(".youtube.com/watch?") == 11 ||
						url.startsWith("http://youtu.be/") ||
						url.startsWith("https://youtu.be/")) {
				String youcode = youcode(url);
				if (youcode != null) {
					youtube_embed(youcode);
					word.setLength(0);
					return true;
				}
			} else if (
					url.startsWith("http://www.forumdeitroll.com/") ||
					url.startsWith("https://www.forumdeitroll.com/") ||
					url.startsWith("http://forumdeitroll.com/") ||
					url.startsWith("https://forumdeitroll.com/")) {
				url = url.substring(url.indexOf(".com/") + 5);
			}
			if (desc.length() > 50) {
				desc = desc.substring(0, 50) + "...";
			}
			for (String s : searches) {
				int p = 0;
				while ((p = desc.indexOf(s, p)) != -1) {
					String hilight = String.format("<span class='searchHighlight'>%s</span>", s);
					desc = desc.substring(0, p) + hilight+ desc.substring(p + s.length(), desc.length());
					p += hilight.length();
				}
			}
			word.setLength(0);
			word.append(String.format("<a href=\"%s\" target='_blank' rel='nofollow noreferrer' title=\"%s\">%s</a>", wrapInRedirect(url), url, desc));
			return true;
		}
		return false;
	}

	private void search() {
		for (String s : searches) {
			simpleReplaceAll(word, s, String.format("<span class='searchHighlight'>%s</span>", s));
		}
	}

	private void color_collapse_quote() {
		int quoteLvl = 0;
		String q = QUOTE;
		int pq = 0;
		while (line.indexOf(q, pq) == pq) {
			pq += q.length();
			q = SP_QUOTE;
			quoteLvl++;
		}
		int scrittoda = (scrittoda=line.indexOf("- Scritto da: ")) != -1
				? scrittoda
				: line.indexOf("Scritto da: ");
		if (scrittoda != -1) {
			if (quoteLvl == 0 && scrittoda == 0) {
				quoteLvl++;
			} else {
				int prefixlen = quoteLvl * (QUOTE.length() + 1);
				if (quoteLvl > 0 && (prefixlen == scrittoda || (prefixlen - 1) == scrittoda)) {
					quoteLvl++;
				}
			}
		}
		
		if (quoteLvl != 0) {
			quoteLvl = quoteLvl % 4;
			if (quoteLvl == 0) quoteLvl = 4;
			line.insert(0, "<span class='quoteLvl" + quoteLvl + "'>");
			line.append("</span>");
			if (!multiLineQuoteStarted && "checked".equals(collapseQuotes)) {
				multiLineQuoteStarted = true;
				line.insert(0, "<div class='quote-container'><div>");
			}
		} else {
			if (multiLineQuoteStarted && "checked".equals(collapseQuotes)) {
				multiLineQuoteStarted = false;
				line.insert(0, "</div></div>");
			}
		}
	}
	
	private void url() {
		switch (body[p + URL.length]) {
		case ']': {
			int p_end = scanFor(URL_END);
			int p_sp = scanFor(' ');
			int p_br = iscanFor(TAG_BR, Messages.MAX_MESSAGE_LENGTH);
			boolean broken = p_sp != -1 ? p_sp < p_end : p_br != -1 ? p_br < p_end : false;
			if (p_end != -1 && !broken) {
				// [url]...[/url]
				word.append(body, p + URL.length + 1, p_end - (p + URL.length + 1));
				if (!link()) {
					word.insert(0, URL);
					word.insert(URL.length, ']');
					word.append(URL_END);
				}
				line.append(word);
				word.setLength(0);
				p = p_end + URL_END.length - 1;
			} else {
				// [url]...... senza [/url]
				line.append(URL).append(']');
				p += URL.length;
			}
			break;
		}
		case '=': {
			int p_url_end = scanFor(']');
			int p_sp = scanFor(' ');
			int p_br = iscanFor(TAG_BR, Messages.MAX_MESSAGE_LENGTH);
			boolean broken = p_sp != -1 ? p_sp < p_url_end : p_br != -1 ? p_br < p_url_end : false;
			if (p_url_end != -1 && !broken) {
				p += URL.length + 1;
				int p_end = scanFor(URL_END);
				p_br = iscanFor(TAG_BR, Messages.MAX_MESSAGE_LENGTH);
				broken = p_br != -1 ? p_br < p_end : false;
				if (p_end != -1 && !broken) {
					// [url=...] ... [/url]
					StringBuilder url = new StringBuilder().append(body, p, p_url_end - p);
					if (isLink(url)) {
						p -= URL.length + 1;
						String unescaped_url = addHttpProtocol(url.toString());
						String normalized_url = escape(unescaped_url);
						String desc = new StringBuilder()
							.append(body, p_url_end + 1, p_end - (p_url_end + 1))
							.toString();
						line.append(String.format("<a href=\"%s\" target='_blank' rel='nofollow noreferrer' title=\"%s\">%s</a>", wrapInRedirect(normalized_url), normalized_url, desc));
						p = p_end + URL_END.length - 1;
					} else {
						// [url=non_url]...[/url]
						p -= URL.length + 1;
						line.append(body, p, (p_end - p) + URL_END.length);
						p = p_end + URL_END.length - 1;
					}
				} else {
					//[url=...]... senza [/url]
					line.append(URL).append('=').append(body, p, p_url_end - (p-1));
					p += (p_url_end - p);
				}
			} else {
				// [url=.... senza ] o separato da spazi
				line.append(URL).append('=');
				p += URL.length;
			}
			break;
		}
		default:
			line.append(URL);
			p += URL.length - 1;
		}
	}
	
	private void code() {
		if (']' == body[p + CODE.length]) {
			p += CODE.length;
			int p_end;
			if ((p_end = scanFor(CODE_END)) != -1) {
				// [code]...[/code]
				line.append("<pre class='code'>");
				StringBuilder code_body = new StringBuilder();
				code_body.append(body, p + 1, p_end - (p + 1));
				simpleReplaceAll(code_body, "<BR>", "\n");
				line.append(code_body);
				line.append(PRE_TAG_END);
				p = p_end + PRE_TAG_END.length;
			} else {
				// [code] orfano di chiusura: ignora
				line.append(CODE).append(']');
				p--;
			}
		} else if (' ' == body[p + CODE.length]) {
			// cerco [code ${lang}]
			p += CODE.length + 1;
			boolean ok = false;
			int i;
			for (i=0;i<10;i++) {
				char c = body[p+i];
				if (isAlphabetic(c) || Character.isDigit(c)) continue;
				else if (i > 0 && ']' == c) {
					ok = true;
					break;
				}
				else break;
			}
			if (ok) {
				int p_end;
				if ((p_end = scanFor(CODE_END)) != -1) {
					// [code $lang]...[/code]
					line.append(String.format("<pre class='brush: %s; class-name: code'>", new String(body, p, i)));
					StringBuilder code_body = new StringBuilder();
					code_body.append(body, p + i + 1, p_end - (p + i + 1));
					simpleReplaceAll(code_body, "<BR>", "\n");
					line.append(code_body);
					line.append(PRE_TAG_END);
					p = p_end + PRE_TAG_END.length;
				} else {
					// [code $lang] senza [/code] finale
					line.append(CODE).append(' ');
					p--;
				}
			} else {
				//[code ...qualcosa non accettato
				line.append(CODE).append(' ');
				p--;
			}
		} else {
			// [code...qualcosa /= ' ' | ']'
			line.append(CODE);
			p += CODE.length - 1;
		}
	}

	private void img() {
		p += IMG.length;
		int img_end = scanFor(IMG_END);
		if (img_end == -1) {
			line.append(IMG);
			p--;
			return;
		}
		int ps = scanFor(' ');
		if (ps != -1 && ps < img_end) {
			line.append(IMG);
			p--;
			return;
		}
		String url = escape(new String(body, p, img_end - p));
		if (!isLink(new StringBuilder(url))) {
			line.append(IMG);
			p--;
			return;
		}
		url = addHttpProtocol(url);
		String showAnonImg = "yes";
		if (loggedUser != null) {
			showAnonImg = loggedUser.getPreferences().get(User.PREF_SHOWANONIMG);
		}
		if ((author != null && StringUtils.isEmpty(author.getNick()) && StringUtils.isEmpty(showAnonImg)) || immysCount > MAX_IMMYS) {
			line.append(String.format("<a href=\"%s\">Immagine postata da ANOnimo</a>", url));
		} else {
			line.append(String.format("<a class='preview' href='%s'><img class='userPostedImage' alt='Immagine postata dall&#39;utente' src=\"%s\"></a>", url, url));
			immysCount++;
		}
		p = img_end + (IMG_END.length - 1);
	}

	private String youcode(String content) {
		if (!isLink(new StringBuilder(content))) return null;
		int s = content.indexOf("http://");
		if (s == -1) {
			s = 1 + content.indexOf("https://");
			if (s == 0) return null;
		}
		boolean http = s == 0;
		boolean https = s == 1;
		int a = content.indexOf(".youtube.com/watch?"),
			b = content.indexOf("youtu.be/");
		boolean yt_classic = (http && (a == 9 || a == 10)) || (https && (a == 10 || a == 11));
		boolean yt_short = (http && b == 7) || (https && b == 8);
		if (!(yt_classic || yt_short)) return null;
		if (yt_classic) {
			a = content.indexOf("?v=");
			if (a == -1) {
				a = content.indexOf("&amp;v=");
				if (a == -1) return null;
				a += 7;
			} else {
				a += 3;
			}
			b = content.indexOf("&", a);
			return content.substring(a, b != -1 ? b : content.length());
		} else {
			a = content.indexOf("/", http ? 7 : 8) + 1;
			b = content.indexOf("?", a);
			return content.substring(a, b != -1 ? b : content.length());
		}
	}

	private void youtube_embed(String youcode) {
		String embeddYt = "yes";
		if (loggedUser != null) {
			embeddYt = loggedUser.getPreferences().get(User.PREF_EMBEDDYT);
		}
		if (StringUtils.isEmpty(embeddYt) || (embedCount > MAX_EMBED)) {
			long myYtCounter = 0l;
			if (ytCounter == Long.MAX_VALUE) {
				ytCounter = 0l;
			} else {
				ytCounter++;
			}
			myYtCounter = ytCounter;
			line.append("<a href=\"http://www.youtube.com/watch?v=").append(youcode).append("\" ");
			line.append("id=\"yt_").append(myYtCounter).append("\" ");
			line.append("onmouseover='YTgetInfo_").append(myYtCounter).append("= YTgetInfo(\"");
			line.append(myYtCounter).append("\",\"").append(youcode).append("\")'>");
			line.append("<img src='http://img.youtube.com/vi/").append(youcode).append("/2.jpg'></a>");
		} else {
			// un glande classico: l'embed
			line.append("<iframe class='youtube-player' type='text/html' width='400' height='329' src='http://www.youtube.com/embed/");
			line.append(youcode);
			line.append("' frameborder='0'></iframe>");
			++embedCount;
		}
	}

	Long ytCounter = 0l;
	private void youtube() {
		p += YT.length;
		int yt_end = scanFor(YT_END);
		if (yt_end == -1) {
			line.append(YT);
			p--;
			return;
		}
		int ps = scanFor(' ');
		if (ps != -1 && ps < yt_end) {
			line.append(YT);
			p--;
			return;
		}
		String content = escape(new String(body, p, yt_end - p));
		String youcode = youcode(content);
		if (youcode == null) {
			youcode = content;
		}
		youtube_embed(youcode);
		p = yt_end + (YT_END.length - 1);
	}

	private void start_color() {
		p += COLOR.length;
		int end_p = scanFor(']', 12);
		if (end_p != -1) {
			int i;
			for (i = p; i < end_p; i++) {
				char c = body[i];
				if (i == p && c == '#') continue;
				if (isAlphabetic(c) || Character.isDigit(c)) continue;
				break;
			}
			if (i == end_p) {
				// ok
				line.append(String.format("<span style='color:%s'>", new String(body, p, end_p - p)));
				p += end_p - p;
			} else {
				// caratteri non validi dopo il [color ${qua}]
				line.append(COLOR);
				p--;
			}
		} else {
			// tag [color non chiuso (entro i 12 char)
			line.append(COLOR);
			p--;
		}
	}

	//----- util -----
	public boolean found(char[] C) {
		if (C.length > body.length - p) return false;
		int i = 0;
		while (i < C.length && C[i] == body[p+i]) i++;
		return i == C.length;
	}

	public boolean ifound(char[] C) {
		if (C.length > ibody.length - p) return false;
		int i = 0;
		while (i < C.length && C[i] == ibody[p+i]) i++;
		return i == C.length;
	}
	public int scanFor(char[] C) {
		for (int i=p;i<body.length;i++) {
			if (C.length > body.length - i) return -1;
			int j = 0;
			while (j < C.length && C[j] == body[i + j]) j++;
			if (j == C.length) return i;
		}
		return -1;
	}
	public int iscanFor(char[] C, int limit) {
		for (int i=p;i<ibody.length && i < limit;i++) {
			if (C.length > ibody.length - i) return -1;
			int j = 0;
			while (j < C.length && C[j] == ibody[i + j]) j++;
			if (j == C.length) return i;
		}
		return -1;
	}
	public int scanFor(char c) {
		for (int i=p;i<body.length;i++) {
			if (body[i] == c) return i;
		}
		return -1;
	}
	public int scanFor(char c, int limit) {
		for (int i=p;i<body.length && i - p < limit;i++) {
			if (body[i] == c) return i;
		}
		return -1;
	}
	private static String wrapInRedirect(String url) {
		try {
			if (url.startsWith("http://") || url.startsWith("https://"))
				return "Misc?action=redirectTo&url=" + URLEncoder.encode(url, "UTF-8");
			else
				return url;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	private static String escape(StringBuilder in) {
		return StringEscapeUtils.escapeHtml4(in.toString()).replace("'", "&apos;");
	}
	private static String escape(String in) {
		return StringEscapeUtils.escapeHtml4(in).replace("'", "&apos;");
	}
	private static boolean simpleReplaceAll(StringBuilder src, String search, String replacement) {
		if (search == null || search.length() == 0) return false;
		int i = 0;
		int len = src.length();
		while ((i = src.indexOf(search, i)) != -1) {
			src.replace(i, i + search.length(), replacement);
			i += replacement.length();
		}
		return src.length() != len;
	}

	private boolean simpleReplaceAllEmoticons(StringBuilder src, String search, String replacement) {
		if (search == null || search.length() == 0) return false;
		int i = 0;
		int len = src.length();
		while ((i = src.indexOf(search, i)) != -1) {
			if (emotiCount++ > MAX_EMOTICONS) break;
			src.replace(i, i + search.length(), replacement);
			i += replacement.length();
		}
		return src.length() != len;
	}

	/* never used ?
	private static void simpleReplaceFirst(StringBuilder src, String search, String replacement) {
		if (search == null || search.length() == 0) return;
		int i = 0;
		if ((i = src.indexOf(search, i)) != -1) {
			src.replace(i, i + search.length(), replacement);
			i += replacement.length();
		}
	}
	*/
	private static boolean isAlphabetic(char ch) {
		int type = Character.getType(ch);
		return type == Character.UPPERCASE_LETTER
				|| type == Character.LOWERCASE_LETTER
				|| type == Character.TITLECASE_LETTER
				|| type == Character.MODIFIER_LETTER
				|| type == Character.OTHER_LETTER
				|| type == Character.LETTER_NUMBER;
	}

	//------ const -----
	private static final char[] TAG_B = "<b>".toCharArray();
	private static final char[] TAG_I = "<i>".toCharArray();
	private static final char[] TAG_S = "<s>".toCharArray();
	private static final char[] TAG_U = "<u>".toCharArray();
	private static final char[] TAG_B_END = "</b>".toCharArray();
	private static final char[] TAG_I_END = "</i>".toCharArray();
	private static final char[] TAG_S_END = "</s>".toCharArray();
	private static final char[] TAG_U_END = "</u>".toCharArray();
	private static final char[] PRE_TAG_END = "</pre>".toCharArray();

	private static final char[] TAG_BR = "<br>".toCharArray();

	private static final char[] CODE = "[code".toCharArray();
	private static final char[] CODE_END = "[/code]".toCharArray();

	private static final char[] IMG = "[img]".toCharArray();
	private static final char[] IMG_END = "[/img]".toCharArray();

	private static final char[] YT = "[yt]".toCharArray();
	private static final char[] YT_END = "[/yt]".toCharArray();

	private static final char[] COLOR = "[color ".toCharArray();
	private static final char[] COLOR_END = "[/color]".toCharArray();
	
	private static final char[] URL = "[url".toCharArray();
	private static final char[] URL_END = "[/url]".toCharArray();
	
	private static final char[] SPOILER = "[spoiler]".toCharArray();
	private static final char[] SPOILER_END = "[/spoiler]".toCharArray();

	private static final String QUOTE = "&gt;";
	private static final String SP_QUOTE = " &gt;";

	//----- test -----

	// injection varie
	// http://localhost:8080/fdtduezero/Threads?action=getByThread&threadId=2658737
	// [code]
	// http://localhost:8080/fdtduezero/Threads?action=getByThread&threadId=2661449
	//
}
