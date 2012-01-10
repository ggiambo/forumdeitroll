package com.acmetoy.ravanator.fdt.servlets;

import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;

public class Messages extends MainServlet {

	private static final long serialVersionUID = 1L;

	private static final Pattern PATTERN_QUOTE = Pattern.compile("<BR>(&gt;\\ ?)*");
	private static final Pattern PATTERN_YT = Pattern.compile("\\[yt\\]((.*?)\"(.*?))\\[/yt\\]");
    private static final Pattern PATTERN_YOUTUBE = Pattern.compile("(https?://)?(www|it)\\.youtube\\.com/watch\\?v=(.{11})");

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
	public String newMessage(HttpServletRequest req, HttpServletResponse res) throws Exception {
		MessageDTO msg = new MessageDTO();
		msg.setForum(req.getParameter("forum"));
		req.setAttribute("message", msg);
		// faccine - ordinate per key
		TreeMap<String, String> emoMap = new TreeMap<String, String>(EMO_MAP);
		req.setAttribute("emoMap", emoMap);
		return "newMessage.jsp";
	}

	/**
	 * Popola il div per la risposta/quota messaggio
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String showReplyDiv(HttpServletRequest req, HttpServletResponse res) throws Exception {
		res.setCharacterEncoding("UTF-8");
		String type = req.getParameter("type");
		long parentId = Long.parseLong(req.getParameter("parentId"));
		req.setAttribute("parentId", parentId);
		MessageDTO newMsg = new MessageDTO();
		if ("quote".equals(type)) {
			MessageDTO msgDTO = getPersistence().getMessage(parentId);
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
			text = "\r\nScritto da: " + (author != null ? author.trim() : "") + "\r\n>" + text + "\r\n";
			newMsg.setText(text);
			newMsg.setForum(msgDTO.getForum());
			newMsg.setSubject(msgDTO.getSubject());
		}
		newMsg.setParentId(parentId);
		req.setAttribute("message", newMsg);

		// faccine - ordinate per key
		TreeMap<String, String> emoMap = new TreeMap<String, String>(EMO_MAP);
		req.setAttribute("emoMap", emoMap);

		return "incReplyMessage.jsp";
	}

	/**
	 * Ritorna una stringa diversa da null da mostrare come messaggio d'errore all'utente
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	private String validateInsertMessage(HttpServletRequest req) throws Exception {

		String text = req.getParameter("text");

		// testo di almeno di 5 caratteri ...
		if (StringUtils.isEmpty(text) || text.length() < 5) {
			return "Un po di fantasia, scrivi almeno 5 caratteri ...";
		}

		// testo al massimo di 5000 caratteri ...
		if (text.length() > 5000) {
			return "Sei piu' logorroico di una Wakka, stai sotto i 5000 caratteri !";
		}

		// subject almeno di 5 caratteri, cribbio !
		long parentId;
		try {
			parentId = Long.parseLong(req.getParameter("parentId"));
		} catch (NumberFormatException e) {
			return "Il valore " + req.getParameter("parentId") + " assomiglia poco a un numero ...";
		}
		if (parentId == -1) {
			// nuovo messaggio
			String subject = req.getParameter("subject");
			if (StringUtils.isEmpty(subject) || subject.trim().length() < 3) {
				return "Oggetto di almeno di 3 caratteri, cribbio !";
			}
			if (subject.length() > 25) {
				return "LOL oggetto piu' lungo di 25 caratteri !";
			}
		}

		// qualcuno prova a creare un forum ;) ?
		String forum = req.getParameter("forum");
		if (!StringUtils.isEmpty(forum) && !getPersistence().getForums().contains(forum)) {
			return "Ma che cacchio di forum e' '" + forum + "' ?!?";
		}

		return null;
	}

	/**
	 * Inserisce un nuovo messaggio
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String editMessage(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// check se l'uente loggato corrisponde a chi ha scritto il messaggio
		AuthorDTO user = login(req);
		String msgId = req.getParameter("msgId");
		MessageDTO msg = getPersistence().getMessage(Long.parseLong(msgId));
		if (!user.isValid() || !user.getNick().equals(msg.getAuthor())) {
			setNavigationMessage(req, "Non puoi editare un messaggio non tuo !");
			return getByPage(req, res);
		}
		
		// cleanup
		msg.setText(msg.getText().replaceAll("<BR>", "\r\n"));
		req.setAttribute("message", msg);
		
		// faccine - ordinate per key
		TreeMap<String, String> emoMap = new TreeMap<String, String>(EMO_MAP);
		req.setAttribute("emoMap", emoMap);
		
		return "newMessage.jsp";
	}
	
	/**
	 * Inserisce un messaggio nuovo o editato
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String insertMessage(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// se c'e' un'errore, mostralo
		String validationMessage = validateInsertMessage(req);
		if (validationMessage != null) {
			res.setContentType("text/plain");
			res.setStatus(500);
			Writer w = res.getWriter();
			w.write(validationMessage);
			w.flush();
			w.close();
			return null;
		}

		final AuthorDTO author = login(req);
		if (!author.isValid()) {
			// la coppia nome utente/password non e` stata inserita e l'utente non e` loggato, ergo deve inserire il captcha giusto
			String captcha = req.getParameter("captcha");
			String correctAnswer = (String)req.getSession().getAttribute("captcha");
			if ((correctAnswer == null) || !correctAnswer.equals(captcha)) {
				//autenticazione fallita
				res.setContentType("text/plain");
				res.setStatus(500);
				final Writer w = res.getWriter();
				w.write("Autenticazione / verifica captcha fallita");
				w.flush();
				w.close();
				return null;
			}
		}

		req.getSession().removeAttribute("captcha");

		String text = req.getParameter("text");
		// replace dei caratteri HTML
		text = text.replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\n", "<BR>");

		// restore <i>, <b>, <u> e <s>
		for (String t : new String[] {"i", "b", "u", "s"}) {
			text = text.replaceAll("(?i)&lt;" + t + "&gt;", "<" + t + ">");
			text = text.replaceAll("(?i)&lt;/" + t + "&gt;", "</" + t + ">");
		}

		// evita inject in yt
		Matcher m = PATTERN_YT.matcher(text);
		while (m.find()) {
			String replace =  m.group(1).replaceAll("\"", "");
			text = m.replaceFirst(Matcher.quoteReplacement("[yt]" + replace + "[/yt]"));
			 m = PATTERN_YT.matcher(text);
		}
		
	    // estrai id da URL youtube
	    m = PATTERN_YOUTUBE.matcher(text);
	    while (m.find()) {
	            text = m.replaceFirst(Matcher.quoteReplacement(m.group(3)));
	            m = PATTERN_YOUTUBE.matcher(text);
	    }

		// reply o messaggio nuovo ?
		long parentId = Long.parseLong(req.getParameter("parentId"));
		MessageDTO msg = new MessageDTO();
		msg.setAuthor(author.getNick());
		msg.setParentId(parentId);
		msg.setDate(new Date());
		msg.setText(text);
		if (parentId > 0) {
			long id = Long.parseLong(req.getParameter("id"));
			if (id > -1) {
				// modify
				msg = getPersistence().getMessage(id);
				if (msg.getAuthor() == null || !msg.getAuthor().equals(author.getNick())) {
					res.setContentType("text/plain");
					res.setStatus(500);
					final Writer w = res.getWriter();
					w.write("Imbroglione, non puoi modificare questo messaggio !");
					w.flush();
					w.close();
					return null;
				}
				text += "<BR><BR><b>**Modificato dall'autore il " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()) + "**</b>";
				msg.setText(text);
			} else {
				// reply
				MessageDTO replyMsg = getPersistence().getMessage(parentId);
				msg.setForum(replyMsg.getForum());
				msg.setSubject(replyMsg.getSubject());
				msg.setThreadId(replyMsg.getThreadId());
			}
		} else {
			// nuovo messaggio
			String forum = req.getParameter("forum");
			if (StringUtils.isEmpty(forum)) {
				forum = null;
			} else {
				forum = forum.replaceAll(">", "&gt;").replaceAll("<", "&lt;");
			}
			msg.setForum(forum);
			msg.setSubject(req.getParameter("subject").replaceAll(">", "&gt;").replaceAll("<", "&lt;"));
			msg.setThreadId(-1);
		}

		// incrementa il numero di messaggi scritti (anche per i modificati, dai ;) !)
		if (author.isValid()) {
			author.setMessages(author.getMessages() + 1);
			getPersistence().updateAuthor(author);
		}

		msg = getPersistence().insertMessage(msg);

		// redirect
		res.setContentType("text/plain");
		Writer out = res.getWriter();
		out.write("/Threads?action=getByThread&threadId=" + msg.getThreadId() + "&random=" + Math.random() + "#msg" + msg.getId());
		out.flush();
		out.close();

		return null;
	}

	public static Map<String, String> getEmoMap() {
		return new HashMap<String, String>(EMO_MAP);
	}

}
