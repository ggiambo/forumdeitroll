package com.acmetoy.ravanator.fdt.servlets;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.acmetoy.ravanator.fdt.MessageTag;
import com.acmetoy.ravanator.fdt.RandomPool;
import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.acmetoy.ravanator.fdt.persistence.MessagesDTO;
import com.acmetoy.ravanator.fdt.persistence.QuoteDTO;
import com.acmetoy.ravanator.fdt.persistence.SearchMessagesSort;
import com.google.gson.stream.JsonWriter;

public class Messages extends MainServlet {

	private static final long serialVersionUID = 1L;

	private static final Pattern PATTERN_QUOTE = Pattern.compile("<BR>(&gt;\\ ?)*");
	private static final Pattern PATTERN_YT = Pattern.compile("\\[yt\\]((.*?)\"(.*?))\\[/yt\\]");
	private static final Pattern PATTERN_YOUTUBE = Pattern.compile("(https?://)?(www|it)\\.youtube\\.com/watch\\?(\\S+&)?v=(\\S{7,11})");

	public static final String ANTI_XSS_TOKEN = "anti_xss_token";

	// key: filename, value[0]: edit value, value[1]: alt
	// tutte le emo ora sono in lower case
	private static final Map<String, String[]> EMO_MAP = new HashMap<String, String[]>();
	static {
		EMO_MAP.put("1", new String[] {" :)", "Sorride" });
		EMO_MAP.put("2", new String[] {" :d", "A bocca aperta"});
		EMO_MAP.put("3", new String[] {" ;)", "Occhiolino"});
		EMO_MAP.put("4", new String[] {" :o", "Sorpresa"});
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

	public static final int MAX_MESSAGE_LENGTH = 40000;
	public static final int MAX_SUBJECT_LENGTH = 40;

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
			return initWithMessage(req, res, NavigationMessage.info("Cronologia messaggi"));
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
			MessagesDTO messages = getPersistence().getMessagesByAuthor(author, PAGE_SIZE, getPageNr(req));
			req.setAttribute("messages", messages.getMessages());
			req.setAttribute("maxNrOfMessages", messages.getMaxNrOfMessages());
			req.getSession().setAttribute(ANTI_XSS_TOKEN, RandomPool.getString(3));
			return "messages.jsp";
		}
	};

	/**
	 * I messaggi di questo forum in ordine di data
	 * Se il parametro forum e` la stringa vuota restituisce i soli messaggi del forum principale (NULL)
	 */
	protected GiamboAction getByForum = new GiamboAction("getByForum", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			String forum = req.getParameter("forum");
			req.setAttribute("navType", "crono");
			if (StringUtils.isEmpty(forum)) {
				setWebsiteTitle(req, "Forum Principale @ Forum dei Troll");
				forum = "";
				req.setAttribute("navForum", "Principale");
			} else {
				setWebsiteTitle(req, forum + " @ Forum dei Troll");
				req.setAttribute("navForum", forum);
			}

			addSpecificParam(req, "forum", forum);
			setNavigationMessage(req, NavigationMessage.info("Cronologia messaggi"));
			MessagesDTO messages = getPersistence().getMessagesByForum(forum, PAGE_SIZE, getPageNr(req));
			req.setAttribute("messages", messages.getMessages());
			req.setAttribute("maxNrOfMessages", messages.getMaxNrOfMessages());
			req.getSession().setAttribute(ANTI_XSS_TOKEN, RandomPool.getString(3));
			return "messages.jsp";
		}
	};

	/**
	 * Questo singolo messaggio
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction getById = new GiamboAction("getById", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			Long msgId = Long.parseLong(req.getParameter("msgId"));
			setWebsiteTitle(req, "Singolo messaggio @ Forum dei Troll");
			List<MessageDTO> messages = new ArrayList<MessageDTO>();
			messages.add(getPersistence().getMessage(msgId));
			req.setAttribute("messages",messages);
			req.getSession().setAttribute(ANTI_XSS_TOKEN, RandomPool.getString(3));
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
			final String search = req.getParameter("search");
			final String sort = req.getParameter("sort");

			addSpecificParam(req, "search", search);
			addSpecificParam(req, "sort", sort);

			setWebsiteTitle(req, "Ricerca di " + search + " @ Forum dei Troll");

			req.setAttribute("messages", getPersistence().searchMessages(search, SearchMessagesSort.parse(sort), PAGE_SIZE, getPageNr(req)));
			req.getSession().setAttribute(ANTI_XSS_TOKEN, RandomPool.getString(3));
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
			MessageDTO msgDTO = getPersistence().getMessage(parentId);
			if ("quote".equals(type)) {
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
			}
			newMsg.setForum(msgDTO.getForum());
			// setta il subject: aggiungi "Re: " e se necessario tronca a 40 caratteri
			String subject = msgDTO.getSubject();
			if (!subject.startsWith("Re:")) {
				subject = "Re: " + subject;
			}
			newMsg.setSubject(subject.substring(0, Math.min(MAX_SUBJECT_LENGTH, subject.length())));
			newMsg.setParentId(parentId);
			req.setAttribute("message", newMsg);

			// faccine - ordinate per key
			TreeMap<String, String[]> emoMap = new TreeMap<String, String[]>(EMO_MAP);
			req.setAttribute("emoMap", emoMap);

			//jstl non accede ai campi stitici
			req.setAttribute("MAX_MESSAGE_LENGTH", MAX_MESSAGE_LENGTH);
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
		if (text.length() > MAX_MESSAGE_LENGTH) {
			return "Sei piu' logorroico di una Wakka, stai sotto i 10000 caratteri !";
		}

		// subject almeno di 5 caratteri, cribbio !
		try {
			Long.parseLong(req.getParameter("parentId"));
		} catch (NumberFormatException e) {
			return "Il valore " + req.getParameter("parentId") + " assomiglia poco a un numero ...";
		}
		String subject = req.getParameter("subject");
		if (StringUtils.isEmpty(subject) || subject.trim().length() < 3) {
			return "Oggetto di almeno di 3 caratteri, cribbio !";
		}
		if (subject.length() > MAX_SUBJECT_LENGTH) {
			return "LOL oggetto pié lungo di " + MAX_SUBJECT_LENGTH + " caratteri !";
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
				return initWithMessage(req, res, NavigationMessage.error("Non puoi editare un messaggio non tuo !"));
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
			// replace dei caratteri HTML
			text = text.replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\n", "<BR>");

			// restore <i>, <b>, <u> e <s>
			for (String t : new String[] {"i", "b", "u", "s"}) {
				text = text.replaceAll("(?i)&lt;" + t + "&gt;", "<" + t + ">");
				text = text.replaceAll("(?i)&lt;/" + t + "&gt;", "</" + t + ">");
			}
			writer.name("content").value(MessageTag.getMessage(text, null, author, null));
			writer.endObject();
			writer.flush();
			writer.close();
			return null;
		}
	};
	
	/**
	 * 
	 */
	protected GiamboAction getSingleMessageContent = new GiamboAction("getSingleMessageContent", ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			String msgId = req.getParameter("msgId");
			MessageDTO msg = getPersistence().getMessage(Long.parseLong(msgId));
			// crea la preview del messaggio
			JsonWriter writer = new JsonWriter(res.getWriter());
			writer.beginObject();
			writer.name("resultCode").value("OK");
			writer.name("content").value(MessageTag.getMessage(msg.getText(), null, msg.getAuthor(), null));
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

		AuthorDTO loggedUser = (AuthorDTO)req.getSession().getAttribute(LOGGED_USER_SESSION_ATTR);
		AuthorDTO author = null;
		String nick = req.getParameter("nick");
		String pass = req.getParameter("pass");
		if (loggedUser != null && loggedUser.getNick().equalsIgnoreCase(nick)) {
			// posta come utente loggato
			author = loggedUser;
		} else if (StringUtils.isNotEmpty(nick) && StringUtils.isNotEmpty(pass)) {
			AuthorDTO sockpuppet = getPersistence().getAuthor(nick);
			if (sockpuppet.passwordIs(pass)) {
				// posta come altro utente
				author = sockpuppet;
			}
		} else {
			// la coppia nome utente/password non e` stata inserita e l'utente non e` loggato, ergo deve inserire il captcha giusto
			String captcha = req.getParameter("captcha");
			String correctAnswer = (String)req.getSession().getAttribute("captcha");
			if (StringUtils.isNotEmpty(correctAnswer) && correctAnswer.equals(captcha)) {
				// posta da anonimo
				author = new AuthorDTO();
			}
		}

		if (author == null) {
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
				text = m.replaceFirst("[yt]"+Matcher.quoteReplacement(m.group(4))+"[/yt]");
				m = PATTERN_YOUTUBE.matcher(text);
		}

		// reply o messaggio nuovo ?
		long parentId = Long.parseLong(req.getParameter("parentId"));
		MessageDTO msg = new MessageDTO();
		msg.setAuthor(author);
		msg.setParentId(parentId);
		msg.setDate(new Date());
		msg.setText(text);
		msg.setSubject(req.getParameter("subject").replaceAll(">", "&gt;").replaceAll("<", "&lt;"));
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
				text += "<BR><BR><b>**Modificato dall'autore il " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()) + "**</b>";
				msg.setText(text);
			} else {
				// reply
				MessageDTO replyMsg = getPersistence().getMessage(parentId);
				msg.setForum(replyMsg.getForum());
				msg.setThreadId(replyMsg.getThreadId());
				// incrementa il numero di messaggi scritti
				if (author.isValid()) {
					author.setMessages(author.getMessages() + 1);
					getPersistence().updateAuthor(author);
				}
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
			msg.setThreadId(-1);
			// incrementa il numero di messaggi scritti
			if (author.isValid()) {
				author.setMessages(author.getMessages() + 1);
				getPersistence().updateAuthor(author);
			}
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

	protected void forShame(final AuthorDTO author, final String shameTitle, final String shameMessage) {
		final MessageDTO msg = new MessageDTO();
		msg.setAuthor(author);
		msg.setParentId(-1);
		msg.setDate(new Date());
		msg.setText(shameMessage);
		msg.setSubject(shameTitle);
		msg.setForum("FreeBan");
		msg.setThreadId(-1);
		getPersistence().insertMessage(msg);
	}

	protected GiamboAction pedonizeThreadTree = new GiamboAction("pedonizeThreadTree", ONGET) {
		@Override
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			AuthorDTO loggedUser = (AuthorDTO)req.getSession().getAttribute(LOGGED_USER_SESSION_ATTR);
			if (loggedUser == null) {
				return initWithMessage(req, res, NavigationMessage.error("Non furmigare !"));
			}
			boolean isAdmin = "yes".equals(getPersistence().getPreferences(loggedUser).get("pedonizeThread"));
			if (! isAdmin) {
				return initWithMessage(req, res, NavigationMessage.error("Non furmigare "+loggedUser.getNick()+" !!!"));
			}

			final String token = (String)req.getSession().getAttribute(ANTI_XSS_TOKEN);
			final String inToken = req.getParameter("token");

			if ((token == null) || (inToken == null) || !token.equals(inToken)) {
				return initWithMessage(req, res, NavigationMessage.error("Verifica token fallita"));
			}

			final long rootMessageId = Long.parseLong(req.getParameter("rootMessageId"));

			getPersistence().pedonizeThreadTree(rootMessageId);

			{ // moderator shaming block
				final MessageDTO movedMessage = getPersistence().getMessage(rootMessageId);
				forShame(loggedUser, "Pe: " + movedMessage.getSubject(), "" + loggedUser.getNick() + " ha spostato in procura il thread " + movedMessage.getSubject() + " http://forumdeitroll.com/Threads?action=getByThread&threadId=" + rootMessageId);
			}

			setNavigationMessage(req, NavigationMessage.info("Pedonization completed."));
			res.sendRedirect("Threads");
			return null;
		}
	};

	protected GiamboAction getRandomQuote = new GiamboAction("getRandomQuote", ONGET) {
		@Override
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			// già escapato da getRandomQuote di MainServlet
			res.setContentType("text/plain");
			QuoteDTO quote = getRandomQuote(req, res);
			res.getWriter().write(quote.getContent()+'\n'+quote.getNick());
			return null;
		}
	};

	private String initWithMessage(HttpServletRequest req, HttpServletResponse res, NavigationMessage message) throws Exception {
		boolean hideProcCatania = StringUtils.isNotEmpty(login(req).getPreferences().get(User.PREF_HIDE_PROC_CATANIA));
		MessagesDTO messages = getPersistence().getMessagesByDate(PAGE_SIZE, getPageNr(req), hideProcCatania);
		req.setAttribute("messages", messages.getMessages());
		req.setAttribute("maxNrOfMessages", messages.getMaxNrOfMessages());

		req.setAttribute("navType", "crono");
		req.setAttribute("navForum", "");

		setWebsiteTitle(req, "Forum dei troll");
		setNavigationMessage(req, message);
		req.getSession().setAttribute(ANTI_XSS_TOKEN, RandomPool.getString(3));
		return "messages.jsp";
	}

}
