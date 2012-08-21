package com.forumdeitroll;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class DelayedScriptTag extends BodyTagSupport {
	
	private static final long serialVersionUID = 1L;
	
	/*
	 * come si usa
	 * 
	 * <fdt:delayedScript>
	 * $.ready(......${anche.jstl}....<%= e jsp %>....)...
	 * </fdt:delayedScript>

	 */
	
	private String dump;
	public String getDump() {
		return dump;
	}
	public void setDump(String dump) {
		this.dump = dump;
	}

	public int doAfterBody() throws JspTagException {
		StringBuilder delayedScripts = (StringBuilder) pageContext.getRequest().getAttribute("delayedScripts");
		if (dump != null && Boolean.parseBoolean(dump)) {
			if (delayedScripts != null) {
				try {
					JspWriter out = getBodyContent().getEnclosingWriter();
					out.write("<script type='text/javascript'>");
					out.write(delayedScripts.toString());
					out.write("</script>");
				} catch (IOException e) {
					throw new JspTagException(e);
				}
			}
		} else {
			if (delayedScripts == null) {
				delayedScripts = new StringBuilder();
			}
			delayedScripts.append(getBodyContent().getString());
			pageContext.getRequest().setAttribute("delayedScripts", delayedScripts);
		}
		return SKIP_BODY;
	}
}
