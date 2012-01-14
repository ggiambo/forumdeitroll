package com.acmetoy.ravanator.fdt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.acmetoy.ravanator.fdt.servlets.Messages;

public class MessageTag extends BodyTagSupport {

	private static final Pattern PATTERN_QUOTE = Pattern.compile("^(&gt;\\ ?)+");
	private static final Pattern PATTERN_IMG = Pattern.compile("\\[img\\](.*?)\\[/img\\]");
	private static final Pattern PATTERN_URL = Pattern.compile("(" +
																	"(?:https?://|ftp://|news://|mailto:|file://|\\bwww\\.)" +
																		"[a-zA-Z0-9\\-\\@;\\/?:&=%\\$_.+!*\\x27,~#]*" +
																		"(" +
																			"\\([a-zA-Z0-9\\-\\@;\\/?:&=%\\$_.+!*\\x27,~#]*\\)|" +
																			"[a-zA-Z0-9\\-\\@;\\/?:&=%\\$_+*~]"+
																		")+" +
																	")");
	private static final Pattern PATTERN_CODE = Pattern.compile("\\[code\\](.*?)\\[/code\\]");
	private static final Pattern PATTERN_YT = Pattern.compile("\\[yt\\](.*?)\\[/yt\\]");
	private static final String[] QUOTE = new String[] { "#007BDF", "#00AF59", "#9A00EF", "#AF6F00" };
	
	private static final Map<String, String> EMO_ALT_MAP = new HashMap<String, String>();
	static {
		EMO_ALT_MAP.put("1", "Sorride");
		EMO_ALT_MAP.put("2", "A bocca aperta");
		EMO_ALT_MAP.put("5", "Con la lingua fuori");
		EMO_ALT_MAP.put("12", "Deluso");
		EMO_ALT_MAP.put("3", "Occhiolino");
		EMO_ALT_MAP.put("4", "Sorpresa");
		EMO_ALT_MAP.put("7", "Arrabbiato");
		EMO_ALT_MAP.put("8", "Perplesso");
		EMO_ALT_MAP.put("10", "Triste");
		EMO_ALT_MAP.put("9", "Imbarazzato");
		EMO_ALT_MAP.put("13", "Ficoso");
		EMO_ALT_MAP.put("11", "In lacrime");
		EMO_ALT_MAP.put("6", "A bocca storta");
		EMO_ALT_MAP.put("angelo", "Angioletto");
		EMO_ALT_MAP.put("anonimo", "Anonimo");
		EMO_ALT_MAP.put("diavoletto", "Indiavolato");
		EMO_ALT_MAP.put("fantasmino", "Fantasma");
		EMO_ALT_MAP.put("geek", "Geek");
		EMO_ALT_MAP.put("idea", "Idea!");
		EMO_ALT_MAP.put("newbie", "Newbie, inesperto");
		EMO_ALT_MAP.put("noia3", "Annoiato");
		EMO_ALT_MAP.put("pirata", "Pirata");
		EMO_ALT_MAP.put("robot", "Cylon");
		EMO_ALT_MAP.put("rotfl", "Rotola dal ridere");
		EMO_ALT_MAP.put("lovewin", "Fan Windows");
		EMO_ALT_MAP.put("lovelinux", "Fan Linux");
		EMO_ALT_MAP.put("loveapple", "Fan Apple");
		EMO_ALT_MAP.put("love", "Innamorato");
		EMO_ALT_MAP.put("loveamiga", "Fan Amiga");
		EMO_ALT_MAP.put("loveatari", "Fan Atari");
		EMO_ALT_MAP.put("lovec64", "Fan Commodore64");
		EMO_ALT_MAP.put("nolove", "Disinnamorato");
		EMO_ALT_MAP.put("troll", "Troll");
		EMO_ALT_MAP.put("troll1", "Troll occhiolino");
		EMO_ALT_MAP.put("troll2", "Troll chiacchierone");
		EMO_ALT_MAP.put("troll3", "Troll occhi di fuori");
		EMO_ALT_MAP.put("troll4", "Troll di tutti i colori");
	}

