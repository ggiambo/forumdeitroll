package com.forumdeitroll.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MainFilter implements Filter {

	@Override
	public void init(FilterConfig config) {
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

		HttpSession session = req.getSession();
		if ("updateMobileViewFromDisclaimer".equals(req.getParameter("action"))) {
			session.setAttribute("mobileView", "true");
		}
		res.setStatus(302);

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
