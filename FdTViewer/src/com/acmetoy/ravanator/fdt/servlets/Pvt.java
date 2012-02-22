package com.acmetoy.ravanator.fdt.servlets;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.FdTException;
import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.PrivateMsgDTO;

/**
 * Servlet implementation class Pvt
 */
public class Pvt extends MainServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = Logger.getLogger(MainServlet.class);
	
	private static int PVT_PER_PAGE = 10;
	
	/**
	 * inbox
	 * outbox
	 * sendNew
	 * sendPvt
	 * delete
	 */
	
	protected GiamboAction inbox = new GiamboAction("inbox", ONGET) {
		@Override
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			AuthorDTO author = login(req);
			String page = req.getParameter("page");
			int npage = 0;
			try {
				npage = Integer.parseInt(page);
			} catch (Exception e) {
				
			}
			req.setAttribute("pvts", getPersistence().getInbox(author, PVT_PER_PAGE, npage));
			req.setAttribute("from", "inbox");
			req.setAttribute("maxNrOfMessages", getPersistence().getInboxPages(author));
			return "pvts.jsp";
		}
	};

	protected GiamboAction sendNew = new GiamboAction("sendNew", ONGET) {
		@Override
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			String singleRecipient = req.getParameter("singleRecipient");
			if (!StringUtils.isEmpty(singleRecipient)) {
				req.setAttribute("recipient", new String[] { singleRecipient });
			}
			req.setAttribute("from", "sendNew");
			return "pvts.jsp";
		}
	};
	
	protected GiamboAction sendPvt = new GiamboAction("sendPvt", ONPOST) {
		private void ripopola(HttpServletRequest req) {
			req.setAttribute("text", req.getParameter("text"));
			req.setAttribute("subject", req.getParameter("subject"));
			req.setAttribute("recipient", req.getParameterValues("recipient"));
		}
		@Override
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			AuthorDTO author = login(req);
			String text = req.getParameter("text");
			String subject = req.getParameter("subject");
			String[] recipients = req.getParameterValues("recipient");
			if (author.isValid()) {
				if (subject == null || subject.length() == 0) {
					setNavigationMessage(req, NavigationMessage.warn("Oggetto un po cortino, non trovi ?"));
					ripopola(req);
					req.setAttribute("from", "sendNew");
					return "pvts.jsp";
				}
				if (text == null || text.length() == 0) {
					setNavigationMessage(req, NavigationMessage.warn("Non hai scritto niente, te ne rendi conto ?"));
					ripopola(req);
					req.setAttribute("from", "sendNew");
					return "pvts.jsp";
				}
				if (recipients == null || recipients.length != 5) {
					setNavigationMessage(req, NavigationMessage.warn("Furmigamento detected!"));
					req.setAttribute("from", "sendNew");
					return "pvts.jsp";
				}
				boolean recOk = false;
				for (int i=0;i<recipients.length;++i) {
					if (recipients[i] != null && recipients[i].length() > 0) {
						recOk = true;
						break;
					}
				}
				if (!recOk) {
					setNavigationMessage(req, NavigationMessage.warn("Specifica almeno un destinatario!"));
					ripopola(req);
					req.setAttribute("from", "sendNew");
					return "pvts.jsp";
				}
				
				text = text.replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\n", "<BR>");
				for (String t : new String[] {"i", "b", "u", "s"}) {
					text = text.replaceAll("(?i)&lt;" + t + "&gt;", "<" + t + ">");
					text = text.replaceAll("(?i)&lt;/" + t + "&gt;", "</" + t + ">");
				}
				
				PrivateMsgDTO message = new PrivateMsgDTO();
				message.setText(text);
				message.setSubject(subject);
				try {
					if (!getPersistence().sendAPvtForGreatGoods(author, message, recipients)) {
						setNavigationMessage(req, NavigationMessage.error("Il messaggio non è stato inviato<img src='images/emo/10.gif'>"));
						ripopola(req);
						req.setAttribute("from", "sendNew");
						return "pvts.jsp";	
					}
				} catch (FdTException e) {
					setNavigationMessage(req, NavigationMessage.error(e.getMessage()+"<img src='images/emo/10.gif'>"));
					ripopola(req);
					req.setAttribute("from", "sendNew");
					return "pvts.jsp";	
				}
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
			req.setAttribute("from", "show");
			req.setAttribute("sender", getPersistence().getAuthor(pvt.getFromNick()));
			return "pvts.jsp";
		}
	};
	
	protected GiamboAction delete = new GiamboAction("delete", ONGET) {
		@Override
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			if (login(req).isValid()) {
				long id = Long.parseLong(req.getParameter("id"));
				getPersistence().deletePvt(id, login(req));
				String from = req.getParameter("from");
				if ("outbox".equals(from)) {
					return outbox.action(req, res);
				}
				return inbox.action(req, res);
			}
			return "pvts.jsp";
		}
	};
	
	protected GiamboAction outbox = new GiamboAction("outbox", ONGET) {
		@Override
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			AuthorDTO author = login(req);
			if (author.isValid()) {
				int npage = 0;
				try {
					npage = Integer.parseInt(req.getParameter("page"));
				} catch (Exception e) {
					
				}
				List<PrivateMsgDTO> pvts = getPersistence().getSentPvts(login(req), PVT_PER_PAGE, npage);
				req.setAttribute("pvts", pvts);
				req.setAttribute("from", "outbox");
				req.setAttribute("maxNrOfMessages", getPersistence().getOutboxPages(author));
				return "pvts.jsp";
			}
			return null; //TODO pagina user non auth
		}
	};
	
	protected GiamboAction reply = new GiamboAction("reply", ONGET) {
		@Override
		public String action(HttpServletRequest req, HttpServletResponse res)
				throws Exception {
			long id = Long.parseLong(req.getParameter("id"));
			PrivateMsgDTO pvt = new PrivateMsgDTO();
			pvt.setId(id);
			pvt = getPersistence().getPvtDetails(id, login(req));
			// prepara il reply
			req.setAttribute("recipient", new String[] { pvt.getFromNick() });
			String subject = pvt.getSubject();
			if (!subject.startsWith("Re:")) {
				subject = "Re: " + subject;
			}
			req.setAttribute("subject", subject);
			String text = pvt.getText().replaceAll("^", "> ").replaceAll("<BR>", "> ");
			req.setAttribute("text", text);
			req.setAttribute("from", "sendNew");
			return "pvts.jsp";
		}
	};
}
