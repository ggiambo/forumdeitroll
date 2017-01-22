package com.forumdeitroll.servlets;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.forumdeitroll.persistence.DigestArticleDTO;

public class ReadersDigest extends MainServlet {

	private static final long serialVersionUID = 1L;

	@Override
	@Action
	String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		List<DigestArticleDTO> articles = digestDAO.getReadersDigest();
		req.setAttribute("excludeSidebar", "true");
		req.setAttribute("articles", articles);
		setWebsiteTitlePrefix(req, "The Troll's Digest");
		return "readersdigest.jsp";
	}

}
