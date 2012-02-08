package com.acmetoy.ravanator.fdt.servlets;

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
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.acmetoy.ravanator.fdt.MessageTag;
import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.google.gson.stream.JsonWriter;

public class Messages extends MainServlet {

	private static final long serialVersionUID = 1L;

	private static final Pattern PATTERN_QUOTE = Pattern.compile("<BR>(&gt;\\ ?)*");
	private static final Pattern PATTERN_YT = Pattern.compile("\\[yt\\]((.*?)\"(.*?))\\[/yt\\]");
	private static final Pattern PATTERN_YOUTUBE = Pattern.compile("(https?://)?(www|it)\\.youtube\\.com/watch\\?v=(.{11})");

	// key: filename, value[0]: edit value, value[1]: alt
	// tutte le emo ora sono in lower case
	private static final Map<String, String[]> EMO_MAP = new HashMap<String, String[]>();
	static {
		EMO_MAP.put("1", new String[] {" :)", "Sorride" });
		EMO_MAP.put("2", new String[] {" :d", "A bocca aperta"});
		EMO_MAP.put("3", new String[] {" ;)", "Occhiolino"});
		EMO_MAP.put("4", new String[] {" :o", "Sorpressa"});
		EMO_MAP.put("5", new String[] {" :p", "Con la lingua fuori"});
		EMO_MAP.put("6", new String[] {" :\\", "A bocca storta"});
		EMO_MAP.put("7", new String[] {" :@", "Arrabbiato"});
		EMO_MAP.put("8", new String[] {" :s", "Perplesso"});
		EMO_MAP.put("9", new String[] {" :$", "Imbarazzato"});
		EMO_MAP.put("10", new String[] {" :(", "Triste"});
		EMO_MAP.put("11", new String[] {":'(", "In lacrime"});
		EMO_MAP.put("12", new String[] {" :|", "Deluso"});
		EMO_MAP.put("13", new String[] {" 8)", "Ficoso"});
		EMO_MAP.put("angelo", new String[] {" o)", "Angioletto"});
		EMO_MAP.put("anonimo", new String[] {"(anonimo)", "Anonimo"});
		EMO_MAP.put("diavoletto", new String[] {" @^", "Indiavolato"});
		EMO_MAP.put("fantasmino", new String[] {"(ghost)", "Fantasma"});
		EMO_MAP.put("geek", new String[] {"(geek)", "Geek"});
		EMO_MAP.put("idea", new String[] {"(idea)", "Idea!"});
		EMO_MAP.put("love", new String[] {"(love)", "Innamorato"});
		EMO_MAP.put("loveamiga", new String[] {"(amiga)", "Fan Amiga"});
		EMO_MAP.put("loveapple", new String[] {"(apple)", "Fan Apple"});
		EMO_MAP.put("loveatari", new String[] {"(atari)", "Fan Atari"});
		EMO_MAP.put("lovec64", new String[] {"(c64)", "Fan Commodore64"});
		EMO_MAP.put("lovelinux", new String[] {"(linux)", "Fan Linux"});
		EMO_MAP.put("lovewin", new String[] {"(win)", "Fan Windows"});
		EMO_MAP.put("newbie", new String[] {"(newbie)", "Newbie, inesperto"});
		EMO_MAP.put("noia3", new String[] {" :-o", "Annoiato"});
		EMO_MAP.put("nolove", new String[] {"(nolove)", "Disinnamorato"});
		EMO_MAP.put("pirata", new String[] {" p)", "Pirata"});
		EMO_MAP.put("robot", new String[] {"(cylon)", "Cylon"});
		EMO_MAP.put("rotfl", new String[] {"(rotfl)", "Rotola dal ridere"});
		EMO_MAP.put("troll1", new String[] {"(troll1)", "Troll occhiolino"});
		EMO_MAP.put("troll2", new String[] {"(troll2)", "Troll chiacchierone"});
		EMO_MAP.put("troll3", new String[] {"(troll3)", "Troll occhi di fuori"});
		EMO_MAP.put("troll4", new String[] {"(troll4)", "Troll di tutti i colori"});
		EMO_MAP.put("troll", new String[] {"(troll)", "Troll"});
	}

