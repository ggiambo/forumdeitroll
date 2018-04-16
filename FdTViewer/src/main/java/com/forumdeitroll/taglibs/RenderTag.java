package com.forumdeitroll.taglibs;

import com.forumdeitroll.markup.RenderOptions;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.PrivateMsgDTO;
import com.forumdeitroll.servlets.MainServlet;
import com.forumdeitroll.servlets.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class RenderTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(RenderTag.class);
	private String target;
	public void setTarget(String target) {
		this.target = target;
	}
	@Override
	public int doEndTag() throws JspException {
		AuthorDTO loggedUser = (AuthorDTO) pageContext.getRequest().getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		String text = null;
		final RenderOptions opts = loggedUserRenderOptions(loggedUser);
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
		}
		try {
			String html = com.forumdeitroll.markup.Renderer.render(text, opts);
			pageContext.getOut().print(html);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			try {
				pageContext.getOut().print("Impossibile visualizzare il messaggio! Segnala questo post alla suora! E di corsa!");
			} catch (IOException e1) {
				throw new JspTagException(e);
			}
		}
		return SKIP_BODY;
	}

	public static String getMessagePreview(String body, AuthorDTO author, AuthorDTO loggedUser) throws Exception {
		final RenderOptions opts = loggedUserRenderOptions(loggedUser);
		opts.authorIsAnonymous = author != null && StringUtils.isEmpty(author.getNick());
		StringReader in = new StringReader(body);
		StringWriter out = new StringWriter();
		com.forumdeitroll.markup.Renderer.render(in, out, opts);
		return out.toString();
	}

	public static RenderOptions loggedUserRenderOptions(final AuthorDTO loggedUser) {
		final boolean luv = loggedUser != null && loggedUser.getNick() != null;
		final RenderOptions opts = new RenderOptions();
		opts.collapseQuotes =
			"checked".equals(luv ? loggedUser.getPreferences().get(User.PREF_COLLAPSE_QUOTES) : null);
		opts.embedYoutube =
			!StringUtils.isEmpty(luv ? loggedUser.getPreferences().get(User.PREF_EMBEDDYT) : "yes");
		opts.showImagesPlaceholder =
			StringUtils.isEmpty(luv ? loggedUser.getPreferences().get(User.PREF_SHOWANONIMG) : "yes");

		return opts;
	}

}
