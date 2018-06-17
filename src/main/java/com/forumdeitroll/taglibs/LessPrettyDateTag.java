package com.forumdeitroll.taglibs;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LessPrettyDateTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private Date date;
	public void setDate(Date date) {
		this.date = date;
	}
	private SimpleDateFormat truncFmt = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat longFmt = new SimpleDateFormat("dd/MM/yyyy ::: HH:mm");
	private SimpleDateFormat shortFmt = new SimpleDateFormat("HH:mm");
	@Override
	public int doEndTag() throws JspException {
		try {
			Date today = truncFmt.parse(truncFmt.format(new Date()));
			if (date.getTime() < today.getTime()) {
				pageContext.getOut().print(
					longFmt.format(date).replace(":::", "<span class=hide-me>alle</span>")
				);
			} else {
				pageContext.getOut().print(
					shortFmt.format(date)
				);
			}

		} catch (Exception e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}
}
