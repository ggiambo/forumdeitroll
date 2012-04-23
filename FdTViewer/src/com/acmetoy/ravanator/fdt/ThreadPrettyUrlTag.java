package com.acmetoy.ravanator.fdt;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

public class ThreadPrettyUrlTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(ThreadPrettyUrlTag.class);
	private String threadId;
	private String subject;
	private String msgId;

	private static String prettifySubject(String subject) {
		StringBuilder out = new StringBuilder();
		for (char c : subject.toCharArray()) {
			if ((isAlphabetic(c)) || (Character.isDigit(c)))
				out.append(c);
			else {
				out.append('-');
			}
		}
		return out.toString();
	}

	public int doEndTag() throws JspException {
		try {
			String prettySubject = prettifySubject(this.subject);
			if (prettySubject.equals("Messages") || prettySubject.equals("Threads")) {
				prettySubject += "-";
			}
			JspWriter out = this.pageContext.getOut();
			out.write("<a href=\"");
			if (!pageContext.getServletContext().getContextPath().equals("/FdTViewer")) {
				// solo nella conf di test
				out.write(this.pageContext.getServletContext().getContextPath());
			}
			/*
			out.write("/thread/");
			out.write(this.threadId);
			out.write("/");
			out.write(prettySubject);
			
			*/
			out.write("/Threads?action=getByThread&amp;threadId=");
			out.write(this.threadId);
			
			out.write("#msg");
			out.write(this.msgId);
			out.write("\">");
			out.write(this.subject);
			out.write("</a>");
			out.flush();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0;
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

	public String getThreadId() {
		return this.threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMsgId() {
		return this.msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
}
