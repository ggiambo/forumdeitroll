package com.forumdeitroll.servlets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.PrivateMsgDTO;
import com.forumdeitroll.persistence.PrivateMsgDTO.ToNickDetailsDTO;
import com.forumdeitroll.servlets.Action.Method;
import com.google.gson.stream.JsonWriter;

/**
 * Servlet implementation class Pvt
 */
public class Pvt extends MainServlet {
	private static final long serialVersionUID = 1L;
	
	private static int PVT_PER_PAGE = 10;
	

	@Override
	@Action
	String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return inbox(req, res);
	}
	

	@Action(method=Method.GET)
	String inbox(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO author = login(req);
		String page = req.getParameter("page");
		int npage = 0;
		try {
			npage = Integer.parseInt(page);
		} catch (Exception e) {
			
		}
		req.setAttribute("pvts", getPersistence().getInbox(author, PVT_PER_PAGE, npage));
		req.setAttribute("from", "inbox");
		req.setAttribute("totalSize", getPersistence().getInboxPages(author));
		return "pvts.jsp";
	}

	@Action(method=Method.GET)
	String sendNew(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String recipients = req.getParameter("recipients");
		if (!StringUtils.isEmpty(recipients)) {
			req.setAttribute("recipients", "'" + recipients + "'");
		}
		req.setAttribute("emoMap", new TreeMap<String, String[]>(Messages.getEmoMap()));
		req.setAttribute("extendedEmos", new TreeMap<String, String[]>(Messages.getEmoExtendedMap()));
		req.setAttribute("from", "sendNew");
		return "pvts.jsp";
	}
	
	@Action(method=Method.POST)
	String sendPvt(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO author = login(req);
		String text = req.getParameter("text");
		String subject = req.getParameter("subject");
		String[] recipients = req.getParameterValues("recipients");
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
			if (recipients == null || recipients.length > 4) {
				setNavigationMessage(req, NavigationMessage.warn("Specifica almeno un destinatario, ma non pi&ugrave; di 5 !"));
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
			if (!getPersistence().sendAPvtForGreatGoods(author, message, recipients)) {
				setNavigationMessage(req, NavigationMessage.error("Il messaggio non &egrave; stato inviato<img src='images/emo/10.gif'>"));
				ripopola(req);
				req.setAttribute("from", "sendNew");
				return "pvts.jsp";	
			}
			return inbox(req, res);
		} else {
			setNavigationMessage(req, NavigationMessage.error("Fai il login o registrati (cit)"));
			return "pvts.jsp";
		}
	}
	
	@Action(method=Method.POST)
	String notifyUnread(HttpServletRequest req, HttpServletResponse res)
		throws Exception {
		long id = Long.parseLong(req.getParameter("id"));
		PrivateMsgDTO pvt = new PrivateMsgDTO();
		pvt.setId(id);
		getPersistence().notifyUnread(login(req), pvt);
		pvt = getPersistence().getPvtDetails(id, login(req));
		req.setAttribute("pvtdetail", pvt);
		req.setAttribute("from", "show");
		req.setAttribute("sender", getPersistence().getAuthor(pvt.getFromNick()));
		return "pvts.jsp";
	}
	
	@Action(method=Method.GET)
	String show(HttpServletRequest req, HttpServletResponse res)
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
	
	@Action(method=Method.GET)
	String delete(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (login(req).isValid()) {
			long id = Long.parseLong(req.getParameter("id"));
			getPersistence().deletePvt(id, login(req));
			String from = req.getParameter("from");
			if ("outbox".equals(from)) {
				return outbox(req, res);
			}
			return inbox(req, res);
		}
		return "pvts.jsp";
	}
	
	@Action(method=Method.GET)
	String outbox(HttpServletRequest req, HttpServletResponse res) throws Exception {
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
			req.setAttribute("totalSize", getPersistence().getOutboxPages(author));
			return "pvts.jsp";
		}
		return null; //TODO pagina user non auth
	}
	
	/**
	 * Rispondi al mittente
	 */
	@Action(method=Method.GET)
	String reply(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return reply(req, false);
	}
	
	/**
	 * Rispondi al mittente e a tutti i destinatari, tranne che me
	 */
	@Action(method=Method.GET)
	String replyAll(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return reply(req, true);
	}
	
	private String reply(HttpServletRequest req, boolean toAll)	throws Exception {
		long id = Long.parseLong(req.getParameter("id"));
		PrivateMsgDTO pvt = new PrivateMsgDTO();
		pvt.setId(id);
		pvt = getPersistence().getPvtDetails(id, login(req));
		// prepara il reply
		if (toAll) {
			List<ToNickDetailsDTO> recipients = new ArrayList<ToNickDetailsDTO>(pvt.getToNick());
			
			recipients.add(new ToNickDetailsDTO(pvt.getFromNick()));
			String me = login(req).getNick();
			for (Iterator<ToNickDetailsDTO> itRec = recipients.iterator(); itRec.hasNext();) {
				if (me.equals(itRec.next().getNick())) {
					itRec.remove();
				}
			}
			req.setAttribute("recipients", "'" + StringUtils.join(recipients, "','") + "'");
		} else {
			req.setAttribute("recipients", "'" + pvt.getFromNick() + "'");
		}
		String subject = pvt.getSubject();
		if (!subject.startsWith("Re:")) {
			subject = "Re: " + subject;
		}
		req.setAttribute("subject", subject);
		String text = pvt.getText().replaceAll("^", "> ").replaceAll("<BR>", "> ");
		req.setAttribute("text", text);
		req.setAttribute("from", "sendNew");
		req.setAttribute("emoMap", new TreeMap<String, String[]>(Messages.getEmoMap()));
		req.setAttribute("extendedEmos", new TreeMap<String, String[]>(Messages.getEmoExtendedMap()));
		return "pvts.jsp";
	}
	
	@Action(method=Method.GET)
	String searchAuthorAjax(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String searchString = req.getParameter("searchString");
		if (StringUtils.isNotEmpty(searchString) && searchString.length() > 1) {
			List<String> authors = getPersistence().searchAuthor(searchString);
			JsonWriter writer = new JsonWriter(res.getWriter());
			writer.beginObject();
			writer.name("resultCode").value("OK");
			writer.name("content");
			writer.beginArray();
			for (String author : authors) {
				writer.value(author);
			}
			writer.endArray();
			writer.endObject();
			writer.flush();
			writer.close();
		}
		return null;
	}
	
	private void ripopola(HttpServletRequest req) {
		req.setAttribute("text", req.getParameter("text"));
		req.setAttribute("subject", req.getParameter("subject"));
		req.setAttribute("emoMap", new TreeMap<String, String[]>(Messages.getEmoMap()));
		req.setAttribute("extendedEmos", new TreeMap<String, String[]>(Messages.getEmoExtendedMap()));
		// recipients: dato in pasto a javascript sotto forma di array di stringhe
		String[] recipients = req.getParameterValues("recipients");
		if (recipients != null) {
			req.setAttribute("recipients", "'" + StringUtils.join(recipients, "','") + "'");
		}
	}

}
