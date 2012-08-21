package com.forumdeitroll.servlets;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.forumdeitroll.RandomPool;
import com.forumdeitroll.ThreadTree;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.IPersistence;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.ThreadDTO;
import com.forumdeitroll.persistence.ThreadsDTO;
import com.forumdeitroll.servlets.Action.Method;

public class Threads extends MainServlet {

	private static final long serialVersionUID = 1L;

	public static final String ANTI_XSS_TOKEN = "anti_xss_token";

	/**
	 * Ordinati per thread / data iniziale
	 */
	@Action
	@Override
	String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// redirect
		res.setHeader("Location", "Threads?action=getThreads");
		res.sendError(301);
		return null;
	}
	
	/**
	 * Recupera la discussione a partire da un messaggio in essa contenuto
	 * Usato in conversione di url da .it a .com
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action
	String getByMessage(HttpServletRequest req, HttpServletResponse res) throws Exception {
		long msgId = Long.parseLong(req.getParameter("msgId"));
		long threadId = getPersistence().getMessage(msgId).getThreadId();
		res.setHeader("Location", "Threads?action=getByThread&threadId=" + threadId + "#msg" + msgId);
		res.sendError(302);
		return null;
	}
	
	/**
	 * Tutti i messaggi di questo thread
	 */
	@Action
	String getByThread(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String stringThreadId = req.getParameter("threadId");
		if (StringUtils.isEmpty(stringThreadId)) {
			return init(req, res);
		}
		String forum = req.getParameter("forum");
		addSpecificParam(req, "forum",  forum);
		Long threadId = Long.parseLong(stringThreadId);
		List<MessageDTO> msgs = getPersistence().getMessagesByThread(threadId);
		req.setAttribute("root", new ThreadTree(msgs).getRoot());
		setWebsiteTitle(req, getPersistence().getMessage(threadId).getSubject() + " @ Forum dei Troll");
		setNavigationMessage(req, NavigationMessage.info("Thread <i>" + getPersistence().getMessage(threadId).getSubject() + "</i>"));

		req.getSession().setAttribute(ANTI_XSS_TOKEN, RandomPool.getString(3));

		return "thread.jsp";
	}

	/**
	 * Chiamato via ajax, apre il thread tree
	 */
	@Action
	String openThreadTree(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Long threadId = Long.parseLong(req.getParameter("threadId"));
		List<MessageDTO> msgs = getPersistence().getMessagesByThread(threadId);
		req.setAttribute("msg", new ThreadTree(msgs).getRoot());

		getServletContext().getRequestDispatcher("/pages/threads/incThreadTree.jsp").forward(req, res);
		return null;
	}
	
	/**
	 * Ordinati per thread / data iniziale
	  Se il parametro forum non e` presente restituisce i thread di tutti i forum, se e` presente ma contiene la stringa vuota restituisce i thread del forum principale, altrimenti restituisce i thread del forum specificato
	 */
	@Action
	String getThreads(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return getThreads(req, res, NavigationMessage.info("Nuove discussioni"));
	}

	/**
	 * Ordinati per thread / ultimo post
	 */
	@Action
	String getThreadsByLastPost(HttpServletRequest req, HttpServletResponse res) throws Exception {
		boolean hideProcCatania = StringUtils.isNotEmpty(login(req).getPreferences().get(User.PREF_HIDE_PROC_CATANIA));
		String forum = req.getParameter("forum");
		ThreadsDTO messages = getPersistence().getThreadsByLastPost(forum, PAGE_SIZE, getPageNr(req), hideProcCatania);
		req.setAttribute("messages", messages.getMessages());
		req.setAttribute("totalSize", messages.getMaxNrOfMessages());
		req.setAttribute("resultSize", messages.getMessages().size());
		addSpecificParam(req, "forum",  forum);
		if (forum == null) {
			setWebsiteTitle(req, "Forum dei troll");
		} else {
			setWebsiteTitle(req, forum.equals("") ? "Forum principale @ Forum dei troll" : (forum + " @ Forum dei troll"));
		}
		setNavigationMessage(req, NavigationMessage.info("Discussioni aggiornate"));
		req.getSession().setAttribute(ANTI_XSS_TOKEN, RandomPool.getString(3));
		return "threadsByLastPost.jsp";
	}

	/**
	 * Tutti i threads dell'utente loggato, ordinati per ultimo post
	 */
	@Action(method=Method.GET)
	String getAuthorThreadsByLastPost(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
		AuthorDTO author = login(req);
		if (!author.isValid()) {
			throw new Exception("Furmigamento detected !");
		}
		boolean hideProcCatania = StringUtils.isNotEmpty(login(req).getPreferences().get(User.PREF_HIDE_PROC_CATANIA));
		List<ThreadDTO> messages = getPersistence().getAuthorThreadsByLastPost(author.getNick(), PAGE_SIZE, getPageNr(req), hideProcCatania);
		req.setAttribute("messages", messages);
		req.setAttribute("resultSize", messages.size());
		setNavigationMessage(req, NavigationMessage.info("Discussioni nelle quali hai partecipato"));
		return "threadsByLastPost.jsp";
	}

	private String getThreads(HttpServletRequest req, HttpServletResponse res, NavigationMessage message) throws Exception {
		String forum = req.getParameter("forum");
		boolean hideProcCatania;
		if (IPersistence.FORUM_PROC.equals(forum)) {
			hideProcCatania = false; // nascondere la proc quando si consulta la proc :P ?
		} else {
			 hideProcCatania = StringUtils.isNotEmpty(login(req).getPreferences().get(User.PREF_HIDE_PROC_CATANIA));
		}
		ThreadsDTO messages = getPersistence().getThreads(forum, PAGE_SIZE, getPageNr(req), hideProcCatania);
		req.setAttribute("messages", messages.getMessages());
		req.setAttribute("totalSize", messages.getMaxNrOfMessages());
		req.setAttribute("resultSize", messages.getMessages().size());
		addSpecificParam(req, "forum", forum);
		if (forum == null) {
			setWebsiteTitle(req, "Forum dei troll");
		} else {
			setWebsiteTitle(req, forum.equals("") ? "Forum principale @ Forum dei troll" : (forum + " @ Forum dei troll"));
		}
		setNavigationMessage(req, message);
		req.getSession().setAttribute(ANTI_XSS_TOKEN, RandomPool.getString(3));
		return "threads.jsp";
	}
}