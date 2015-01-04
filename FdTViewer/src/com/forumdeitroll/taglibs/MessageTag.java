package com.forumdeitroll.taglibs;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.forumdeitroll.markup.RenderOptions;
import com.forumdeitroll.markup.Renderer;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.servlets.MainServlet;
import com.forumdeitroll.servlets.User;

@Deprecated
public class MessageTag extends BodyTagSupport {
	private static final long serialVersionUID = -4382505626768797422L;
	private static final Logger LOG = Logger.getLogger(MessageTag.class);

	// ----- BodyTagSupport -----
	private String search;
	private AuthorDTO author;
	private String signature;
	private boolean alt = false;
	public void setSearch(String search) {
		this.search = search;
	}
	public String getSearch() {
		return search;
	}
	public void setAuthor(AuthorDTO author) {
		this.author = author;
	}
	public AuthorDTO getAuthor() {
		return author;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public void setAlt(String alt) {
		if (alt != "") {
			this.alt = true;
		}
	}
	public boolean isRenderingSignature() {
		return Boolean.parseBoolean(getSignature());
	}
	
	public int doAfterBody() throws JspTagException {
		try {
			AuthorDTO loggedUser = (AuthorDTO) pageContext.getRequest().getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
			RenderOptions opts = new RenderOptions();
			opts.renderImages = ! isRenderingSignature();
			opts.renderYoutube = ! isRenderingSignature(); // renderImages e renderYoutube sono la stessa cosa?? wtf?
			opts.collapseQuotes =
				! isRenderingSignature() &&
				"checked".equals(
					loggedUser != null
						? loggedUser.getPreferences().get(User.PREF_COLLAPSE_QUOTES)
						: null);
			opts.embedYoutube =
				! StringUtils.isEmpty(
					loggedUser != null
						? loggedUser.getPreferences().get(User.PREF_EMBEDDYT)
						: "yes");
			opts.showImagesPlaceholder =
				StringUtils.isEmpty(
					loggedUser != null
						? loggedUser.getPreferences().get(User.PREF_SHOWANONIMG)
						: "yes");
			opts.authorIsAnonymous =
				author != null && StringUtils.isEmpty(author.getNick()); // se author e` null allora non e` anonymous?? wtf?
			if (alt) {
				com.forumdeitroll.markup2.Renderer.render(getBodyContent().getString(), getBodyContent().getEnclosingWriter(), opts);
			} else {
				Renderer.render(
					getBodyContent().getReader(),
					getBodyContent().getEnclosingWriter(),
					opts);
			}
		} catch (Exception e) {
			LOG.error("Errore durante il rendering del post "+e.getMessage(), e);
			LOG.error("BODY:\n"+getBodyContent().getString());
			try {
				getBodyContent().getEnclosingWriter().print("Impossibile visualizzare il messaggio! Segnala questo post alla suora! E di corsa!");
			} catch (IOException e1) {
				throw new JspTagException(e);
			}
		}
		return SKIP_BODY;
	}

	public static String getMessagePreview(String body, AuthorDTO author, AuthorDTO loggedUser) throws Exception {
		RenderOptions opts = new RenderOptions();
		opts.collapseQuotes =
			"checked".equals(
				loggedUser != null && loggedUser.getNick() != null
					? loggedUser.getPreferences().get(User.PREF_COLLAPSE_QUOTES)
					: null);
		opts.embedYoutube =
			! StringUtils.isEmpty(
				loggedUser != null && loggedUser.getNick() != null
					? loggedUser.getPreferences().get(User.PREF_EMBEDDYT)
					: "yes");
		opts.showImagesPlaceholder =
			StringUtils.isEmpty(
				loggedUser != null && loggedUser.getNick() != null
					? loggedUser.getPreferences().get(User.PREF_SHOWANONIMG)
					: "yes");
		opts.authorIsAnonymous =
			author != null && StringUtils.isEmpty(author.getNick());
		StringReader in = new StringReader(body);
		StringWriter out = new StringWriter();
		com.forumdeitroll.markup3.Renderer.render(in, out, opts);
		return out.toString();
	}
}
