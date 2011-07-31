package com.acmetoy.ravanator.fdt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.acmetoy.ravanator.fdt.persistence.AuthorPersistence;
import com.acmetoy.ravanator.fdt.persistence.MessagePersistence;
import com.mongodb.DBObject;

public class MainServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private byte[] noImage;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			InputStream is = config.getServletContext().getResourceAsStream("/images/emo/troll.gif");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int count = -1;
			while ((count = is.read(buffer)) != -1) {
				bos.write(buffer, 0, count);
			}
			noImage = bos.toByteArray();
		} catch (IOException e) {
			
		}
		
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		doPost(req, res);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try {
			String action = req.getParameter("action");
			if (action == null || action.trim().length() == 0) {
				req.setAttribute("messages", MessagePersistence.getInstance().getMessagesByDate(5));
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/pages/result.jsp");
				rd.forward(req, res);
			} else if ("avatar".equals(action)) {
				String nick = req.getParameter("nick");
				DBObject author = AuthorPersistence.getInstance().getAuthor(nick);
				if (author != null) {
					res.getOutputStream().write((byte[])author.get("avatar"));
				} else {
					res.getOutputStream().write(noImage);
				}
				
			}
		} catch (Exception e) {
			res.getWriter().write(e.toString());
		}
	}
}