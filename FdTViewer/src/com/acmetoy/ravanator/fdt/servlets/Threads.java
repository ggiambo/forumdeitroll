package com.acmetoy.ravanator.fdt.servlets;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.acmetoy.ravanator.fdt.IndentMessageDTO;
import com.acmetoy.ravanator.fdt.ThreadTree;
import com.acmetoy.ravanator.fdt.RandomPool;
import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.acmetoy.ravanator.fdt.persistence.ThreadsDTO;

public class Threads extends MainServlet {

	private static final long serialVersionUID = 1L;

	public static final String ANTI_XSS_TOKEN = "anti-xss-token";

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
			return initWithMessage(req, res, NavigationMessage.info("Ordinati per data inizio discussione"));
		}
	};

	/**
	 * Ordinati per thread / ultimo post
	 */
	protected GiamboAction getThreadsByLastPost = new GiamboAction("getThreadsByLastPost", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			ThreadsDTO messages = getPersistence().getThreadsByLastPost(PAGE_SIZE, getPageNr(req));
			req.setAttribute("messages", messages.getMessages());
			req.setAttribute("maxNrOfMessages", messages.getMaxNrOfMessages());
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

	/**
	 * Sposta il thread in procura
	 */
	protected GiamboAction pedonizeThread = new GiamboAction("pedonizeThread", ONPOST|ONGET) {
		public String action(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
			AuthorDTO loggedUser = (AuthorDTO)req.getSession().getAttribute(MainServlet.LOGGED_USER_SESSION_ATTR);
			if (loggedUser == null || !loggedUser.isValid()) {
				return initWithMessage(req, res, NavigationMessage.warn("Cosa stai cercando di fare ;) ?"));
			}

			final String token = (String)req.getSession().getAttribute(ANTI_XSS_TOKEN);
			final String inToken = req.getParameter("token");

			if ((token == null) || (inToken == null) || !token.equals(inToken)) {
				return initWithMessage(req, res, NavigationMessage.warn("Verifica token fallita"));
			}


			String threadId = req.getParameter("threadId");
			if (StringUtils.isEmpty(threadId)) {
				return initWithMessage(req, res, NavigationMessage.warn("Scegli un thread da spostare in Procura."));
			}
			if ("yes".equals(loggedUser.getPreferences().get("pedonizeThread"))) {
				getPersistence().pedonizeThread(Long.parseLong(threadId));
				return initWithMessage(req, res, NavigationMessage.info("Thread pedonizzato, fuck yeah 8) !"));
			}
			return initWithMessage(req, res, NavigationMessage.warn("\"Da un grande potere deriva una grande responsabilit&agrave;\""));
		}
	};

	private String initWithMessage(HttpServletRequest req, HttpServletResponse res, NavigationMessage message) throws Exception {
		ThreadsDTO messages = getPersistence().getThreads(PAGE_SIZE, getPageNr(req));
		req.setAttribute("messages", messages.getMessages());
		req.setAttribute("maxNrOfMessages", messages.getMaxNrOfMessages());
		setWebsiteTitle(req, "Forum dei troll");
		setNavigationMessage(req, message);

		final AuthorDTO loggedUser = (AuthorDTO)req.getSession().getAttribute(MainServlet.LOGGED_USER_SESSION_ATTR);
		if ((loggedUser != null) && ("yes".equals(loggedUser.getPreferences().get("pedonizeThread")))) {
			final String token = RandomPool.getString(3);
			req.getSession().setAttribute(ANTI_XSS_TOKEN, token);
			req.setAttribute("token", token);
		} else {
			req.setAttribute("token", "");
		}
		return "threads.jsp";
	}
}