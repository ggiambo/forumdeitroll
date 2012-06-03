package com.acmetoy.ravanator.fdt.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

public class MainFilter implements Filter {
	
	private ServletContext servletContext;

	@Override
	public void init(FilterConfig config) throws ServletException {
		servletContext = config.getServletContext();
	}
	
	/**
	 * Inizializzazioni di base, per tutti i servlets tranne "Misc"
	 * @param req
	 * @param res
	 * @param chain
	 * @throws IOException
	 * @throws ServletException
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;

		// actual time
		req.setAttribute("currentTimeMillis", System.currentTimeMillis());
		
		// I love UTF-8
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		
		// mostrare il disclaimer ?
		if (req.getSession(false) == null) {
			if (!"OK".equals(req.getParameter("disclaimer"))) {
				String originalURL = req.getRequestURI();
				if (req.getQueryString() != null) {
					originalURL += "?" + req.getQueryString();
				}
				req.setAttribute("originalURL", originalURL);
				servletContext.getRequestDispatcher("/pages/disclaimer.jsp").forward(req, res);
			} else {
				// l'utente ha clickato su "OK": creiamo la sessione e rimandiamo alla pagina voluta
				final HttpSession session = req.getSession();
				if (session.isNew()) {
					String id = session.getId();
					res.setHeader("Set-Cookie", String.format("JSESSIONID=%s;Max-Age=%d;Path=/", id, 365*24*60*60));
				}
				String originalURL = req.getParameter("originalURL");
				if (StringUtils.isEmpty(originalURL)) {
					originalURL = "Messages";
				}
				res.setStatus(302);
				res.setHeader("Location", originalURL);
				res.setHeader("Connection", "close" );
			}
			return;
		}
		
		// i vecchi errori del passato ...
		try {
			req.getSession().removeAttribute("loggedUser");
		} catch (Exception e) {
			// nothing to do here, move along ...
		}
		
		String action = req.getParameter("action");
		if (action == null || action.trim().length() == 0) {
			action = "init";
		}
		req.setAttribute("action", action);

		chain.doFilter(req, res);
	}
	
	@Override
	public void destroy() {
	}
	
}
