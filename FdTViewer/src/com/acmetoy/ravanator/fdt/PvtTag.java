package com.acmetoy.ravanator.fdt;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.IPersistence;
import com.acmetoy.ravanator.fdt.persistence.PersistenceFactory;

public class PvtTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see com.acmetoy.ravanator.fdt.servlets.MainServlet#LOGGED_USER_SESSION_ATTR
	 */
	private String LOGGED_USER_SESSION_ATTR = "loggedUser";
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
		 String nick = (String) pageContext.getSession().getAttribute(LOGGED_USER_SESSION_ATTR);
		 if (getPersistence() != null) {
			 AuthorDTO author = new AuthorDTO();
			 author.setNick(nick);
			 boolean hasPvts = getPersistence().checkForNewPvts(author);
			 try {
				 JspWriter out = pageContext.getOut();
				if (hasPvts) {
					out.write("<a href='Pvt?action=inbox'><img src='images/icona_pibox_a.gif'></a>");
				} else {
					out.write("<a href='Pvt?action=inbox'><img src='images/icona_pibox.png'></a>");
				}
				out.flush();
			} catch (IOException e) {
				throw new JspException(e);
			}
		 }
		return SKIP_BODY;
	}
}
