package com.forumdeitroll.servlets;

import com.forumdeitroll.persistence.AuthorDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class Authors extends MainServlet {
	@Override
	String init(HttpServletRequest req, HttpServletResponse res) {
		return getAuthors(req, res);
	}

	// solo vista mobile
	@Action
	String getAuthors(HttpServletRequest req, HttpServletResponse res) {
		List<AuthorDTO> result = authorsDAO.getActiveAuthors();
		req.setAttribute("authors", result);
		setNavigationMessage(req, NavigationMessage.info("Autori"));
		return "authors.jsp";
	}
}
