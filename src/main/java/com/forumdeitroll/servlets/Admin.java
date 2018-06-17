package com.forumdeitroll.servlets;

import com.forumdeitroll.persistence.AuthorDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class Admin extends MainServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(Admin.class);

	public static final String ADMIN_PREF_BLOCK_TOR = "blockTorExitNodes";
	public static final String ADMIN_NON_ANON_POST = "adminNonAnonPost";
	public static final String ADMIN_WEBSITE_TITLES = "websiteTitles";

	@Override
	public void doBefore(HttpServletRequest req, HttpServletResponse res) {
		req.setAttribute(ADMIN_PREF_BLOCK_TOR, miscDAO.getSysinfoValue(ADMIN_PREF_BLOCK_TOR));
		req.setAttribute(ADMIN_NON_ANON_POST, miscDAO.getSysinfoValue(ADMIN_NON_ANON_POST));
		req.setAttribute(ADMIN_WEBSITE_TITLES, adminDAO.getTitles());
	}

	@Action
	String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = login(req);
		setWebsiteTitlePrefix(req, "");
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}

		if (!"yes".equals(loggedUser.getPreferences().get("super"))) {
			setNavigationMessage(req, NavigationMessage.warn("Non sei un admin"));
			return loginAction(req,  res);
		}

		return "prefs.jsp";

	}

	/**
	 * Mostra la pagina di login
	 */
	@Action
	String loginAction(HttpServletRequest req, HttpServletResponse res) throws Exception {
		setWebsiteTitlePrefix(req, "Login");
		res.sendRedirect("User");
		return null;
	}

	/**
	 * Cambia le preferences
	 */
	@Action
	String updatePreferences(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}

		if (!"yes".equals(loggedUser.getPreferences().get("super"))) {
			setNavigationMessage(req, NavigationMessage.warn("Non sei un admin"));
			return loginAction(req,  res);
		}

		String blockTorExitNodes = req.getParameter(ADMIN_PREF_BLOCK_TOR);
		if (!StringUtils.isEmpty(blockTorExitNodes)) {
			adminDAO.setSysinfoValue(ADMIN_PREF_BLOCK_TOR, "checked");
		} else {
			adminDAO.setSysinfoValue(ADMIN_PREF_BLOCK_TOR, "");
		}
		req.setAttribute(ADMIN_PREF_BLOCK_TOR, adminDAO.getSysinfoValue(ADMIN_PREF_BLOCK_TOR));

		String adminNonAnonPost = req.getParameter(ADMIN_NON_ANON_POST);
		if (!StringUtils.isEmpty(adminNonAnonPost)) {
			adminDAO.setSysinfoValue(ADMIN_NON_ANON_POST, "checked");
		} else {
			adminDAO.setSysinfoValue(ADMIN_NON_ANON_POST, "");
		}
		req.setAttribute(ADMIN_NON_ANON_POST, adminDAO.getSysinfoValue(ADMIN_NON_ANON_POST));

		String javascript = req.getParameter("javascript");
		if (javascript.length() > 255) {
			StringBuilder errMsg = new StringBuilder("javascript troppo lungo: ");
			errMsg.append(javascript.length()).append(" caratteri, max 255");
			setNavigationMessage(req, NavigationMessage.warn(errMsg.toString()));
		} else {
			adminDAO.setSysinfoValue("javascript", javascript);
		}
		req.setAttribute("javascript", javascript);

		String[] websiteTitles = req.getParameterValues(ADMIN_WEBSITE_TITLES);
		List<String> titles = new ArrayList<>();
		if (websiteTitles != null) {
			for (String title : websiteTitles) {
				if (StringUtils.isNotEmpty(title)) {
					titles.add(title);
				}
			}
			adminDAO.setTitles(titles);
			cachedTitles.invalidate();
		}
		req.setAttribute(ADMIN_WEBSITE_TITLES, titles);

		return "prefs.jsp";
	}

}
