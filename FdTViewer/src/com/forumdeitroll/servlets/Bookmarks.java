package com.forumdeitroll.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.BookmarkDTO;
import com.forumdeitroll.servlets.Action.Method;

public class Bookmarks extends MainServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	@Action(method=Method.GET)
	String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return list(req, res);
	}
	
	@Action(method=Method.GET)
	String list(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = login(req);
		if (loggedUser == null || !loggedUser.isValid()) return null;
		req.setAttribute("bookmarks", getPersistence().getBookmarks(loggedUser));
		return "bookmarks.jsp";
	}
	
	@Action(method=Method.GET)
	String add(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = login(req);
		if (loggedUser == null || !loggedUser.isValid()) return null;
		Long msgId = Long.parseLong(req.getParameter("msgId"));
		BookmarkDTO bookmark = new BookmarkDTO();
		bookmark.setNick(loggedUser.getNick());
		bookmark.setMsgId(msgId);
		if (getPersistence().existsBookmark(bookmark)) {
			setNavigationMessage(req, NavigationMessage.warn("Il messaggio &egrave; gi&agrave; tra i tuoi segnalibri."));
			req.setAttribute("highlight", msgId);
		} else {
			req.setAttribute("msgId", msgId);
			req.setAttribute("subject", getPersistence().getMessage(msgId).getSubject());	
		}
		return list(req, res);
	}
	
	@Action(method=Method.POST)
	String confirmAdd(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = login(req);
		if (loggedUser == null || !loggedUser.isValid()) return null;
		String msgId = req.getParameter("msgId");
		String subject = req.getParameter("subject");
		BookmarkDTO bookmark = new BookmarkDTO();
		bookmark.setNick(loggedUser.getNick());
		bookmark.setMsgId(Long.parseLong(msgId));
		bookmark.setSubject(
			subject.length() > Messages.MAX_SUBJECT_LENGTH
			? subject.substring(0, Messages.MAX_SUBJECT_LENGTH)
			: subject);
		getPersistence().addBookmark(bookmark);
		return list(req, res);
	}
	
	@Action(method=Method.POST)
	String delete(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = login(req);
		if (loggedUser == null || !loggedUser.isValid()) return null;
		String msgId = req.getParameter("msgId");
		BookmarkDTO bookmark = new BookmarkDTO();
		bookmark.setNick(loggedUser.getNick());
		bookmark.setMsgId(Long.parseLong(msgId));
		getPersistence().deleteBookmark(bookmark);
		return list(req, res);
	}
	
	@Action(method=Method.POST)
	String edit(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = login(req);
		if (loggedUser == null || !loggedUser.isValid()) return null;
		String msgId = req.getParameter("msgId");
		String subject = req.getParameter("subject");
		BookmarkDTO bookmark = new BookmarkDTO();
		bookmark.setNick(loggedUser.getNick());
		bookmark.setMsgId(Long.parseLong(msgId));
		bookmark.setSubject(
				subject.length() > Messages.MAX_SUBJECT_LENGTH
				? subject.substring(0, Messages.MAX_SUBJECT_LENGTH)
				: subject);
		getPersistence().editBookmark(bookmark);
		return list(req,res);
	}
}
