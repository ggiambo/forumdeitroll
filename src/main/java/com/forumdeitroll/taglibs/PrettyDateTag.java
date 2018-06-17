package com.forumdeitroll.taglibs;

import org.apache.commons.text.StringEscapeUtils;
import org.ocpsoft.prettytime.PrettyTime;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class PrettyDateTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private Date date;
	public void setDate(Date date) {
		this.date = date;
	}
	@Override
	public int doEndTag() throws JspException {
		String formattedDate = new PrettyTime(Locale.ITALIAN).format(date);
		try {
			pageContext.getOut().print(StringEscapeUtils.escapeHtml4(formattedDate));
		} catch (IOException e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}
}
