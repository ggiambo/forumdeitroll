package com.acmetoy.ravanator.fdt.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.PersistenceFactory;

public abstract class MainServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected static final int PAGE_SIZE = 15;
	
	private byte[] notAuthenticated;
	private byte[] noAvatar;

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
				getServletContext().getRequestDispatcher("/WEB-INF/pages/" + pageForward).forward(req, res);
			}

		} catch (Exception e) {
			res.getWriter().write(e.toString());
		}
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
		AuthorDTO author = PersistenceFactory.getPersistence().getAuthor(nick);
		if (author != null) {
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
	
	protected int getPageNr(HttpServletRequest req) {
		// pageNr
		String pageNr = req.getParameter("pageNr");
		if (pageNr == null) {
			pageNr = "0";
		}
		req.setAttribute("pageNr", pageNr);
		return Integer.parseInt(pageNr);
	}

}