	protected GiamboAction init = new GiamboAction("init", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			setWebsiteTitle(req, "Forum dei troll");
			return getByPage.action(req, res);
		}
	};

	/**
	 * I messaggi di questa pagina (Dimensione PAGE_SIZE) in ordine di data
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction getByPage = new GiamboAction("getByPage", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			req.setAttribute("messages", getPersistence().getMessagesByDate(PAGE_SIZE, getPageNr(req)));
			setWebsiteTitle(req, "Forum dei troll");
			setNavigationMessage(req, NavigationMessage.info("Ordinati cronologicamente"));
			return "messages.jsp";
		}
	};

	/**
	 * I messaggi di questo autore in ordine di data
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction getByAuthor = new GiamboAction("getByAuthor", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			String author = req.getParameter("author");
			addSpecificParam(req, "author", author);
			setWebsiteTitle(req, "Messaggi di " + author + " @ Forum dei Troll");
			setNavigationMessage(req, NavigationMessage.info("Messaggi scritti da <i>" + author + "</i>"));
			req.setAttribute("messages", getPersistence().getMessagesByAuthor(author, PAGE_SIZE, getPageNr(req)));
			return "messages.jsp";
		}
	};

	protected GiamboAction getByForum = new GiamboAction("getByForum", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			String forum = req.getParameter("forum");
			addSpecificParam(req, "forum", forum);
			setWebsiteTitle(req, forum + " @ Forum dei Troll");
			setNavigationMessage(req, NavigationMessage.info("Forum <i>" + forum + "</i>"));
			req.setAttribute("messages", getPersistence().getMessagesByForum(forum, PAGE_SIZE, getPageNr(req)));
			return "messages.jsp";
		}
	};

	/**
	 * Ricerca in tutti i messaggi
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction search = new GiamboAction("search", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			String search = req.getParameter("search");
			addSpecificParam(req, "search", search);
			setWebsiteTitle(req, "Ricerca di " + search + " @ Forum dei Troll");
			req.setAttribute("messages", getPersistence().searchMessages(search, PAGE_SIZE, getPageNr(req)));
			return "messages.jsp";
		}
	};

	/**
	 * Popola il div per la risposta/quota messaggio
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction newMessage = new GiamboAction("newMessage", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			MessageDTO msg = new MessageDTO();
			msg.setForum(req.getParameter("forum"));
			req.setAttribute("message", msg);
			setWebsiteTitle(req, "Nuovo messaggio @ Forum dei Troll");
			// faccine - ordinate per key
			TreeMap<String, String[]> emoMap = new TreeMap<String, String[]>(EMO_MAP);
			req.setAttribute("emoMap", emoMap);
			return "newMessage.jsp";
		}
	};

	/**
	 * Popola il div per la risposta/quota messaggio
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction showReplyDiv = new GiamboAction("showReplyDiv", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
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

			String author = msgDTO.getAuthor().getNick();
				text = "\r\nScritto da: " + (author != null ? author.trim() : "") + "\r\n> " + text + "\r\n";
				newMsg.setText(text);
				newMsg.setForum(msgDTO.getForum());
				newMsg.setSubject(msgDTO.getSubject());
			}
			newMsg.setParentId(parentId);
			req.setAttribute("message", newMsg);

			// faccine - ordinate per key
			TreeMap<String, String[]> emoMap = new TreeMap<String, String[]>(EMO_MAP);
			req.setAttribute("emoMap", emoMap);

			return "incReplyMessage.jsp";
		}
	};

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

		// testo al massimo di 10000 caratteri ...
		if (text.length() > 10000) {
			return "Sei piu' logorroico di una Wakka, stai sotto i 10000 caratteri !";
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
			if (subject.length() > 40) {
				return "LOL oggetto piu' lungo di 40 caratteri !";
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
	 * Modifica un messaggio esistente
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction editMessage = new GiamboAction("editMessage", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			// check se l'uente loggato corrisponde a chi ha scritto il messaggio
			AuthorDTO user = login(req);
			String msgId = req.getParameter("msgId");
			MessageDTO msg = getPersistence().getMessage(Long.parseLong(msgId));
			if (!user.isValid() || !user.getNick().equals(msg.getAuthor().getNick())) {
				setNavigationMessage(req, NavigationMessage.error("Non puoi editare un messaggio non tuo !"));
				return getByPage.action(req, res);
			}

			// cleanup
			msg.setText(msg.getText().replaceAll("<BR>", "\r\n"));
			req.setAttribute("message", msg);

			// faccine - ordinate per key
			TreeMap<String, String[]> emoMap = new TreeMap<String, String[]>(EMO_MAP);
			req.setAttribute("emoMap", emoMap);

			req.setAttribute("isEdit", true);

			return "newMessage.jsp";
		}
	};
	
	/**
	 * Crea la preview del messaggio
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	
	protected GiamboAction getMessagePreview = new GiamboAction("getMessagePreview", ONPOST) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			String text = req.getParameter("text");
			AuthorDTO author = login(req);
			// crea la preview del messaggio
			JsonWriter writer = new JsonWriter(res.getWriter());
			writer.beginObject();
			writer.name("resultCode").value("OK");
			writer.name("content").value(MessageTag.getMessage(text, null, author, null).replaceAll("\n", "<BR/>"));
			writer.endObject();
			writer.flush();
			writer.close();
			return null;
		}
	};

	/**
	 * Inserisce un messaggio nuovo o editato
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction insertMessage = new GiamboAction("insertMessage", ONPOST) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			try {
				return insertMessageAjax(req, res);
			} catch (Exception e) {
				StringBuilder body = new StringBuilder();
				body.append("<body>");
				body.append("<h1 style=\"color:#FFFFFF; background-color:#AA1111; padding: 5px 5px 5px 20px; margin-left: 20px;\">Errore !!!!1!</h1>");
				body.append("Che cazzo e' successo <img src=\"images/emo/7.gif\" /> ?!? Contatta subito <del>la suora</del> Giambo e mandagli questo messaggio:<br/>");
				body.append("<pre style=\"border:1px solid black; padding:10px;\">\"").append(ExceptionUtils.getStackTrace(e)).append("\"/></pre>");
				body.append("<div style=\"clear: both;\"></div>");
				body.append("</body>");
				JsonWriter writer = new JsonWriter(res.getWriter());
				writer.beginObject();
				writer.name("resultCode").value("ERROR");
				writer.name("content").value(body.toString());
				writer.endObject();
				writer.flush();
				writer.close();
			}
			return null;
		}
	};

	private String insertMessageAjax(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// se c'e' un'errore, mostralo
		String validationMessage = validateInsertMessage(req);
		if (validationMessage != null) {
			JsonWriter writer = new JsonWriter(res.getWriter());
			writer.beginObject();
			writer.name("resultCode").value("MSG");
			writer.name("content").value(validationMessage);
			writer.endObject();
			writer.flush();
			writer.close();
			return null;
		}

		final AuthorDTO author = login(req);
		if (!author.isValid()) {
			// la coppia nome utente/password non e` stata inserita e l'utente non e` loggato, ergo deve inserire il captcha giusto
			String captcha = req.getParameter("captcha");
			String correctAnswer = (String)req.getSession().getAttribute("captcha");
			if ((correctAnswer == null) || !correctAnswer.equals(captcha)) {
				//autenticazione fallita
				JsonWriter writer = new JsonWriter(res.getWriter());
				writer.beginObject();
				writer.name("resultCode").value("MSG");
				writer.name("content").value("Autenticazione / verifica captcha fallita");
				writer.endObject();
				writer.flush();
				writer.close();
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
				text = m.replaceFirst("[yt]"+Matcher.quoteReplacement(m.group(3))+"[/yt]");
				m = PATTERN_YOUTUBE.matcher(text);
		}

		// reply o messaggio nuovo ?
		long parentId = Long.parseLong(req.getParameter("parentId"));
		MessageDTO msg = new MessageDTO();
		msg.setAuthor(author);
		msg.setParentId(parentId);
		msg.setDate(new Date());
		msg.setText(text);
		if (parentId > 0) {
			long id = Long.parseLong(req.getParameter("id"));
			if (id > -1) {
				// modify
				msg = getPersistence().getMessage(id);
				if (msg.getAuthor() == null || !msg.getAuthor().getNick().equals(author.getNick())) {
					JsonWriter writer = new JsonWriter(res.getWriter());
					writer.beginObject();
					writer.name("resultCode").value("MSG");
					writer.name("content").value("Imbroglione, non puoi modificare questo messaggio !");
					writer.endObject();
					writer.flush();
					writer.close();
					return null;
				}
				msg.setSubject(req.getParameter("subject").replaceAll(">", "&gt;").replaceAll("<", "&lt;"));
				text += "<BR><BR><b>**Modificato dall'autore il " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()) + "**</b>";
				msg.setText(text);
			} else {
				// reply
				MessageDTO replyMsg = getPersistence().getMessage(parentId);
				msg.setForum(replyMsg.getForum());
				if (replyMsg.getId() == replyMsg.getParentId()) {
					msg.setSubject("Re: " + replyMsg.getSubject());
				} else {
					msg.setSubject(replyMsg.getSubject());
				}
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
		JsonWriter writer = new JsonWriter(res.getWriter());
		writer.beginObject();
		writer.name("resultCode").value("OK");
		StringBuilder content = new StringBuilder();
		if (req.getServletPath().endsWith("/Threads")) {
			content.append("/Threads?action=getByThread&threadId=").append(msg.getThreadId());
		} else {
			content.append("/Messages?action=init");
		}
		content.append("&rnd=").append(System.currentTimeMillis());
		content.append("#msg").append(msg.getId());
		writer.name("content").value(content.toString());
		writer.endObject();
		writer.flush();
		writer.close();
		return null;
	}

	public static Map<String, String[]> getEmoMap() {
		return new HashMap<String, String[]>(EMO_MAP);
	}
	
	private void addSpecificParam(HttpServletRequest req, String key, String value) {
		Map<String, String> specificParams = (Map<String, String>)req.getAttribute("specificParams");
		if (specificParams == null) {
			specificParams = new HashMap<String, String>();
			req.setAttribute("specificParams", specificParams);
		}
		specificParams.put(key, value);
	}

}
