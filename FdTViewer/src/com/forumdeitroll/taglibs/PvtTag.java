package com.forumdeitroll.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.servlets.MainServlet;

public class PvtTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public int doEndTag() throws JspException {
		AuthorDTO author = (AuthorDTO)pageContext.getRequest().getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (author != null) {
			Boolean hasPvts = (Boolean)pageContext.getRequest().getAttribute("hasPvts");
    		try {
    			JspWriter out = pageContext.getOut();
    			if (hasPvts != null && hasPvts) {
    				out.write("<a href='Pvt?action=inbox' class='pvt' title='Nuovi messaggi!'><img src='images/icona_pibox_a.gif' alt='Nuovi Messaggi Privati' /></a>");
    			} else {
    				out.write("<a href='Pvt?action=inbox' class='pvt' title='Nessun nuovo messaggio'><img src='images/icona_pibox.png' alt='Messaggi Privati' /></a>");
    			}
    			out.flush();
    		} catch (IOException e) {
    			throw new JspException(e);
    		}
		}
		return SKIP_BODY;
	}
}
