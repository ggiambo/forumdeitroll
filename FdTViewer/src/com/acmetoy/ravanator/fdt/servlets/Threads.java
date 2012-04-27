package com.acmetoy.ravanator.fdt.servlets;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.acmetoy.ravanator.fdt.RandomPool;
import com.acmetoy.ravanator.fdt.ThreadTree;
import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.acmetoy.ravanator.fdt.persistence.ThreadDTO;
import com.acmetoy.ravanator.fdt.persistence.ThreadsDTO;

public class Threads extends MainServlet {

	private static final long serialVersionUID = 1L;

	public static final String ANTI_XSS_TOKEN = "anti_xss_token";

	/**
	 * Ordinati per thread / data iniziale
	 */
	protected GiamboAction init = new GiamboAction("init", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			res.sendRedirect("Threads?action=getThreads");
			return null;
		}
	};
	
	/**
	 * Tutti i messaggi di questo thread
	 */
	protected GiamboAction getByThread = new GiamboAction("getByThread", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			String stringThreadId = req.getParameter("threadId");
			if (StringUtils.isEmpty(stringThreadId)) {
				return init.action(req, res);
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
	};

	/**
	 * Chiamato via ajax, apre il thread tree
	 */
	protected GiamboAction openThreadTree = new GiamboAction("openThreadTree", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			Long threadId = Long.parseLong(req.getParameter("threadId"));
			List<MessageDTO> msgs = getPersistence().getMessagesByThread(threadId);
			req.setAttribute("msg", new ThreadTree(msgs).getRoot());

			return "threadTree.jsp";
		}
	};
	
	/**
	 * Ordinati per thread / data iniziale
	 */
	protected GiamboAction getThreads = new GiamboAction("getThreads", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return getThreads(req, res, NavigationMessage.info("Thread nuovi"));
		}
	};

	/**
	 * Ordinati per thread / ultimo post
	 */
	protected GiamboAction getThreadsByLastPost = new GiamboAction("getThreadsByLastPost", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
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
			setNavigationMessage(req, NavigationMessage.info("Thread aggiornati"));
			req.getSession().setAttribute(ANTI_XSS_TOKEN, RandomPool.getString(3));
			return "threadsByLastPost.jsp";
		}
	};

	/**
	 * Tutti i threads dell'utente loggato, ordinati per ultimo post
	 */
	protected GiamboAction getAuthorThreadsByLastPost = new GiamboAction("getAuthorThreadsByLastPost", ONGET) {
		public String action(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
			AuthorDTO author = login(req);
			if (!author.isValid()) {
				throw new Exception("Furmigamento detected !");
			}
			boolean hideProcCatania = StringUtils.isNotEmpty(login(req).getPreferences().get(User.PREF_HIDE_PROC_CATANIA));
			List<ThreadDTO> messages = getPersistence().getAuthorThreadsByLastPost(author.getNick(), PAGE_SIZE, getPageNr(req), hideProcCatania);
			req.setAttribute("messages", messages);
			req.setAttribute("resultSize", messages.size());
			return "threadsByLastPost.jsp";
		}
	};

	private String getThreads(HttpServletRequest req, HttpServletResponse res, NavigationMessage message) throws Exception {
		boolean hideProcCatania = StringUtils.isNotEmpty(login(req).getPreferences().get(User.PREF_HIDE_PROC_CATANIA));
		String forum = req.getParameter("forum");
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