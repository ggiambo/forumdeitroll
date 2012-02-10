package com.acmetoy.ravanator.fdt;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.servlets.MainServlet;
import com.acmetoy.ravanator.fdt.servlets.Messages;
import com.acmetoy.ravanator.fdt.servlets.User;

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
			AuthorDTO loggedUser = (AuthorDTO) pageContext.getSession().getAttribute(MainServlet.LOGGED_USER_SESSION_ATTR);
			getBodyContent().getEnclosingWriter().write(getMessage(getBodyContent().getString().toCharArray(), search, author, loggedUser).toString());
		} catch (Exception e) {
			LOG.error("Errore durante il rendering del post "+e.getMessage(), e);
			LOG.error("BODY:\n"+getBodyContent().getString());
			throw new JspTagException(e);
		}
		return SKIP_BODY;
	}
	
	// ----- message parsing -----
	
	// per la preview
	public static String getMessage(String body, String search, AuthorDTO author, AuthorDTO loggedUser) throws Exception {
		MessageTag messageTag = new MessageTag();
		try {
			return messageTag.getMessage(body.toCharArray(),search, author, loggedUser).toString();
		} catch (Exception e) {
			LOG.error("Errore durante il rendering del post "+e.getMessage(), e);
			LOG.error("BODY:\n"+new String(messageTag.body));
			throw new JspTagException(e);
		}
	}
	
	
	private char[] body;
	private char[] ibody;
	private int p;
	private StringBuilder out, word, line;
	private String[] searches;
	private static String[] EMPTY_STRING_ARRAY = new String[0];
	int open_b = 0, open_i = 0, open_s = 0, open_u = 0;
	
	private StringBuilder getMessage(char[] body, String search, AuthorDTO author, AuthorDTO loggedUser) throws Exception {
		this.body = body;
		ibody = new String(body).toLowerCase().toCharArray();
		out = new StringBuilder((int) (body.length * 1.3));
		word = new StringBuilder();
		line = new StringBuilder();
		searches = search != null && !search.equals("") ? search.split(" ") : EMPTY_STRING_ARRAY;
		open_b = 0; open_i = 0; open_s = 0; open_u = 0;
		p = -1;
		
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
			} else if (found(IMG)) {
				on_word();
				img(loggedUser);
			} else if (found(YT)) {
				on_word();
				youtube(loggedUser);
			} else if (found(COLOR)) {
				on_word();
				start_color();
			} else if (found(COLOR_END)) {
				on_word();
				line.append("</span>");
				p += COLOR_END.length - 1;
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
			case 'b': open_b--; break;
			case 'i': open_i--; break;
			case 's': open_s--; break;
			case 'u': open_u--; break;
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
		color_quote();
		out.append(line);
		line.setLength(0);
	}
	private static final class Emo {
		public final String imgName, sequence, altText, replacement, sequenceToUpper;
		public final char[] sequenceCh;
		public final int length;
		public Emo(String imgName, String emoSequence, String altText, String emoReplacement) {
			super();
			this.imgName = imgName;
			this.sequence = emoSequence;
			this.sequenceToUpper = emoSequence.toUpperCase();
			this.altText = altText;
			this.replacement = emoReplacement;
			this.sequenceCh = emoSequence.toCharArray();
			this.length = sequenceCh.length;
		}
		
	}
	private static final Emo[] emos = load_emos();
	private static Emo[] load_emos() {
		Map<String, String[]> emoMap = Messages.getEmoMap();
		Emo[] emos = new Emo[emoMap.size()];
		int i = 0;
		for (Iterator<String> imgNameIter = emoMap.keySet().iterator(); imgNameIter.hasNext();) {
			String imgName = imgNameIter.next();
			//NB: se non va bene trim, valutare diversamente nel metodo emoticons, eventualmente controllo se body[p-1] è uno spazio
			String emoSequence = emoMap.get(imgName)[0].trim();
			String altText = emoMap.get(imgName)[1];
			String emoReplacement = String.format("<img alt='%s' title='%s' src='images/emo/%s.gif'>", altText, altText, imgName);
			emos[i++] = new Emo(imgName, emoSequence, altText, emoReplacement);
		}
		return emos;
	}
	
	private boolean emoticons() {
		int wlen = word.length();
		for (Emo emo: emos) {
			simpleReplaceAll(word, emo.sequence, emo.replacement);
			simpleReplaceAll(word, emo.sequenceToUpper, emo.replacement);
		}
		return wlen != word.length();
	}
	
	private boolean link() {
		if (word.indexOf("http://") == 0 || word.indexOf("https://") == 0 ||
				word.indexOf("www.") == 0 || word.indexOf("ftp://") == 0 || word.indexOf("mailto:") == 0) {
			String url = escape(word);
			String desc = word.toString();
			if (url.startsWith("www.")) {
				url = "http://" + url;
			}
			if (desc.length() > 50) {
				desc = desc.substring(0, 50) + "...";
			}
			for (String s : searches) {
				int p = 0;
				while ((p = desc.indexOf(s, p)) != -1) {
					String hilight = String.format("<span style='color: yellow'>%s</span>", s);
					desc = desc.substring(0, p) + hilight+ desc.substring(p + s.length(), desc.length());
					p += hilight.length();
				}
			}
			word.setLength(0);
			word.append(String.format("<a href=\"%s\">%s</a>", url, desc));
		}
		return false;
	}
	
	private void search() {
		for (String s : searches) {
			simpleReplaceAll(word, s, String.format("<span style='color: yellow'>%s</span>", s));
		}
	}
	
	private void color_quote() {
		int quoteLvl = 0;
		String q = QUOTE;
		int pq = 0;
		while (line.indexOf(q, pq) == pq) {
			pq += q.length();
			q = SP_QUOTE;
			quoteLvl++;
		}
		if (quoteLvl != 0) {
			if (quoteLvl > 4) quoteLvl = 1 + (quoteLvl % 4);
			line.insert(0, "<span class='quoteLvl" + quoteLvl + "'>");
			line.append("</span>");
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
	
	private void img(AuthorDTO loggedUser) {
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
		String showAnonImg = "yes";
		if (loggedUser != null) {
			showAnonImg = loggedUser.getPreferences().getProperty(User.PREF_SHOWANONIMG);
		}
		if (author != null && StringUtils.isEmpty(author.getNick()) && StringUtils.isEmpty(showAnonImg)) {
			line.append(String.format("<a href=\"%s\">Immagine postata da ANOnimo</a>", url));
		} else {
			line.append(String.format("<a class='preview' href='%s'><img class='userPostedImage' alt='Immagine postata dall&#39;utente' src=\"%s\"></a>", url, url));
		}
		p = img_end + (IMG_END.length - 1);
	}
	
	Long ytCounter = 0l;
	private void youtube(AuthorDTO loggedUser) {
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
		String youcode = escape(new String(body, p, yt_end - p));
		String embeddYt = "yes";
		if (loggedUser != null) {
			embeddYt = loggedUser.getPreferences().getProperty(User.PREF_EMBEDDYT);
		}
		if (StringUtils.isEmpty(embeddYt)) {
			long myYtCounter = 0l;
			synchronized (ytCounter) {
				if (ytCounter == Long.MAX_VALUE) {
					ytCounter = 0l;
				} else {
					ytCounter++;
				}
				myYtCounter = ytCounter;
			}
			line.append("<a href=\"http://www.youtube.com/watch?v=").append(youcode).append("\" ");
			line.append("id=\"yt_").append(myYtCounter).append("\">");
			line.append("http://www.youtube.com/watch?v=").append(youcode).append("</a>");
			line.append("<script type='text/javascript'>YTgetInfo_");
			line.append(myYtCounter).append("= YTgetInfo('");
			line.append(myYtCounter).append("')</script>");
			line.append("<script type='text/javascript' src=\"");
			line.append("http://gdata.youtube.com/feeds/api/videos/").append(youcode);
			line.append("?v=2&amp;alt=json-in-script&amp;callback=YTgetInfo_");
			line.append(myYtCounter).append("\"></script>");
		} else {
			// un glande classico: l'embed
			line.append("<object height='329' width='400'>");
			line.append("<param value='http://www.youtube.com/v/").append(youcode).append("' name='movie'>");
			line.append("<param value='transparent' name='wmode'>");
			line.append("<embed height='329' width='400' wmode='transparent' ");
			line.append("type='application/x-shockwave-flash' ");
			line.append("src='http://www.youtube.com/v/").append(youcode).append("'></object>");
		}
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
	private static String escape(StringBuilder in) {
		return StringEscapeUtils.escapeHtml4(in.toString()).replace("'", "&quot;");
	}
	private static String escape(String in) {
		return StringEscapeUtils.escapeHtml4(in).replace("'", "&quot;");
	}
	private static void simpleReplaceAll(StringBuilder src, String search, String replacement) {
		if (search == null || search.length() == 0) return;
		int i = 0;
		while ((i = src.indexOf(search, i)) != -1) {
			src.replace(i, i + search.length(), replacement);
			i += replacement.length();
		}
	}
	
	private static void simpleReplaceFirst(StringBuilder src, String search, String replacement) {
		if (search == null || search.length() == 0) return;
		int i = 0;
		if ((i = src.indexOf(search, i)) != -1) {
			src.replace(i, i + search.length(), replacement);
			i += replacement.length();
		}
	}
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
	
	private static final String QUOTE = "&gt;";
	private static final String SP_QUOTE = " &gt;";
	
	//----- test -----
	
	// injection varie
	// http://localhost:8080/fdtduezero/Threads?action=getByThread&threadId=2658737
	// [code]
	// http://localhost:8080/fdtduezero/Threads?action=getByThread&threadId=2661449
	//
}
