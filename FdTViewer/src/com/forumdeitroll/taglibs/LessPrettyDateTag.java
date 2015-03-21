package com.forumdeitroll.taglibs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

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
					longFmt.format(date).replace(":::", "alle")
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
