package com.acmetoy.ravanator.fdt.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Messages extends MainServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	public String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return getByPage(req, res);
	}

	/**
	 * I messaggi di questa pagina (Dimensione PAGE_SIZE) in ordine di data
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String getByPage(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setAttribute("messages", getPersistence().getMessagesByDate(PAGE_SIZE, getPageNr(req)));
		setNavigationMessage(req, "Ordinati cronologicamente");
		return "messages.jsp";
	}
	
	/**
	 * I messaggi di questo autore in ordine di data
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String getByAuthor(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String author = req.getParameter("author");
		req.setAttribute("specificParams", "&author=" + author);
		setNavigationMessage(req, "Messaggi scritti da <i>" + author + "</i>");
		req.setAttribute("messages", getPersistence().getMessagesByAuthor(author, PAGE_SIZE, getPageNr(req)));
		return "messages.jsp";
	}
	
	public String getByForum(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String forum = req.getParameter("forum");
		req.setAttribute("specificParams", "&forum=" + forum);
		setNavigationMessage(req, "Forum <i>" + forum + "</i>");
		req.setAttribute("messages", getPersistence().getMessagesByForum(forum, PAGE_SIZE, getPageNr(req)));
		return "messages.jsp";
	}
	
	/**
	 * Ricerca in tutti i messaggi
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String search(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String search = req.getParameter("search");
		req.setAttribute("specificParams", "&search=" + search);
		req.setAttribute("messages", getPersistence().searchMessages(search, PAGE_SIZE, getPageNr(req)));
		return "messages.jsp";
	}

}