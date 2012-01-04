package com.acmetoy.ravanator.fdt;

import java.io.IOException;
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
	private static final Pattern PATTERN_URL = Pattern.compile( "([^\"]|^)(http[s]?://(.+?))( |$)" );
	private static final String[] QUOTE = new String[] { "#007BDF", "#00AF59", "#9A00EF", "#AF6F00" };

	private static final long serialVersionUID = 1L;

	private String search;

	public int doAfterBody() throws JspTagException {
		JspWriter out = getBodyContent().getEnclosingWriter();
		String body = getBodyContent().getString();
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
			for (Map.Entry<String, String> entry : emoMap.entrySet()) {
				line = line.replace(entry.getValue(), "<img border=\"0\" src=\"images/emo/" + entry.getKey() + ".gif\">");
			}
			
			// img
			Matcher m = PATTERN_IMG.matcher(line);
			if (m.find()) {
				String replace = "<a class=\"preview\" href=\"" + m.group(1) + "\"><img width=\"150px\" src=\"" + m.group(1) + "\"/></a>";
				line = m.replaceFirst(replace);
				m = PATTERN_IMG.matcher(line);
			}
			
			// url
			m = PATTERN_URL.matcher(line);
			if (m.find()) {
				String url = m.group(2);
				String replace = "<a href=\"" + url + "\">";
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
