package com.forumdeitroll.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class MainFilter implements Filter {

	@Override
	public void init(FilterConfig config) {
	}

	/**
	 * Inizializzazioni di base, per tutti i servlets tranne "Misc"
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;

		// actual time
		req.setAttribute("currentTimeMillis", System.currentTimeMillis());

		// I love UTF-8
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");

		HttpSession session = req.getSession();
		if ("updateMobileViewFromDisclaimer".equals(req.getParameter("action"))) {
			session.setAttribute("mobileView", "true");
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
