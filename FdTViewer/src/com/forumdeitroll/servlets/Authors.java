package com.forumdeitroll.servlets;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.forumdeitroll.persistence.AuthorDTO;

public class Authors extends MainServlet {
	@Override
	String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return getAuthors(req, res);
	}

	@Action
	String getAuthors(HttpServletRequest req, HttpServletResponse res) throws Exception {
		boolean onlyActive = true;
		String paramOnlyActive = req.getParameter("onlyActive");
		if (paramOnlyActive != null) {
			onlyActive = Boolean.parseBoolean(paramOnlyActive);
		}
		List<AuthorDTO> result = getPersistence().getAuthors(onlyActive);
		req.setAttribute("authors", result);
		return "authors.jsp";
	}
}
