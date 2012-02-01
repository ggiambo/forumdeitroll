package com.acmetoy.ravanator.fdt.servlets;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.acmetoy.ravanator.fdt.IndentMessageDTO;
import com.acmetoy.ravanator.fdt.ThreadTree;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;

public class Threads extends MainServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Tutti i messaggi di questo thread, identati
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction getByThread = new GiamboAction("getByThread", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			Long threadId = Long.parseLong(req.getParameter("threadId"));
			List<MessageDTO> msgs = getPersistence().getMessagesByThread(threadId);
			List<IndentMessageDTO> indentMsg = new ArrayList<IndentMessageDTO>(msgs.size());
			for (MessageDTO dto : msgs) {
				indentMsg.add(new IndentMessageDTO(dto));
			}
			req.setAttribute("messages", new ThreadTree(indentMsg, threadId).asList());
			setWebsiteTitle(req, getPersistence().getMessage(threadId).getSubject() + " @ Forum dei Troll");
			setNavigationMessage(req, NavigationMessage.info("Thread <i>" + getPersistence().getMessage(threadId).getSubject() + "</i>"));

			return "thread.jsp";
		}
	};

	protected GiamboAction openThreadTree = new GiamboAction("openThreadTree", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			Long threadId = Long.parseLong(req.getParameter("threadId"));
			List<MessageDTO> msgs = getPersistence().getMessagesByThread(threadId);
			List<IndentMessageDTO> indentMsg = new ArrayList<IndentMessageDTO>(msgs.size());
			for (MessageDTO dto : msgs) {
				indentMsg.add(new IndentMessageDTO(dto));
			}
			req.setAttribute("messages", new ThreadTree(indentMsg, threadId).asList());

			return "threadTree.jsp";
		}
	};

	/**
	 * Ordinati per thread / data iniziale
	 */
	protected GiamboAction init = new GiamboAction("init", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			req.setAttribute("messages", getPersistence().getThreads(PAGE_SIZE, getPageNr(req)));
			setWebsiteTitle(req, "Forum dei troll");
			setNavigationMessage(req, NavigationMessage.info("Ordinati per data inizio discussione"));
			return "threads.jsp";
		}
	};

	/**
	 * Ordinati per thread / ultimo post
	 */
	protected GiamboAction getThreadsByLastPost = new GiamboAction("getThreadsByLastPost", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			req.setAttribute("messages", getPersistence().getThreadsByLastPost(PAGE_SIZE, getPageNr(req)));
			setWebsiteTitle(req, "Forum dei troll");
			setNavigationMessage(req, NavigationMessage.info("Ordinati per ultimo post"));
			return "threadsByLastPost.jsp";
		}
	};

	protected GiamboAction getAuthorThreadsByLastPost = new GiamboAction("getAuthorThreadsByLastPost", ONPOST|ONGET) {
		public String action(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
			String author = req.getParameter("author");
			if (author == null) author = "";
			req.setAttribute("messages", getPersistence().getAuthorThreadsByLastPost(author, PAGE_SIZE, getPageNr(req)));
			return "threadsByLastPost.jsp";
		}
	};
}