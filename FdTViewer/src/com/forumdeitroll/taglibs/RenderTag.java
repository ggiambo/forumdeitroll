package com.forumdeitroll.taglibs;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.forumdeitroll.markup.RenderOptions;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.DigestArticleDTO;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.PrivateMsgDTO;
import com.forumdeitroll.servlets.MainServlet;
import com.forumdeitroll.servlets.User;

public class RenderTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(RenderTag.class);
	private String target;
	protected static final boolean alt = false;
	public void setTarget(String target) {
		this.target = target;
	}
	@Override
	public int doEndTag() throws JspException {
		AuthorDTO loggedUser = (AuthorDTO) pageContext.getRequest().getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		String text = null;
		RenderOptions opts = new RenderOptions();
		opts.collapseQuotes =
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
		if (target.equals("message")) {
			MessageDTO message = (MessageDTO) pageContext.findAttribute("message");
			//LOG.debug("rendering message " + message.getId());
			text = message.getText();
			opts.authorIsAnonymous = message.getAuthor() == null || message.getAuthor().getNick() == null;
		} else if (target.equals("privateMessage")) {
			PrivateMsgDTO pvt = (PrivateMsgDTO) pageContext.findAttribute("privateMessage");
			//LOG.debug("rendering private message" + pvt.getId());
			text = pvt.getText();
			opts.authorIsAnonymous = false;
		} else if (target.equals("signature")) {
			MessageDTO referencedMessage = (MessageDTO) pageContext.findAttribute("message");
			//LOG.debug("rendering signature for author " + referencedMessage.getAuthor());
			text = referencedMessage.getAuthor().getPreferences().get("signature");
			opts.authorIsAnonymous = false;
			opts.renderImages = false;
			opts.renderYoutube = false;
			opts.collapseQuotes = false;
		} else if (target.equals("articleOpener")) {
			DigestArticleDTO articleDTO = (DigestArticleDTO) pageContext.findAttribute("article");
			text = articleDTO.getOpenerText();
			opts.authorIsAnonymous = StringUtils.isEmpty(articleDTO.getAuthor());
		} else if (target.equals("articleExcerpt")) {
			DigestArticleDTO articleDTO = (DigestArticleDTO) pageContext.findAttribute("article");
			text = articleDTO.getExcerpt();
			opts.authorIsAnonymous = true;// nel dubbio si`
		}
		try {
			String html;
			if (alt) {
				StringWriter sw = new StringWriter();
				com.forumdeitroll.markup2.Renderer.render(text, sw, opts);
				html = sw.toString();
			} else {
				html = com.forumdeitroll.markup3.Renderer.render(text, opts);
			}
			if (pageContext.getRequest().getParameter("compareRendering") != null) {
				StringWriter sw = new StringWriter();
				sw.write(html);
				sw.write("<BR>--- R1 ---<BR>");
				com.forumdeitroll.markup.Renderer.render(new StringReader(text), sw, opts);
				sw.write("<BR>--- RS ---<BR>");
				com.forumdeitroll.markup2.Renderer.render(text, sw, opts);
				html = sw.toString();
			}
			pageContext.getOut().print(html);
		} catch (Exception e) {
			LOG.error(e);
			try {
				pageContext.getOut().print("Impossibile visualizzare il messaggio! Segnala questo post alla suora! E di corsa!");
			} catch (IOException e1) {
				throw new JspTagException(e);
			}
		}
		return SKIP_BODY;
	}
}
