package com.forumdeitroll.servlets;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.forumdeitroll.MessageTag;
import com.forumdeitroll.PasswordUtils;
import com.forumdeitroll.markup.InputSanitizer;
import com.forumdeitroll.markup.RenderOptions;
import com.forumdeitroll.markup.Renderer;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.IPersistence;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.MessagesDTO;
import com.forumdeitroll.persistence.QuoteDTO;
import com.forumdeitroll.persistence.TagDTO;
import com.forumdeitroll.profiler.UserProfile;
import com.forumdeitroll.profiler.UserProfiler;
import com.forumdeitroll.servlets.Action.Method;
import com.forumdeitroll.util.CacheTorExitNodes;
import com.forumdeitroll.util.IPMemStorage;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

public class Messages extends MainServlet {
	private static final long serialVersionUID = 1L;

	private static final List<String> REFRESHABLE_ACTIONS = Arrays.asList("getMessages");

	private static final Pattern PATTERN_QUOTE = Pattern.compile("<BR> *(&gt;\\ ?)*");

	public static final int MAX_MESSAGE_LENGTH = 40000;
	public static final int MAX_SUBJECT_LENGTH = 80;

	protected static final Set<String> BANNED_IPs = new HashSet<String>();

	@Action
	@Override
	String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// redirect
		res.setHeader("Location", "Messages?action=getMessages");
		res.sendError(301);
		return null;
	}

	@Override
	public void doBefore(HttpServletRequest req, HttpServletResponse res) {
		if (REFRESHABLE_ACTIONS.contains(req.getAttribute("action")) && StringUtils.isEmpty(req.getParameter("page"))) {
			req.setAttribute("refreshable", "1");
		}
	}

	@Override
	public void doAfter(HttpServletRequest req, HttpServletResponse res) {
		AuthorDTO author = (AuthorDTO) req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (author != null) {
			req.setAttribute("notifications", getPersistence().getNotifications(null, author.getNick()));
		}
	}

	/**
	 * I messaggi di questa pagina (Dimensione PAGE_SIZE) in ordine di data
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action
	String getMessages(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return getMessages(req, res, NavigationMessage.info("Cronologia messaggi"));
	}

	/**
	 * I messaggi di questo autore in ordine di data
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action
	String getByAuthor(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String author = req.getParameter("author");
		addSpecificParam(req, "author", author);
		String forum = req.getParameter("forum");
		addSpecificParam(req, "forum", forum);
		setWebsiteTitle(req, "Messaggi di " + author + " @ Forum dei Troll");
		setNavigationMessage(req, NavigationMessage.info("Messaggi scritti da <i>" + author + "</i>"));
		MessagesDTO messages = getPersistence().getMessages(forum, author, PAGE_SIZE, getPageNr(req), hideProcCatania(req));
		req.setAttribute("messages", messages.getMessages());
		req.setAttribute("resultSize", messages.getMessages().size());
		req.setAttribute("totalSize", messages.getMaxNrOfMessages());
		setAntiXssToken(req);
		return "messages.jsp";
	}

	/**
	 * I messaggi di questo forum in ordine di data
	 * Se il parametro forum e` la stringa vuota restituisce i soli messaggi del forum principale (NULL)
	 */
	@Action
	String getByForum(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String forum = req.getParameter("forum");
		if (StringUtils.isEmpty(forum)) {
			setWebsiteTitle(req, "Forum Principale @ Forum dei Troll");
		} else {
			setWebsiteTitle(req, forum + " @ Forum dei Troll");
		}

		addSpecificParam(req, "forum", forum);
		setNavigationMessage(req, NavigationMessage.info("Cronologia messaggi"));
		MessagesDTO messages = getPersistence().getMessages(forum, null, PAGE_SIZE, getPageNr(req), false);
		req.setAttribute("messages", messages.getMessages());
		req.setAttribute("resultSize", messages.getMessages().size());
		req.setAttribute("totalSize", messages.getMaxNrOfMessages());
		setAntiXssToken(req);
		return "messages.jsp";
	}

	/**
	 * Questo singolo messaggio
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action
	String getById(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Long msgId = Long.parseLong(req.getParameter("msgId"));
		setWebsiteTitle(req, "Singolo messaggio @ Forum dei Troll");
		List<MessageDTO> messages = new ArrayList<MessageDTO>();
		messages.add(getPersistence().getMessage(msgId));
		req.setAttribute("messages", messages);
		req.setAttribute("resultSize", messages.size());
		setAntiXssToken(req);

		// request from a notification ?
		String notificationId = req.getParameter("notificationId");
		String fromNick = req.getParameter("notificationFromNick");
		final AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(LOGGED_USER_REQ_ATTR);
		if (StringUtils.isNotEmpty(notificationId) && StringUtils.isNotEmpty(fromNick) && loggedUser.isValid()) {
			// remove this notification once clicked
			try {
				long id = Long.parseLong(notificationId);
				getPersistence().removeNotification(fromNick, loggedUser.getNick(), id);
			} catch (NumberFormatException e) {
				// Ma che c'� frega ma che ce 'mporta ...
			}
		}

		return "messages.jsp";
	}

	/**
	 * Ricerca in tutti i messaggi
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action
	String search(HttpServletRequest req, HttpServletResponse res) throws Exception {
		final String search = req.getParameter("search");
		final String sort = req.getParameter("sort");

		addSpecificParam(req, "search", search);
		addSpecificParam(req, "sort", sort);

		setWebsiteTitle(req, "Ricerca di " + search + " @ Forum dei Troll");

//		List<MessageDTO> messages = getPersistence().searchMessages(search, SearchMessagesSort.parse(sort), PAGE_SIZE, getPageNr(req));
//		req.setAttribute("messages", messages);
//		req.setAttribute("resultSize", messages.size());
		setAntiXssToken(req);
		return "messages.jsp";
	}

	/**
	 * Popola il div per la risposta/quota messaggio
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action
	String newMessage(HttpServletRequest req, HttpServletResponse res) throws Exception {
		MessageDTO msg = new MessageDTO();
		msg.setForum(req.getParameter("forum"));
		req.setAttribute("message", msg);
		setWebsiteTitle(req, "Nuovo messaggio @ Forum dei Troll");
		// faccine - ordinate per key
		return "newMessage.jsp";
	}

	/**
	 * Popola il div per la risposta/quota messaggio
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action
	String showReplyDiv(HttpServletRequest req, HttpServletResponse res) throws Exception {
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

		//jstl non accede ai campi stitici
		req.setAttribute("MAX_MESSAGE_LENGTH", MAX_MESSAGE_LENGTH);

		getServletContext().getRequestDispatcher("/pages/messages/incReplyMessage.jsp").forward(req, res);
		return null;
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

		// testo al massimo di 10000 caratteri ...
		if (text.length() > MAX_MESSAGE_LENGTH) {
			return "Sei piu' logorroico di una Wakka, stai sotto i " + MAX_MESSAGE_LENGTH + " caratteri !";
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
			return "LOL oggetto piu' lungo di " + MAX_SUBJECT_LENGTH + " caratteri !";
		}

		// qualcuno prova a creare un forum ;) ?
		String forum = req.getParameter("forum");
		if (!StringUtils.isEmpty(forum) && !getPersistence().getForums().contains(forum)) {
			return "Ma che cacchio di forum e' '" + forum + "' ?!?";
		}

		if ((forum != null) && forum.equals(IPersistence.FORUM_ASHES)) {
			return "Postare nel forum " + IPersistence.FORUM_ASHES + " e' vietato e stupido";
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
	@Action
	String editMessage(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// check se l'uente loggato corrisponde a chi ha scritto il messaggio
		AuthorDTO user = login(req);
		String msgId = req.getParameter("msgId");
		MessageDTO msg = getPersistence().getMessage(Long.parseLong(msgId));
		if (!user.isValid() || !user.getNick().equals(msg.getAuthor().getNick())) {
			return getMessages(req, res, NavigationMessage.error("Non puoi editare un messaggio non tuo !"));
		}

		// cleanup
		msg.setText(msg.getText().replaceAll("<BR>", "\r\n"));
		req.setAttribute("message", msg);

		req.setAttribute("isEdit", true);

		return "newMessage.jsp";
	}

	/**
	 * Crea la preview del messaggio
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action(method=Method.POST)
	String getMessagePreview(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String text = req.getParameter("text");
		AuthorDTO author = login(req);
		// crea la preview del messaggio
		JsonWriter writer = new JsonWriter(res.getWriter());
		writer.beginObject();
		writer.name("resultCode").value("OK");

		text = InputSanitizer.sanitizeText(text);


		writer.name("content").value(MessageTag.getMessagePreview(text, author, author));
		writer.endObject();
		writer.flush();
		writer.close();
		return null;
	}

	/**
	 * contenuto di un singolo messaggio
	 */
	@Action(method=Method.GET)
	String getSingleMessageContent(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String msgId = req.getParameter("msgId");
		MessageDTO msg = getPersistence().getMessage(Long.parseLong(msgId));
		// crea la preview del messaggio
		JsonWriter writer = new JsonWriter(res.getWriter());
		writer.beginObject();
		writer.name("resultCode").value("OK");

		RenderOptions opts = new RenderOptions();
		opts.authorIsAnonymous = msg.getAuthor() == null;
		opts.collapseQuotes = "yes".equals(req.getParameter(User.PREF_COLLAPSE_QUOTES));
		opts.embedYoutube = "yes".equals(req.getParameter(User.PREF_EMBEDDYT));
		opts.showImagesPlaceholder = "yes".equals(req.getParameter(User.PREF_SHOWANONIMG));
		StringReader in = new StringReader(msg.getText());
		StringWriter out = new StringWriter();
		Renderer.render(in, out, opts);

		writer.name("content").value(out.toString());
		writer.endObject();
		writer.flush();
		writer.close();
		return null;
	}

	/**
	 * Inserisce un messaggio nuovo o editato
	 */
	@Action(method=Method.POST)
	String insertMessage(HttpServletRequest req, HttpServletResponse res) throws Exception {
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

	protected AuthorDTO insertMessageAuthentication(final HttpServletRequest req) throws Exception {
		final AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(LOGGED_USER_REQ_ATTR);
		String nick = req.getParameter("nick");
		String pass = req.getParameter("pass");
		if (loggedUser != null && loggedUser.getNick() != null && loggedUser.getNick().equalsIgnoreCase(nick)) {
			// posta come utente loggato
			return loggedUser;
		} else if ((loggedUser != null) && StringUtils.isEmpty(nick)) {
			// utente loggato che posta come anonimo
			return new AuthorDTO(loggedUser);
		} else if (StringUtils.isNotEmpty(nick) && StringUtils.isNotEmpty(pass)) {
			AuthorDTO sockpuppet = getPersistence().getAuthor(nick);
			if (PasswordUtils.hasUserPassword(sockpuppet, pass)) {
				// posta come altro utente
				return sockpuppet;
			}
		} else {
			// se non e` stato inserito nome utente/password e l'utente non e` loggato
			String captcha = req.getParameter("captcha");
			String correctAnswer = (String)req.getSession().getAttribute("captcha");
			if (StringUtils.isNotEmpty(correctAnswer) && correctAnswer.equals(captcha)) {
				// posta da anonimo
				return new AuthorDTO(loggedUser);
			}
		}

		// autenticazione fallita, no utente loggato, no username/password inserita e no captcha corretto
		return null;
	}

	protected void insertMessageAjaxBan(final HttpServletResponse res) throws IOException {
		JsonWriter writer = new JsonWriter(res.getWriter());
		writer.beginObject();
		writer.name("resultCode").value("BAN");
		writer.name("content").value("Sei stato bannato");
		writer.endObject();
		writer.flush();
		writer.close();
	}

	protected void insertMessageAjaxFail(final HttpServletResponse res, final String error) throws IOException {
		JsonWriter writer = new JsonWriter(res.getWriter());
		writer.beginObject();
		writer.name("resultCode").value("MSG");
		writer.name("content").value(error);
		writer.endObject();
		writer.flush();
		writer.close();
	}

	protected boolean authorIsBanned(final AuthorDTO author, final HttpServletRequest req) {
		if (author.isBanned()) return true;
		if (req.getSession().getAttribute(SESSION_IS_BANNED) != null) return true;

		final String ip = IPMemStorage.requestToIP(req);

		if (BANNED_IPs.contains(ip)) return true;

		// check se ANOnimo usa TOR
		if (!author.isValid()) {
			if (getPersistence().blockTorExitNodes()) {
				if (CacheTorExitNodes.check(ip)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Inserisce un messaggio nuovo o editato
	 */
	private String insertMessageAjax(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// se c'e' un'errore, mostralo
		String validationMessage = validateInsertMessage(req);
		if (validationMessage != null) {
			insertMessageAjaxFail(res, validationMessage);
			return null;
		}

		final AuthorDTO author = insertMessageAuthentication(req);

		if (author == null) {
			insertMessageAjaxFail(res, "Autenticazione / verifica captcha fallita");
			return null;
		}

		if (authorIsBanned(author, req)) {
			insertMessageAjaxBan(res);
			return null;
		}

		UserProfile profile = null;
		boolean bannato = false;
		try {
			// un errore nel profiler non preclude la funzionalita' del forum, ma bisogna tenere d'occhio i logs
			UserProfile candidate = new Gson().fromJson(req.getParameter("jsonProfileData"), UserProfile.class);
			candidate.setIpAddress(req.getHeader("X-Forwarded-For") != null ? req.getHeader("X-Forwarded-For") : req.getRemoteAddr());
			candidate.setNick(author.getNick());
			profile = UserProfiler.getInstance().guess(candidate);
			if (profile.isBannato()) {
				insertMessageAjaxBan(res);
				Logger.getLogger(Messages.class).info(
						"E` stato riconosciuto come bannato il seguente profilo utente: "+new Gson().toJson(candidate));
				Logger.getLogger(Messages.class).info(
						"Il profilo utente a cui e` stato associato è "+new Gson().toJson(profile));
				bannato = true;
			}
		} catch (Exception e) {
			Logger.getLogger(Messages.class).error("ERRORE IN PROFILAZIONE!! "+e.getClass().getName() + ": "+e.getMessage(), e);
			insertMessageAjaxFail(res, "Errore durante l'inserimento del messaggio. La suora sa perché.");
			bannato = true;
		}

		req.getSession().removeAttribute("captcha");

		String text = req.getParameter("text");

		text = InputSanitizer.sanitizeText(text);

		// reply o messaggio nuovo ?
		long parentId = Long.parseLong(req.getParameter("parentId"));
		MessageDTO msg = new MessageDTO();
		msg.setAuthor(author);
		msg.setParentId(parentId);
		msg.setDate(new Date());
		msg.setText(text);
		if (bannato) {
			msg.setIsVisible(-1);
		}
		msg.setSubject(InputSanitizer.sanitizeSubject(req.getParameter("subject")));
		if (parentId > 0) {
			long id = Long.parseLong(req.getParameter("id"));
			if (id > -1) {
				// modify
				msg = getPersistence().getMessage(id);
				if (msg.getAuthor() == null || !msg.getAuthor().getNick().equals(author.getNick())) {
					insertMessageAjaxFail(res, "Imbroglione, non puoi modificare questo messaggio !");
					return null;
				}
				text += "<BR><BR><b>**Modificato dall'autore il " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()) + "**</b>";
				msg.setText(text);
				msg.setSubject(InputSanitizer.sanitizeSubject(req.getParameter("subject")));
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

			if (bannato) {
				restoreTitleFromParent(msg, parentId);
			}
		} else {
			// nuovo messaggio
			String forum = req.getParameter("forum");
			if (StringUtils.isEmpty(forum)) {
				forum = null;
			} else {
				forum = InputSanitizer.sanitizeForum(forum);
			}
			if (bannato) {
				forum = IPersistence.FORUM_ASHES;
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
		String m_id = Long.toString(msg.getId());
		IPMemStorage.store(req, m_id, author);
		try {
			if (profile != null)
				UserProfiler.getInstance().bind(profile, m_id);
		} catch (Exception e) {

		}

		// redirect
		JsonWriter writer = new JsonWriter(res.getWriter());
		writer.beginObject();
		writer.name("resultCode").value("OK");
		StringBuilder content = new StringBuilder();

		/*
		Costruiamo l'url di destinazione a partire dall'url da cui e` stato effettuato il submit -- sarrusofono
		*/

		final String submitLocation = req.getParameter("submitLocation");
		//System.out.println("submitLocation [" + submitLocation + "]");
		URI submitURI = null;
		try {
			submitURI = new URI(submitLocation);
		} catch (URISyntaxException e) {
			content.append("/Messages?action=init");
		}

		if (submitURI != null) {
			if (submitURI.getPath().endsWith("/Threads")) {
				content.append("/Threads?action=getByThread&threadId=").append(msg.getThreadId());
			} else if (submitURI.getPath().endsWith("/Messages")) {
				final String rawQuery = submitURI.getQuery();
				String navForum = null;
				if (rawQuery != null) {
					final String[] query = rawQuery.split("&");
					for (final String argval: query) {
						if (argval.startsWith("forum=")) {
							navForum = argval;
							break;
						}
					}
				}

				if (navForum == null) {
					content.append("/Messages?action=init");
				} else {
					content.append("/Messages?action=getByForum&" + navForum);
				}
			} else {
				content.append("/Messages?action=init");
			}
		}

		content.append("&rnd=").append(System.currentTimeMillis());
		content.append("#msg").append(msg.getId());
		writer.name("content").value(content.toString());
		writer.endObject();
		writer.flush();
		writer.close();
		return null;
	}

	protected void restoreTitleFromParent(final MessageDTO msg, final long parentId) {
		final MessageDTO parent = getPersistence().getMessage(parentId);
		if (parent == null) return;
		msg.setSubject(parent.getSubjectReal());
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

	/**
	 * Riassegnaa questo messaggio e tutti i suoi figli il forum "Proc di Catania"
	 */
	@Action(method=Method.GET)
	String pedonizeThreadTree(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(LOGGED_USER_REQ_ATTR);
		if (loggedUser == null) {
			return getMessages(req, res, NavigationMessage.error("Non furmigare !"));
		}
		boolean isAdmin = "yes".equals(getPersistence().getPreferences(loggedUser).get("pedonizeThread"));
		if (! isAdmin) {
			return getMessages(req, res, NavigationMessage.error("Non furmigare "+loggedUser.getNick()+" !!!"));
		}

		if (!antiXssOk(req)) {
			return getMessages(req, res, NavigationMessage.error("Verifica token fallita"));
		}

		final long rootMessageId = Long.parseLong(req.getParameter("rootMessageId"));

		getPersistence().moveThreadTree(rootMessageId, IPersistence.FORUM_PROC);

		{ // moderator shaming block
			final MessageDTO movedMessage = getPersistence().getMessage(rootMessageId);
			StringBuilder msg = new StringBuilder(loggedUser.getNick());
			msg.append(" ha spostato in procura questo [url=");
			msg.append("Threads?action=getByThread&threadId=").append(rootMessageId);
			msg.append("]thread[/url]");
			forShame(loggedUser, "Pe: " + movedMessage.getSubject(), msg.toString());
		}

		setNavigationMessage(req, NavigationMessage.info("Pedonization completed."));
		// redirect
		res.setHeader("Location", "Threads");
		res.sendError(301);
		return null;
	}

	/**
	 * Nascondi questo orrifico messaggio agli occhi dei poveri troll.
	 */
	@Action(method=Method.GET)
	String hideMessage(HttpServletRequest req, HttpServletResponse res) throws Exception {
        return restoreOrHideMessage(req, res, Long.parseLong(req.getParameter("msgId")), 0);
	}

	/**
	 * Abilita alla visione questo angelico messaggio
	 */
	@Action(method=Method.GET)
	String restoreHiddenMessage(HttpServletRequest req, HttpServletResponse res) throws Exception {
        return restoreOrHideMessage(req, res, Long.parseLong(req.getParameter("msgId")), 1);
	}

	private String restoreOrHideMessage(HttpServletRequest req, HttpServletResponse res, long msgId, int visible)  throws Exception {
    	AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(LOGGED_USER_REQ_ATTR);
    	if (loggedUser == null) {
    		return getMessages(req, res, NavigationMessage.error("Non furmigare !"));
    	}
    	boolean isAdmin = "yes".equals(getPersistence().getPreferences(loggedUser).get("hideMessages"));
    	if (! isAdmin) {
    		return getMessages(req, res, NavigationMessage.error("Non furmigare "+loggedUser.getNick()+" !!!"));
    	}

    	if (!antiXssOk(req)) {
    		return getMessages(req, res, NavigationMessage.error("Verifica token fallita"));
    	}

    	getPersistence().restoreOrHideMessage(msgId, visible);

    	return getMessages(req, res, NavigationMessage.info("Messaggio infernale nascosto agli occhi dei giovini troll."));
	}

	/**
	 * Ritorna una frase celebre a caso
	 */
	@Action(method=Method.GET)
	String getRandomQuote(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// già escapato da getRandomQuote di MainServlet
		res.setContentType("text/plain");
		QuoteDTO quote = getRandomQuoteDTO(req, res);
		res.getWriter().write(quote.getContent()+'\n'+quote.getNick());
		return null;
	}

	/**
	 * up/downVote
	 */
	@Action(method=Method.GET)
	String like(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!antiXssOk(req)) {
			insertMessageAjaxFail(res, "Verifica token fallita");
			return null;
		}
    	AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(LOGGED_USER_REQ_ATTR);
    	if (loggedUser == null) {
    		insertMessageAjaxFail(res, "Non furmigare !");
    		return null;
    	}
		String id = req.getParameter("msgId");
		if (StringUtils.isEmpty(id)) {
			insertMessageAjaxFail(res, "Nessun messaggio selezionato");
			return null;
		}
		long msgId;
		try {
			msgId = Long.parseLong(id);
		} catch (NumberFormatException e) {
			insertMessageAjaxFail(res, "Messaggio " + id + " non conosciuto");
			return null;
		}
		String upvote = req.getParameter("like");
		if (StringUtils.isEmpty(upvote)) {
			insertMessageAjaxFail(res, "+1 o -1, deciditi cribbio !");
			return null;
		}
		int voteValue = getPersistence().like(msgId, loggedUser.getNick(), Boolean.parseBoolean(upvote));
		if (voteValue != 0) {
			JsonWriter writer = new JsonWriter(res.getWriter());
			writer.beginObject();
			writer.name("resultCode").value("OK");
			writer.name("content").value("Hai espresso il tuo inalienabile diritto di voto !");
			writer.name("voteValue").value(voteValue);
			writer.endObject();
			writer.flush();
			writer.close();
		} else {
			insertMessageAjaxFail(res, "Un troll, un voto !");
		}
		return null;
	}
	
	@Action(method=Method.POST)
	String saveTag(HttpServletRequest req, HttpServletResponse res) throws Exception {
		try {
			if (login(req) == null || !login(req).isValid()) return null;
			res.setContentType("application/json");
			String value = req.getParameter("value");
			long msgId = Long.parseLong(req.getParameter("msgId"));
			TagDTO tag = new TagDTO();
			tag.setM_id(msgId);
			tag.setAuthor(login(req).getNick());
			tag.setValue(value);
			tag = getPersistence().addTag(tag);
			JsonWriter writer = new JsonWriter(res.getWriter());
			writer.beginObject();
			writer.name("resultCode").value("OK");
			writer.name("content").value(tag.getT_id());
			writer.endObject();
			writer.flush();
			writer.close();
		} catch (Exception e) {
			Logger.getLogger(Messages.class).error(e);
			JsonWriter writer = new JsonWriter(res.getWriter());
			writer.beginObject();
			writer.name("resultCode").value("KO");
			writer.endObject();
			writer.flush();
			writer.close();
		}
		return null;
	}
	
	@Action(method=Method.POST)
	String deleTag(HttpServletRequest req, HttpServletResponse res) throws Exception {
		try {
			if (login(req) == null || !login(req).isValid()) return null;
			res.setContentType("application/json");
			TagDTO tag = new TagDTO();
			tag.setAuthor(login(req).getNick());
			tag.setT_id(Long.parseLong(req.getParameter("t_id")));
			tag.setM_id(Long.parseLong(req.getParameter("m_id")));
			getPersistence().deleTag(tag);
			JsonWriter writer = new JsonWriter(res.getWriter());
			writer.beginObject();
			writer.name("resultCode").value("OK");
			writer.endObject();
			writer.flush();
			writer.close();
		} catch (Exception e) {
			Logger.getLogger(Messages.class).error(e);
			JsonWriter writer = new JsonWriter(res.getWriter());
			writer.beginObject();
			writer.name("resultCode").value("KO");
			writer.endObject();
			writer.flush();
			writer.close();
		}
		return null;
	}
	
	@Action(method=Method.GET)
	String getMessagesByTag(HttpServletRequest req, HttpServletResponse res) throws Exception {
		long t_id = Long.parseLong(req.getParameter("t_id"));
		MessagesDTO messages = getPersistence().getMessagesByTag(PAGE_SIZE, getPageNr(req), t_id, hideProcCatania(req));
		req.setAttribute("messages", messages.getMessages());
		req.setAttribute("totalSize", messages.getMaxNrOfMessages());
		req.setAttribute("resultSize", messages.getMessages().size());
		setWebsiteTitle(req, "Ricerca per tag @ Forum dei troll");
		setAntiXssToken(req);
		return "messages.jsp";
	}

	private String getMessages(HttpServletRequest req, HttpServletResponse res, NavigationMessage message) throws Exception {
		String forum = req.getParameter("forum");
		MessagesDTO messages = getPersistence().getMessages(forum, null, PAGE_SIZE, getPageNr(req), hideProcCatania(req));
		getPersistence().getTags(messages);
		req.setAttribute("messages", messages.getMessages());
		req.setAttribute("totalSize", messages.getMaxNrOfMessages());
		req.setAttribute("resultSize", messages.getMessages().size());
		addSpecificParam(req, "forum", forum);
		if (forum == null) {
			setWebsiteTitle(req, "Forum dei troll");
		} else {
			setWebsiteTitle(req, forum.equals("") ? "Forum principale @ Forum dei troll" : (forum + " @ Forum dei troll"));
		}
		setNavigationMessage(req, message);
		setAntiXssToken(req);
		return "messages.jsp";
	}

	public static void banIP(final String ip) {
		synchronized(BANNED_IPs) {
			BANNED_IPs.add(ip);
		}
	}
}
