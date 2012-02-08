package com.acmetoy.ravanator.fdt;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.IPersistence;
import com.acmetoy.ravanator.fdt.persistence.PersistenceFactory;
import com.acmetoy.ravanator.fdt.servlets.MainServlet;

public class PvtTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = Logger.getLogger(PersistenceFactory.class);
	private IPersistence persistence;
	
	private IPersistence getPersistence() {
		if (persistence == null) {
			try {
				persistence = PersistenceFactory.getInstance();
			} catch (Exception e) {
				LOG.error("Impossibile inizializzare la persistence in PvtTag", e);
				return null;
			}
		}
		return persistence;
	}
	
	@Override
	public int doEndTag() throws JspException {
		AuthorDTO author = (AuthorDTO) pageContext.getSession().getAttribute(MainServlet.LOGGED_USER_SESSION_ATTR);
		if (author != null) {
    		boolean hasPvts = getPersistence().checkForNewPvts(author);
    		try {
    			JspWriter out = pageContext.getOut();
    			if (hasPvts) {
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
