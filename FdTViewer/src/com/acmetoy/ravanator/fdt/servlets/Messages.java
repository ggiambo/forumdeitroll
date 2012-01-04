package com.acmetoy.ravanator.fdt.servlets;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.acmetoy.ravanator.fdt.persistence.MessageDTO;

public class Messages extends MainServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Pattern PATTERN_QUOTE = Pattern.compile("<BR>(&gt;\\ ?)*");
	
	private static final Map<String, String> EMO_MAP = new HashMap<String, String>();	
	static {
		EMO_MAP.put("1", " :)");
		EMO_MAP.put("2", " :D");
		EMO_MAP.put("3", " ;)");
		EMO_MAP.put("4", " :o");
		EMO_MAP.put("5", " :p");
		EMO_MAP.put("6", " :\\");
		EMO_MAP.put("7", " :@");
		EMO_MAP.put("8", " :s");
		EMO_MAP.put("9", " :$");
		EMO_MAP.put("10", " :(");
		EMO_MAP.put("11", ":'(");
		EMO_MAP.put("12", " :|");
		EMO_MAP.put("13", " 8)");
		EMO_MAP.put("angelo", " O)");
		EMO_MAP.put("anonimo", "(anonimo)");
		EMO_MAP.put("diavoletto", " @^");
		EMO_MAP.put("fantasmino", "(ghost)");
		EMO_MAP.put("geek", "(geek)");
		EMO_MAP.put("idea", "(idea)");
		EMO_MAP.put("love", "(love)");
		EMO_MAP.put("loveamiga", "(amiga)");
		EMO_MAP.put("loveapple", "(apple)");
		EMO_MAP.put("loveatari", "(atari)");
		EMO_MAP.put("lovec64", "(c64)");
		EMO_MAP.put("lovelinux", "(linux)");
		EMO_MAP.put("lovewin", "(win)");
		EMO_MAP.put("newbie", "(newbie)");
		EMO_MAP.put("noia3", " :-o");
		EMO_MAP.put("nolove", "(nolove)");
		EMO_MAP.put("pirata", " p)");
		EMO_MAP.put("robot", "(cylon)");
		EMO_MAP.put("rotfl", "(rotfl)");
		EMO_MAP.put("troll1", "(troll1)");
		EMO_MAP.put("troll2", "(troll2)");
		EMO_MAP.put("troll3", "(troll3)");
		EMO_MAP.put("troll4", "(troll4)");
		EMO_MAP.put("troll", "(troll)");
	}
	
	@Override
	public String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return getByPage(req, res);
	}

	/**
	 * I messaggi di questa pagina (Dimensione PAGE_SIZE) in ordine di data
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String getByPage(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setAttribute("messages", getPersistence().getMessagesByDate(PAGE_SIZE, getPageNr(req)));
		setNavigationMessage(req, "Ordinati cronologicamente");
		return "messages.jsp";
	}

	/**
	 * I messaggi di questo autore in ordine di data
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String getByAuthor(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String author = req.getParameter("author");
		req.setAttribute("specificParams", "&author=" + author);
		setNavigationMessage(req, "Messaggi scritti da <i>" + author + "</i>");
		req.setAttribute("messages", getPersistence().getMessagesByAuthor(author, PAGE_SIZE, getPageNr(req)));
		return "messages.jsp";
	}

	public String getByForum(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String forum = req.getParameter("forum");
		req.setAttribute("specificParams", "&forum=" + forum);
		setNavigationMessage(req, "Forum <i>" + forum + "</i>");
		req.setAttribute("messages", getPersistence().getMessagesByForum(forum, PAGE_SIZE, getPageNr(req)));
		return "messages.jsp";
	}

	/**
	 * Ricerca in tutti i messaggi
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String search(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String search = req.getParameter("search");
		req.setAttribute("specificParams", "&search=" + search);
		req.setAttribute("messages", getPersistence().searchMessages(search, PAGE_SIZE, getPageNr(req)));
		return "messages.jsp";
	}
	
	/**
	 * Popola il div per la risposta/quota messaggio
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String showReplyDiv(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String type = req.getParameter("type");
		String parentId = req.getParameter("parentId");
		String threadId = req.getParameter("threadId");
		req.setAttribute("parentId", parentId);
		req.setAttribute("threadId", threadId);
		if ("quote".equals(type)) {
			MessageDTO msgDTO = getPersistence().getMessage(Long.parseLong(parentId));
			String text = msgDTO.getText().trim();
			
			// quote
			Matcher m = PATTERN_QUOTE.matcher(text);
			while (m.find()) {
				String group = m.group(0);
				int nrQuotes = group.replace(" ", "").replace("<BR>", "").length() / 4; // 4 = "&gt;".length();
				nrQuotes++;
				StringBuilder newBegin = new StringBuilder("\r\n");
				for (int j = 0; j < nrQuotes; j++) {
					newBegin.append("> ");
				}
				text = m.replaceFirst(newBegin.toString());
				m = PATTERN_QUOTE.matcher(text);
			}
			
			String author = msgDTO.getAuthor();
			text = "\r\nScritto da: " + (author != null ? author : "") + "\r\n>" + text;
			msgDTO.setText(text);
			req.setAttribute("message", msgDTO);
		}

		return "incReplyMessage.jsp";
	}
	
	/**
	 * Inserisce un nuovo messaggio
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String insertMessage(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String parentId = req.getParameter("parentId");
		String threadId = req.getParameter("threadId");
		String text = req.getParameter("text").replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\n", "<BR>");
		return null;
	}
	
	public static Map<String, String> getEmoMap() {
		return new HashMap<String, String>(EMO_MAP);
	}
	
}