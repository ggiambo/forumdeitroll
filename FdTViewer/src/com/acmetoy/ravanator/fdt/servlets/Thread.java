package com.acmetoy.ravanator.fdt.servlets;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.acmetoy.ravanator.fdt.IndentMessageDTO;
import com.acmetoy.ravanator.fdt.ThreadTree;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.acmetoy.ravanator.fdt.persistence.PersistenceFactory;

public class Thread extends MainServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Tutti i messaggi di questo thread, identati
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String getByThread(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String threadId = req.getParameter("threadId");
		List<MessageDTO> msgs = PersistenceFactory.getPersistence().getMessagesByThread(Integer.parseInt(threadId));
		List<IndentMessageDTO> indentMsg = new ArrayList<IndentMessageDTO>(msgs.size());
		for (MessageDTO dto : msgs) {
			indentMsg.add(new IndentMessageDTO(dto));
		}
		req.setAttribute("messages", new ThreadTree(indentMsg, Long.parseLong(threadId)).asList());

		return "thread.jsp";
	}

}