	private static final long serialVersionUID = 1L;

	private String search;

	public int doAfterBody() throws JspTagException {
		JspWriter out = getBodyContent().getEnclosingWriter();
		String body = getBodyContent().getString();
		
		// code
		Matcher m = PATTERN_CODE.matcher(body);
		if (m.find()) {
			String replace = "<pre style='border:1px dotted black'>" + m.group(1).replaceAll("<BR>", "\n") + "</pre>";
			body = m.replaceFirst(replace);
			m = PATTERN_CODE.matcher(body);
		}
		
		String[] lines = body.split("<BR>");
		StringBuilder res = new StringBuilder();
		
		Map<String, String> emoMap = Messages.getEmoMap();
		
		boolean highlightSearch = search != null && search.trim().length() != 0;
		for (String line : lines) {
			if (highlightSearch && line.contains(search)) {
				// highlight search words
				line = line.replaceAll("(?i)" + search, "<span style=\"background-color:yellow\">" + search + "</span>");
			}
			
			// faccine
			StringBuffer sb = new StringBuffer();
			for (Map.Entry<String, String> entry : emoMap.entrySet()) {
				sb.append("<img border=\"0\" ");
				sb.append("alt=\"");
				sb.append(EMO_ALT_MAP.get(entry.getKey()));
				sb.append("\"" );
				sb.append("title=\"");
				sb.append(EMO_ALT_MAP.get(entry.getKey()));
				sb.append("\"" );
				sb.append("src=\"images/emo/");
				sb.append(entry.getKey());
				sb.append(".gif\" />");
				line = line.replace(entry.getValue(), sb.toString());
				sb.setLength(0);
			}
			
			// img
			m = PATTERN_IMG.matcher(line);
			if (m.find()) {
				String url = m.group(1).trim();
				if (url.toLowerCase().startsWith("http")) {
					String replace = "<a class=\"preview\" href=\"" + url + "\"><img width=\"150px\" src=\"" + url + "\" /></a>";
					line = m.replaceFirst(replace);
					m = PATTERN_IMG.matcher(line);
				}
			}
			
			// yt
			m = PATTERN_YT.matcher(line);
			if (m.find()) {
				sb.append("<object height=\"329\" width=\"400\">");
				sb.append("<param value=\"http://www.youtube.com/v/").append(m.group(1)).append("\" name=\"movie\">");
				sb.append("<param value=\"transparent\" name=\"wmode\">");
				sb.append("<embed height=\"329\" width=\"400\" wmode=\"transparent\" ");
				sb.append("type=\"application/x-shockwave-flash\" ");
				sb.append("src=\"http://www.youtube.com/v/").append(m.group(1)).append("\"></object>");
				line = m.replaceFirst(sb.toString());
				sb.setLength(0);
				m = PATTERN_YT.matcher(line);
			}

			// url
			m = PATTERN_URL.matcher(line);
			if (m.find()) {
				String url = m.group(1);
				String replace = "<a href=\"" + url + "\" rel=\"nofollow noreferrer\" target=\"_blank\">";
				if (url.length() > 50) {
					url = url.substring(0, 50) + "...";
				}
				replace += url;
				replace += "</a>";
				line = m.replaceFirst(replace);
				m = PATTERN_URL.matcher(line);
			}

			res.setLength(0);
			res.append(line);
			
			// quote
			m = PATTERN_QUOTE.matcher(line);
			if (m.find()) {
				String group = m.group(0);
				int nrQuotes = group.replace(" ", "").length() / 4;
				String color = QUOTE[(nrQuotes - 1) % QUOTE.length];
				res.insert(0, "<span style='color:" + color + "'>");
				res.append("</span>");
			}
			res.append("<BR>\n");

			try {
				out.print(res.toString());
			} catch (IOException e) {
				throw new JspTagException(e);
			}
		}
		return SKIP_BODY;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getSearch() {
		return search;
	}
}
