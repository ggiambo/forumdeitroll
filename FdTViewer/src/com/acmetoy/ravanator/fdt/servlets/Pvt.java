package com.acmetoy.ravanator.fdt.servlets;

import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.PersistenceFactory;
import com.acmetoy.ravanator.fdt.persistence.PrivateMsgDTO;

/**
 * Servlet implementation class Pvt
 */
public class Pvt extends MainServlet implements Servlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(PersistenceFactory.class);
	
	private static int PVT_PER_PAGE = 10;
	
	protected GiamboAction inbox = new GiamboAction("inbox", ONGET) {
		@Override
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			AuthorDTO author = login(req);
			String page = req.getParameter("page");
			int npage = 0;
			try {
				npage = Integer.parseInt(page);
			} catch (Exception e) {}
			req.setAttribute("pvts", getPersistence().getInbox(author, PVT_PER_PAGE, npage));
			return "pvts.jsp";
		}
	};

	protected GiamboAction sendPvt = new GiamboAction("sendPvt", ONPOST) {
		@Override
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			AuthorDTO author = login(req);
			String text = req.getParameter("text");
			String subject = req.getParameter("subject");
			String[] recipients = req.getParameterValues("recipient");
			if (author.isValid()) {
				if (text == null) {
					setNavigationMessage(req, NavigationMessage.warn("Non hai scritto niente, te ne rendi conto ?"));
					return "pvts.jsp";
				}
				if (recipients == null || recipients.length == 0 || recipients.length > 5) return "403";
				
				text = text.replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\n", "<BR>");
				for (String t : new String[] {"i", "b", "u", "s"}) {
					text = text.replaceAll("(?i)&lt;" + t + "&gt;", "<" + t + ">");
					text = text.replaceAll("(?i)&lt;/" + t + "&gt;", "</" + t + ">");
				}
				if (subject == null) {
					setNavigationMessage(req, NavigationMessage.warn("Oggetto un po cortino, non trovi ?"));
					return "pvts.jsp";
				}
				PrivateMsgDTO message = new PrivateMsgDTO();
				message.setText(text);
				message.setSubject(subject);
				
				getPersistence().sendAPvtForGreatGoods(author, message, recipients);
				return inbox.action(req, res);
			} else {
				setNavigationMessage(req, NavigationMessage.error("Fai il login o registrati (cit)"));
				return "pvts.jsp";
			}
		}
	};
	
	protected GiamboAction show = new GiamboAction("show", ONGET) {
		@Override
		public String action(HttpServletRequest req, HttpServletResponse res)
				throws Exception {
			long id = Long.parseLong(req.getParameter("id"));
			PrivateMsgDTO pvt = new PrivateMsgDTO();
			pvt.setId(id);
			getPersistence().notifyRead(login(req), pvt);
			pvt = getPersistence().getPvtDetails(id, login(req));
			req.setAttribute("pvtdetail", pvt);
			return "pvts.jsp";
		}
	};
	
	protected GiamboAction delete = new GiamboAction("delete", ONGET) {
		@Override
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			if (login(req).isValid()) {
				long id = Long.parseLong(req.getParameter("id"));
				getPersistence().deletePvt(id, login(req));
				return inbox.action(req, res);
			}
			return "pvts.jsp";
		}
	};
	
	protected GiamboAction outbox = new GiamboAction("outbox", ONGET) {
		@Override
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			if (login(req).isValid()) {
				int npage = 0;
				try {
					npage = Integer.parseInt(req.getParameter("page"));
				} catch (Exception e) {}
				List<PrivateMsgDTO> pvts = getPersistence().getSentPvts(login(req), PVT_PER_PAGE, npage);
				req.setAttribute("pvts", pvts);
				return "pvts.jsp";
			}
			return "pvts.jsp";
		}
	};
}
