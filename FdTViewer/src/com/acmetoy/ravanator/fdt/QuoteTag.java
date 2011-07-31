package com.acmetoy.ravanator.fdt;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class QuoteTag extends BodyTagSupport {

	private static final Pattern PATTERN = Pattern.compile("^(&gt;\\ ?)+");
	private static final String[] QUOTE = new String[] { "#007BDF", "#00AF59", "#9A00EF", "#AF6F00" };

	private static final long serialVersionUID = 1L;

	public int doAfterBody() throws JspTagException {
		JspWriter out = getBodyContent().getEnclosingWriter();
		String body = getBodyContent().getString();
		String[] lines = body.split("<BR>");
		StringBuilder res = new StringBuilder();
		for (String line : lines) {
			res.setLength(0);
			res.append(line);
			Matcher m = PATTERN.matcher(line);
			try {
				if (m.find()) {
					String group = m.group(0);
					int nrQuotes = group.replace(" ", "").length() / 4;
					String color = QUOTE[(nrQuotes - 1) % QUOTE.length];
					res.insert(0, "<span style='color:" + color + "'>");
					res.append("</span>");
				}
				res.append("<BR>");
				out.print(res.toString());
			} catch (IOException e) {
				throw new JspTagException(e);
			}
		}
		return SKIP_BODY;
	}
}
