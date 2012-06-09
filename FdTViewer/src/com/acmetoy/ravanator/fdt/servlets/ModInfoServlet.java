package com.acmetoy.ravanator.fdt.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.util.IPMemStorage;
import com.acmetoy.ravanator.fdt.util.CacheTorExitNodes;
import com.acmetoy.ravanator.fdt.util.ModInfo;

public class ModInfoServlet extends MainServlet {
	private static final long serialVersionUID = 1L;

	protected String show(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
		final String m_id = req.getParameter("m_id");

		setWebsiteTitle(req, "Moderazione " + m_id + " @ Forum dei Troll");

		final IPMemStorage.Record record = IPMemStorage.get(m_id);
		final ModInfo modInfo = new ModInfo(m_id, record);

		req.setAttribute("modInfo", modInfo);

		setAntiXssToken(req);
		return "modinfo.jsp";
	}

	@Action
	@Override
	String init(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
		final AuthorDTO loggedUser = login(req);

		final boolean isAdmin = (loggedUser != null) && ("yes".equals(getPersistence().getPreferences(loggedUser).get("super")));
		if (!isAdmin) {
			return "messages.jsp";
		}

		return show(req, res);
	}

	@Action
	String banUser(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
		final String m_id = req.getParameter("m_id");
		final AuthorDTO loggedUser = login(req);

		final boolean isAdmin = (loggedUser != null) && ("yes".equals(getPersistence().getPreferences(loggedUser).get("super")));
		if (!isAdmin) {
			return "messages.jsp";
		}

		if (!antiXssOk(req)) {
			req.setAttribute("comm", "Impossibile verificare token XSS");
			return show(req, res);
		}

		req.setAttribute("comm", "Non implementato");

		//TODO:
		// - bannare utente
		// - bannare sessione
		// -- sarrusofono 2012-06-09

		return show(req, res);
	}

	@Action
	String banIP(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
		final String m_id = req.getParameter("m_id");
		final AuthorDTO loggedUser = login(req);

		final boolean isAdmin = (loggedUser != null) && ("yes".equals(getPersistence().getPreferences(loggedUser).get("super")));
		if (!isAdmin) {
			return "messages.jsp";
		}

		if (!antiXssOk(req)) {
			req.setAttribute("comm", "Impossibile verificare token XSS");
			return show(req, res);
		}

		req.setAttribute("comm", "Non implementato");

		//TODO:
		// - bannare ip
		// -- sarrusofono 2012-06-09

		return show(req, res);
	}

	@Action
	String banMessage(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
		final String m_id = req.getParameter("m_id");
		final AuthorDTO loggedUser = login(req);

		final boolean isAdmin = (loggedUser != null) && ("yes".equals(getPersistence().getPreferences(loggedUser).get("super")));
		if (!isAdmin) {
			return "messages.jsp";
		}

		if (!antiXssOk(req)) {
			req.setAttribute("comm", "Impossibile verificare token XSS");
			return show(req, res);
		}

		req.setAttribute("comm", "Non implementato");

		//TODO:
		// - nascondere messaggio permanentemente
		// -- sarrusofono 2012-06-09

		return show(req, res);
	}
}
