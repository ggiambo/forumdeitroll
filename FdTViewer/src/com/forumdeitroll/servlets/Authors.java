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

	// solo vista mobile
	@Action
	String getAuthors(HttpServletRequest req, HttpServletResponse res) throws Exception {
		List<AuthorDTO> result = authorsDAO.getActiveAuthors();
		req.setAttribute("authors", result);
		setNavigationMessage(req, NavigationMessage.info("Autori"));
		return "authors.jsp";
	}
}
