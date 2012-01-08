package com.acmetoy.ravanator.fdt.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.io.Writer;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.IPersistence;
import com.acmetoy.ravanator.fdt.persistence.PersistenceFactory;

public abstract class MainServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected static final int PAGE_SIZE = 15;

	protected static final String LOGGED_USER_SESSION_ATTR = "loggedUser";

	private byte[] notAuthenticated;
	private byte[] noAvatar;

	private IPersistence persistence;

	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);

		try {
			// anonimo
			InputStream is = config.getServletContext().getResourceAsStream("/images/avataranonimo.gif");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int count = -1;
			while ((count = is.read(buffer)) != -1) {
				bos.write(buffer, 0, count);
			}
			notAuthenticated = bos.toByteArray();

			// default
			is = config.getServletContext().getResourceAsStream("/images/avatardefault.gif");
			bos = new ByteArrayOutputStream();
			buffer = new byte[1024];
			count = -1;
			while ((count = is.read(buffer)) != -1) {
				bos.write(buffer, 0, count);
			}
			noAvatar = bos.toByteArray();

			//
			config.getServletContext().setAttribute("PAGE_SIZE", PAGE_SIZE);

		} catch (IOException e) {
			throw new ServletException("Cannot read default images", e);
		}

		try {
			persistence = PersistenceFactory.getInstance();
		} catch (Exception e) {
			throw new ServletException("Cannot instantiate persistence", e);
		}
	}

	/**
	 * Metodo di default, quando nessuna "action" e' definita.
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public abstract String init(HttpServletRequest req, HttpServletResponse res) throws Exception;

	public final void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		doPost(req, res);
	}

	public final void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

		// actual time
		req.setAttribute("currentTimeMillis", System.currentTimeMillis());

		// forums
		req.setAttribute("forums", getPersistence().getForums());

		// this context
		req.setAttribute("contextPath", req.getRequestURL());

		// action
		String action = req.getParameter("action");
		if (action == null || action.trim().length() == 0) {
			action = "init";
		}
		req.setAttribute("action", action);

		try {
			// call via reflection
			Method method = this.getClass().getMethod(action, HttpServletRequest.class, HttpServletResponse.class);
			String pageForward = (String)method.invoke(this, req, res);
			// forward
			if (pageForward != null) {
				getServletContext().getRequestDispatcher("/pages/" + pageForward).forward(req, res);
			}

		} catch (Exception e) {
			req.setAttribute("exceptionStackTrace", ExceptionUtils.getStackTrace(e));
			e.printStackTrace(System.err);
			try {
				getServletContext().getRequestDispatcher("/pages/error.jsp").forward(req, res);
			} catch (ServletException e1) {
				for (StackTraceElement elem : e1.getStackTrace()) {
					res.getWriter().write(elem + "<br/>");
				}
			}
		}
	}

	/**
	 * La persistence inizializzata
	 * @return
	 */
	protected final IPersistence getPersistence() {
		return persistence;
	}

	/**
	 * Scrive direttamente nella response i bytes che compongono l'avatar
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String getAvatar(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String nick = req.getParameter("nick");
		AuthorDTO author = getPersistence().getAuthor(nick);
		if (author.isValid()) {
			if (author.getAvatar() != null) {
				res.getOutputStream().write(author.getAvatar());
			} else {
				res.getOutputStream().write(noAvatar);
			}
		} else {
			res.getOutputStream().write(notAuthenticated);
		}
		return null;
	}

	/**
	Richiesta logout
	*/
	public String logoutAction(HttpServletRequest req, HttpServletResponse res) throws IOException, NoSuchAlgorithmException {
		final String sesNick = (String)req.getSession().getAttribute(LOGGED_USER_SESSION_ATTR);
		final String nick = req.getParameter("nick");
		final String pass = req.getParameter("pass");
		if (!StringUtils.isEmpty(nick) && !StringUtils.isEmpty(pass) && !StringUtils.isEmpty(sesNick)) {
			final AuthorDTO author = getPersistence().getAuthor(nick, md5(pass));

			if (sesNick.equals(author.getNick())) {
				res.setContentType("text/plain");
				res.setStatus(200);
				final Writer w = res.getWriter();
				w.write("ok");
				w.flush();
				w.close();
				return null;
			}
		}

		res.setContentType("test/plain");
		res.setStatus(500);
		final Writer w = res.getWriter();
		w.write("fail");
		w.flush();
		w.close();

		return null;
	}

	/**
	 * Setta nella session lo stato della sidebar (Aperta/chiusa)
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String sidebarStatus(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String sidebarStatus = req.getParameter("sidebarStatus");
		// get sidebar status
		if (sidebarStatus == null || sidebarStatus.trim().length() == 0) {
			sidebarStatus = (String) req.getSession().getAttribute("sidebarStatus");
			if (sidebarStatus == null || sidebarStatus.trim().length() == 0) {
				// get from cookie
				if (req.getCookies() != null) {
					for (Cookie cookie : req.getCookies()) {
						if ("sidebarStatus".equals(cookie.getName())) {
							sidebarStatus = cookie.getValue();
						}
					}
				}
				if (sidebarStatus == null || sidebarStatus.trim().length() == 0) {
    				// default: show
    				sidebarStatus = "show";
				}
			}
		}
		req.getSession().setAttribute("sidebarStatus", sidebarStatus);
		// set in cookie
		boolean found = false;
		for (Cookie c : req.getCookies()) {
			if ("sidebarStatus".equals(c.getName())) {
				c.setValue(sidebarStatus);
				found = true;
			}
		}
		if (!found) {
			Cookie cookie = new Cookie("sidebarStatus", sidebarStatus);
			res.addCookie(cookie);
		}
		res.getWriter().write(sidebarStatus);
		res.flushBuffer();
		return null;
	}

	/**
	 * Ritorna la pagina attuale se definita come req param, altrimenti 0. Setta il valore come req attr.
	 * @param req
	 * @return
	 */
	protected int getPageNr(HttpServletRequest req) {
		// pageNr
		String pageNr = req.getParameter("pageNr");
		if (pageNr == null) {
			pageNr = "0";
		}
		req.setAttribute("pageNr", pageNr);
		return Integer.parseInt(pageNr);
	}

	/**
	 * Messaggio mostrato tra l'header e la navigazione
	 * @param req
	 * @param navigationMessage
	 */
	protected void setNavigationMessage(HttpServletRequest req, String navigationMessage) {
		req.setAttribute("navigationMessage", navigationMessage);
	}

	protected String md5(String input) throws NoSuchAlgorithmException {
		String result = input;
		if (input != null) {
			MessageDigest md = MessageDigest.getInstance("MD5"); // or "SHA-1"
			md.update(input.getBytes());
			BigInteger hash = new BigInteger(1, md.digest());
			result = hash.toString(16);
			while (result.length() < 32) {
				result = "0" + result;
			}
		}
		return result;
	}
}