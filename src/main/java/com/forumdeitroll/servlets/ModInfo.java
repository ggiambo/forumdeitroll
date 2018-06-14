package com.forumdeitroll.servlets;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.DAOFactory;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.util.IPMemStorage;
import com.forumdeitroll.util.ModInfoBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModInfo extends MainServlet {
	private static final long serialVersionUID = 1L;

	protected String show(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
		final String m_id = req.getParameter("m_id");

		setWebsiteTitlePrefix(req, "Moderazione " + m_id);

		final IPMemStorage.Record record = IPMemStorage.get(m_id);
		final ModInfoBean modInfo = new ModInfoBean(m_id, record);
		final MessageDTO msg = messagesDAO.getMessage(Long.parseLong(m_id));
		msg.modInfoException();

		req.setAttribute("modInfo", modInfo);
		req.setAttribute("msg", msg);

		setAntiXssToken(req);
		return "modinfo.jsp";
	}

	@Action
	@Override
	String init(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
		final AuthorDTO loggedUser = login(req);

		final boolean isAdmin = (loggedUser != null) && ("yes".equals(messagesDAO.getPreferences(loggedUser).get("super")));
		if (!isAdmin) {
			return "messages.jsp";
		}

		return show(req, res);
	}

	@Action
	String banUser(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
		final String m_id = req.getParameter("m_id");
		final AuthorDTO loggedUser = login(req);

		final boolean isAdmin = (loggedUser != null) && ("yes".equals(messagesDAO.getPreferences(loggedUser).get("super")));
		if (!isAdmin) {
			return "messages.jsp";
		}

		if (!antiXssOk(req)) {
			setNavigationMessage(req,NavigationMessage.error("Impossibile verificare token XSS"));
			return show(req, res);
		}

		String nickname = null;

		final IPMemStorage.Record record = IPMemStorage.get(m_id);
		if (record != null) {
			nickname = record.authorNickname();
		} else {
			try {
				final MessageDTO msg = messagesDAO.getMessage(Long.parseLong(m_id));
				if (msg != null) {
					final AuthorDTO msgAuthor = msg.getAuthor();
					if ((msgAuthor != null) && msgAuthor.isValid()) {
						nickname = msgAuthor.getNick();
					}
				}
			} catch (NumberFormatException e) {
				/* soppressa, qualcuno sta facendo qualcosa di strano ma irrilevante */
			}
		}

		if ((nickname == null) || nickname.equals("Non Autenticato")) {
			setNavigationMessage(req,NavigationMessage.error("Fallito: impossibile bannare 'Non Autenticato' (o impossibile risalire all'utente che ha creato questo post)"));
			return show(req, res);
		}

		final AuthorDTO target = messagesDAO.getAuthor(nickname);

		if (target == null) {
			setNavigationMessage(req,NavigationMessage.error("Fallito: impossibile trovare l'utente <" + nickname + ">"));
			return show(req, res);
		}

		authorsDAO.updateAuthorPassword(target, null);

		return show(req, res);
	}

	@Action
	String banIP(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
		final String m_id = req.getParameter("m_id");
		final AuthorDTO loggedUser = login(req);

		final boolean isAdmin = (loggedUser != null) && ("yes".equals(messagesDAO.getPreferences(loggedUser).get("super")));
		if (!isAdmin) {
			return "messages.jsp";
		}

		if (!antiXssOk(req)) {
			setNavigationMessage(req,NavigationMessage.error("Impossibile verificare token XSS"));
			return show(req, res);
		}

		final IPMemStorage.Record record = IPMemStorage.get(m_id);

		if (record != null) {
			Messages.banIP(record.ip());
			setNavigationMessage(req,NavigationMessage.info("Ok"));
		} else {
			setNavigationMessage(req,NavigationMessage.error("Non riuscito"));
		}

		return show(req, res);
	}

	@Action
	String banMessage(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
		final String m_id = req.getParameter("m_id");
		final AuthorDTO loggedUser = login(req);

		final boolean isAdmin = (loggedUser != null) && ("yes".equals(messagesDAO.getPreferences(loggedUser).get("super")));
		if (!isAdmin) {
			return "messages.jsp";
		}

		if (!antiXssOk(req)) {
			setNavigationMessage(req,NavigationMessage.error("Impossibile verificare token XSS"));
			return show(req, res);
		}

		try {
			final long id = Long.parseLong(m_id);
			MessageDTO rootMessage = messagesDAO.getMessage(id);
			adminDAO.moveThreadTree(rootMessage, DAOFactory.FORUM_ASHES);
			adminDAO.restoreOrHideMessage(id, -1);
		} catch (NumberFormatException e) {
			setNavigationMessage(req, NavigationMessage.error("Errore durante l'esecuzione della richiesta: " + e.getMessage()));
			return show(req, res);
		}



		return show(req, res);
	}
}
