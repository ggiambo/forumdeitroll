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
	public String getByThread(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Long threadId = Long.parseLong(req.getParameter("threadId"));
		List<MessageDTO> msgs = getPersistence().getMessagesByThread(threadId);
		List<IndentMessageDTO> indentMsg = new ArrayList<IndentMessageDTO>(msgs.size());
		for (MessageDTO dto : msgs) {
			indentMsg.add(new IndentMessageDTO(dto));
		}
		req.setAttribute("messages", new ThreadTree(indentMsg, threadId).asList());
		//req.setAttribute("messages", new ThreadTree2(indentMsg, threadId).asList());
		setNavigationMessage(req, "Thread <i>" + getPersistence().getMessage(threadId).getSubject() + "</i>");

		return "thread.jsp";
	}

	/**
	 * Ordinati per thread
	 */
	@Override
	public String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setAttribute("messages", getPersistence().getThreads(PAGE_SIZE, getPageNr(req)));
		setNavigationMessage(req, "Ordinati per data inizio discussione");
		return "threads.jsp";
	}

}