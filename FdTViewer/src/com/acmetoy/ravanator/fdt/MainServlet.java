package com.acmetoy.ravanator.fdt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.persistence.AuthorPersistence;
import com.acmetoy.ravanator.fdt.persistence.MessagePersistence;
import com.acmetoy.ravanator.fdt.persistence.StatusPersistence;
import com.mongodb.DBObject;

public class MainServlet extends HttpServlet {
	
	private static final Logger LOG = Logger.getLogger(MainServlet.class);

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
			LOG.error("Cannot read default images", e);
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		doPost(req, res);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try {
			Date lastScan = StatusPersistence.getInstance().getLastScan();
			if (lastScan == null || new Date().getTime() - lastScan.getTime() < 1 * 60 * 1000) {
				// trigger a scan of the last page
				StatusPersistence.getInstance().updateScanDate();
			}
			
			String action = req.getParameter("action");
			if (action == null || action.trim().length() == 0) {
				req.setAttribute("messages", MessagePersistence.getInstance().getMessagesByDate(15));
				req.setAttribute("pageNr", "0");
				getServletContext().getRequestDispatcher("/WEB-INF/pages/result.jsp").forward(req, res);
			} else if ("avatar".equals(action)) {
				String nick = req.getParameter("nick");
				DBObject author = AuthorPersistence.getInstance().getAuthor(nick);
				if (author != null) {
					if (author.get("avatar") != null) {
						res.getOutputStream().write((byte[])author.get("avatar"));
					} else {
						res.getOutputStream().write(noAvatar);
					}
				} else {
					res.getOutputStream().write(notAuthenticated);
				}
			} else if ("page".equals(action)) {
				String pageNr = req.getParameter("pageNr");
				req.setAttribute("pageNr", pageNr);
				req.setAttribute("messages", MessagePersistence.getInstance().getMessagesByDate(15,  Integer.parseInt(pageNr)));
				getServletContext().getRequestDispatcher("/WEB-INF/pages/result.jsp").forward(req, res);
			} else if ("thread".equals(action)) {
				String threadId = req.getParameter("threadId");
				List<DBObject> msgs = MessagePersistence.getInstance().getMessagesByThread(Integer.parseInt(threadId));
				req.setAttribute("messages", new ThreadTree(msgs, Long.parseLong(threadId)).asList());
				
				getServletContext().getRequestDispatcher("/WEB-INF/pages/thread.jsp").forward(req, res);
			}
		} catch (Exception e) {
			res.getWriter().write(e.toString());
		}
	}
	
	
}