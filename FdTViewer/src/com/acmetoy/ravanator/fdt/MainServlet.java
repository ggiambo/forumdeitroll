package com.acmetoy.ravanator.fdt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.acmetoy.ravanator.fdt.persistence.PersistenceFactory;

public class MainServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private byte[] notAuthenticated;
	private byte[] noAvatar;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			InputStream is = config.getServletContext().getResourceAsStream("/images/avataranonimo.gif");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int count = -1;
			while ((count = is.read(buffer)) != -1) {
				bos.write(buffer, 0, count);
			}
			notAuthenticated = bos.toByteArray();
			
			is = config.getServletContext().getResourceAsStream("/images/avatardefault.gif");
			bos = new ByteArrayOutputStream();
			buffer = new byte[1024];
			count = -1;
			while ((count = is.read(buffer)) != -1) {
				bos.write(buffer, 0, count);
			}
			noAvatar = bos.toByteArray();
		} catch (IOException e) {
			throw new ServletException("Cannot read default images", e);
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		doPost(req, res);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try {
			String action = req.getParameter("action");
			if (action == null || action.trim().length() == 0) {
				req.setAttribute("messages", PersistenceFactory.getPersistence().getMessagesByDate(15));
				req.setAttribute("pageNr", "0");
				getServletContext().getRequestDispatcher("/WEB-INF/pages/result.jsp").forward(req, res);
			} else if ("avatar".equals(action)) {
				String nick = req.getParameter("nick");
				AuthorDTO author = PersistenceFactory.getPersistence().getAuthor(nick);
				if (author != null) {
					if (author.getAvatar() != null) {
						res.getOutputStream().write(author.getAvatar());
					} else {
						res.getOutputStream().write(noAvatar);
					}
				} else {
					res.getOutputStream().write(notAuthenticated);
				}
			} else if ("page".equals(action)) {
				String pageNr = req.getParameter("pageNr");
				req.setAttribute("pageNr", pageNr);
				req.setAttribute("messages", PersistenceFactory.getPersistence().getMessagesByDate(15,  Integer.parseInt(pageNr)));
				getServletContext().getRequestDispatcher("/WEB-INF/pages/result.jsp").forward(req, res);
			} else if ("thread".equals(action)) {
				String threadId = req.getParameter("threadId");
				List<MessageDTO> msgs = PersistenceFactory.getPersistence().getMessagesByThread(Integer.parseInt(threadId));
				List<IndentMessageDTO> indentMsg = new ArrayList<IndentMessageDTO>(msgs.size());
				for (MessageDTO dto : msgs) {
					indentMsg.add(new IndentMessageDTO(dto));
				}
				req.setAttribute("messages", new ThreadTree(indentMsg, Long.parseLong(threadId)).asList());
				
				getServletContext().getRequestDispatcher("/WEB-INF/pages/thread.jsp").forward(req, res);
			}
		} catch (Exception e) {
			res.getWriter().write(e.toString());
		}
	}
	
	
}