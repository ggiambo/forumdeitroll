package com.forumdeitroll.servlets;

import com.forumdeitroll.FdTConfig;
import com.forumdeitroll.MessagePenetrator;
import com.forumdeitroll.markup.InputSanitizer;
import com.forumdeitroll.markup.RenderOptions;
import com.forumdeitroll.markup.Renderer;
import com.forumdeitroll.persistence.*;
import com.forumdeitroll.servlets.Action.Method;
import com.forumdeitroll.taglibs.RenderTag;
import com.forumdeitroll.util.CacheTorExitNodes;
import com.forumdeitroll.util.IPMemStorage;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Messages extends MainServlet {
	private static final long serialVersionUID = 1L;

	private static final List<String> REFRESHABLE_ACTIONS = Collections.singletonList("getMessages");

	private static final Pattern PATTERN_QUOTE = Pattern.compile("<BR> *(&gt; ?)*");

	public static final Set<String> BANNED_IPs = new HashSet<>();

	public static final int MAX_MESSAGE_LENGTH = 40000;
	public static final int MAX_SUBJECT_LENGTH = 80;

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
		String action = (String)req.getAttribute("action");
		if (REFRESHABLE_ACTIONS.contains(action) && StringUtils.isEmpty(req.getParameter("page"))) {
			req.setAttribute("refreshable", "1");
		}
	}

	@Override
	public void doAfter(HttpServletRequest req, HttpServletResponse res) {
		AuthorDTO author = (AuthorDTO) req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (author != null) {
			req.setAttribute("notifications", miscDAO.getNotifications(null, author.getNick()));
		}
		req.setAttribute("captchakey", FdTConfig.getProperty("recaptcha.key.client"));
	}

	/**
	 * I messaggi di questa pagina (Dimensione PAGE_SIZE) in ordine di data
	 */
	@Action
	String getMessages(HttpServletRequest req, HttpServletResponse res) {
		return getMessages(req, res, NavigationMessage.info("Cronologia messaggi"));
	}

	/**
	 * I messaggi di questo autore in ordine di data
	 */
	@Action
	String getByAuthor(HttpServletRequest req, HttpServletResponse res) {
		String author = req.getParameter("author");
		addSpecificParam(req, "author", author);
		String forum = req.getParameter("forum");
		addSpecificParam(req, "forum", forum);
		setWebsiteTitlePrefix(req, "Messaggi di " + author);
		setNavigationMessage(req, NavigationMessage.info("Messaggi scritti da <i>" + StringEscapeUtils.escapeHtml4(author) + "</i>"));
		MessagesDTO messages = messagesDAO.getMessages(forum, author, PAGE_SIZE, getPageNr(req), hiddenForums(req));
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
	String getByForum(HttpServletRequest req, HttpServletResponse res) {
		String forum = req.getParameter("forum");
		if (StringUtils.isEmpty(forum)) {
			setWebsiteTitlePrefix(req, "Forum Principale");
		} else {
			setWebsiteTitlePrefix(req, forum);
		}

		addSpecificParam(req, "forum", forum);
		setNavigationMessage(req, NavigationMessage.info("Cronologia messaggi"));
		MessagesDTO messages = messagesDAO.getMessages(forum, null, PAGE_SIZE, getPageNr(req), null);
		req.setAttribute("messages", messages.getMessages());
		req.setAttribute("resultSize", messages.getMessages().size());
		req.setAttribute("totalSize", messages.getMaxNrOfMessages());
		setAntiXssToken(req);
		return "messages.jsp";
	}

	/**
	 * Questo singolo messaggio
	 */
	@Action
	String getById(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Long msgId = Long.parseLong(req.getParameter("msgId"));
		setWebsiteTitlePrefix(req, "Singolo messaggio");
		List<MessageDTO> messages = new ArrayList<>();
		messages.add(messagesDAO.getMessage(msgId));
		req.setAttribute("messages", messages);
		req.setAttribute("resultSize", messages.size());

		// request from a notification ?
		String notificationId = req.getParameter("notificationId");
		String fromNick = req.getParameter("notificationFromNick");
		final AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(LOGGED_USER_REQ_ATTR);
		if (StringUtils.isNotEmpty(notificationId) && StringUtils.isNotEmpty(fromNick) && loggedUser.isValid()) {
			// remove this notification once clicked
			try {
				long id = Long.parseLong(notificationId);
				miscDAO.removeNotification(fromNick, loggedUser.getNick(), id);
			} catch (NumberFormatException e) {
				// Ma che c'� frega ma che ce 'mporta ...
			}
		}

		return "messages.jsp";
	}

	/**
	 * Ricerca in tutti i messaggi
	 */
	@Action
	String search(HttpServletRequest req, HttpServletResponse res) {
		final String search = req.getParameter("search");
		final String sort = req.getParameter("sort");

		addSpecificParam(req, "search", search);
		addSpecificParam(req, "sort", sort);

		setWebsiteTitlePrefix(req, "Ricerca di " + search);

//		List<MessageDTO> messages = getPersistence().searchMessages(search, SearchMessagesSort.parse(sort), PAGE_SIZE, getPageNr(req));
//		req.setAttribute("messages", messages);
//		req.setAttribute("resultSize", messages.size());
		setAntiXssToken(req);
		return "messages.jsp";
	}

	/**
	 * Popola il div per la risposta/quota messaggio
	 */
	@Action
	String newMessage(HttpServletRequest req, HttpServletResponse res) {
		MessageDTO msg = new MessageDTO();
		msg.setForum(req.getParameter("forum"));
		msg.setSubject(req.getParameter("subject"));
		msg.setText(req.getParameter("text"));
		req.setAttribute("message", msg);
		setWebsiteTitlePrefix(req, "Nuovo messaggi");
		// faccine - ordinate per key
		final String ip = IPMemStorage.requestToIP(req);
		if (adminDAO.blockTorExitNodes()) {
			if (CacheTorExitNodes.check(ip)) {
				req.setAttribute("warnTorUser", "true");
			}
		}

		req.setAttribute(Admin.ADMIN_NON_ANON_POST, miscDAO.getSysinfoValue(Admin.ADMIN_NON_ANON_POST));

		return "newMessage.jsp";
	}

	private static String getReplySubject(String parentSubject) {
		if (!parentSubject.startsWith("Re:")) {
			parentSubject = "Re: " + parentSubject;
		}
		return parentSubject.substring(0, Math.min(MAX_SUBJECT_LENGTH, parentSubject.length()));
	}

	private static final List<String> quoteTypes = Arrays.asList("quote", "quote1", "quote4");

	private static String getReplyText(String parentText, String type, String author) {
		if (!quoteTypes.contains(type)) {
			return "";
		}
		String text = parentText.trim();
		// quote
		Matcher m = PATTERN_QUOTE.matcher(text);
		while (m.find()) {
			String group = m.group(0);
			int nrQuotes = group.replace(" ", "").replace("<BR>", "").length() / 4; // 4 = "&gt;".length();
			nrQuotes++;
			StringBuilder newBegin = new StringBuilder("\r\n");
			for (int j = 0; j < nrQuotes; j++) {
				newBegin.append("&gt; ");
			}
			text = m.replaceFirst(newBegin.toString());
			m = PATTERN_QUOTE.matcher(text);
		}

		text = "\r\nScritto da: " + (author != null ? author.trim() : "") + "\r\n&gt; " + text + "\r\n";
		if ("quote1".equals(type)) {
			StringBuilder out = new StringBuilder();
			for (String line : text.split("\r\n")) {
				if (line.startsWith("Scritto da")
						|| line.startsWith("&gt; ")
						&& !line.startsWith("&gt; &gt;")
						&& !line.startsWith("&gt; Scritto da")
						) {
					out.append(line).append("\r\n");
				}
			}
			text = out.toString();
		} else if ("quote4".equals(type)) {
			StringBuilder out = new StringBuilder();
			for (String line : text.split("\r\n")) {
				if (!line.startsWith("&gt; &gt; &gt; &gt; &gt;")
					|| line.startsWith("&gt; Scritto da")
					|| line.startsWith("&gt; &gt; Scritto da")
					|| line.startsWith("&gt; &gt; &gt; Scritto da")
					|| line.startsWith("&gt; &gt; &gt; &gt; Scritto da")) {
					out.append(line).append("\r\n");
				}
			}
			text = out.toString();
		}
		return text;
	}

	/**
	 * Popola il div per la risposta/quota messaggio
	 */
	@Action
	String showReplyDiv(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String type = req.getParameter("type");
		long parentId = Long.parseLong(req.getParameter("parentId"));
		req.setAttribute("parentId", parentId);
		MessageDTO newMsg = new MessageDTO();
		MessageDTO msgDTO = messagesDAO.getMessage(parentId);
		newMsg.setText(getReplyText(msgDTO.getText(), type, msgDTO.getAuthor().getNick()));
		newMsg.setForum(msgDTO.getForum());
		// setta il subject: aggiungi "Re: " e se necessario tronca a 40 caratteri
		newMsg.setSubject(getReplySubject(msgDTO.getSubject()));
		newMsg.setParentId(parentId);
		req.setAttribute("message", newMsg);

		//jstl non accede ai campi stitici
		req.setAttribute("MAX_MESSAGE_LENGTH", MAX_MESSAGE_LENGTH);
		req.setAttribute(Admin.ADMIN_NON_ANON_POST, miscDAO.getSysinfoValue(Admin.ADMIN_NON_ANON_POST));

		req.setAttribute("captchakey", FdTConfig.getProperty("recaptcha.key.client"));

		getServletContext().getRequestDispatcher("/pages/messages/incReplyMessage.jsp").forward(req, res);
		return null;
	}
	/**
	 * Modifica un messaggio esistente
	 */
	@Action
	String editMessage(HttpServletRequest req, HttpServletResponse res) {
		// check se l'uente loggato corrisponde a chi ha scritto il messaggio
		AuthorDTO user = login(req);
		String msgId = req.getParameter("msgId");
		MessageDTO msg = messagesDAO.getMessage(Long.parseLong(msgId));
		if (!user.isValid() || !user.getNick().equals(msg.getAuthor().getNick())) {
			return getMessages(req, res, NavigationMessage.error("Non puoi editare un messaggio non tuo !"));
		}

		// cleanup
		msg.setText(msg.getText().replaceAll("<BR>", "\r\n"));
		req.setAttribute("message", msg);

		req.setAttribute("isEdit", true);

		req.setAttribute(Admin.ADMIN_NON_ANON_POST, miscDAO.getSysinfoValue(Admin.ADMIN_NON_ANON_POST));

		return "newMessage.jsp";
	}

	/**
	 * Crea la preview del messaggio
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


		writer.name("content").value(RenderTag.getMessagePreview(text, author, author));
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
		MessageDTO msg = messagesDAO.getMessage(Long.parseLong(msgId));
		// crea la preview del messaggio
		JsonWriter writer = new JsonWriter(res.getWriter());
		writer.beginObject();
		writer.name("resultCode").value("OK");

		RenderOptions opts = new RenderOptions();
		opts.authorIsAnonymous = msg.getAuthor() == null || !msg.getAuthor().isValid();
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

	private void insertMessageAjaxBan(final HttpServletResponse res) throws IOException {
		JsonWriter writer = new JsonWriter(res.getWriter());
		writer.beginObject();
		writer.name("resultCode").value("BAN");
		writer.name("content").value("Sei stato bannato");
		writer.endObject();
		writer.flush();
		writer.close();
	}

	private void insertMessageAjaxFail(final HttpServletResponse res, final String error) throws IOException {
		JsonWriter writer = new JsonWriter(res.getWriter());
		writer.beginObject();
		writer.name("resultCode").value("MSG");
		writer.name("content").value(error);
		writer.endObject();
		writer.flush();
		writer.close();
	}


	/**
	 * Inserisce un messaggio nuovo o editato
	 */
	private String insertMessageAjax(HttpServletRequest req, HttpServletResponse res) throws Exception {
		final MessagePenetrator mp = new MessagePenetrator();

		mp
			.setForum(req.getParameter("forum"))
			.setParentId(req.getParameter("parentId"))
			.setSubject(req.getParameter("subject"))
			.setText(req.getParameter("text"))
			.setCredentials(
				(AuthorDTO)req.getAttribute(LOGGED_USER_REQ_ATTR),
				req.getParameter("nick"), req.getParameter("pass"),
				req.getParameter("g-recaptcha-response"))
			.deduceType(req.getParameter("id"));

		if (mp.isAuthorDisabled()) {
			insertMessageAjaxFail(res, "Questo utente non è abilitato");
			return null;
		}

		if (mp.isBanned(req.getSession().getAttribute(SESSION_IS_BANNED), IPMemStorage.requestToIP(req), req)) {
			insertMessageAjaxBan(res);
			return null;
		}

		final MessageDTO msg = mp.penetrate();
		if (msg == null) {
			insertMessageAjaxFail(res, mp.Error());
			return null;
		}

		// redirect
		res.setContentType("text/plain");
		JsonWriter writer = new JsonWriter(res.getWriter());
		writer.beginObject();
		writer.name("resultCode").value("OK");
		StringBuilder content = new StringBuilder();


		//Costruiamo l'url di destinazione a partire dall'url da cui e` stato effettuato il submit -- sarrusofono

		final String submitLocation = req.getParameter("submitLocation");
		//System.out.println("submitLocation [" + submitLocation + "]");
		URI submitURI = null;
		try {
			submitURI = new URI(StringUtils.defaultString(submitLocation));
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
					content.append("/Messages?action=getByForum&").append(navForum);
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

	private void forShame(final AuthorDTO author, final String shameTitle, final String shameMessage) {
		final MessageDTO msg = new MessageDTO();
		msg.setAuthor(author);
		msg.setParentId(-1);
		msg.setDate(new Date());
		msg.setText(shameMessage);
		msg.setSubject(shameTitle);
		msg.setForum("FreeBan");
		msg.setThreadId(-1);
		messagesDAO.insertMessage(msg);
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
		boolean isAdmin = "yes".equals(messagesDAO.getPreferences(loggedUser).get("pedonizeThread"));
		if (! isAdmin) {
			return getMessages(req, res, NavigationMessage.error("Non furmigare "+loggedUser.getNick()+" !!!"));
		}

		if (!antiXssOk(req)) {
			return getMessages(req, res, NavigationMessage.error("Verifica token fallita"));
		}

		final long rootMessageId = Long.parseLong(req.getParameter("rootMessageId"));
		MessageDTO rootMessage = messagesDAO.getMessage(rootMessageId);
		adminDAO.moveThreadTree(rootMessage, DAOFactory.FORUM_PROC);

		{ // moderator shaming block
			final MessageDTO movedMessage = messagesDAO.getMessage(rootMessageId);
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

	private String restoreOrHideMessage(HttpServletRequest req, HttpServletResponse res, long msgId, int visible) {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(LOGGED_USER_REQ_ATTR);
		if (loggedUser == null) {
			return getMessages(req, res, NavigationMessage.error("Non furmigare !"));
		}
		boolean isAdmin = "yes".equals(miscDAO.getPreferences(loggedUser).get("hideMessages"));
		if (! isAdmin) {
			return getMessages(req, res, NavigationMessage.error("Non furmigare "+loggedUser.getNick()+" !!!"));
		}

		if (!antiXssOk(req)) {
			return getMessages(req, res, NavigationMessage.error("Verifica token fallita"));
		}

		adminDAO.restoreOrHideMessage(msgId, visible);

		return getMessages(req, res, NavigationMessage.info("Messaggio infernale nascosto agli occhi dei giovini troll."));
	}

	/**
	 * Ritorna una frase celebre a caso
	 */
	@Action(method=Method.GET)
	String getRandomQuote(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// già escapato da getRandomQuote di MainServlet
		res.setContentType("text/plain");
		QuoteDTO quote = getRandomQuoteDTO();
		res.getWriter().write(quote.getContent()+'\n'+quote.getNick());
		res.flushBuffer();
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
		if (!loggedUser.isEnabled()) {
			insertMessageAjaxFail(res, "Utente non abilitato !");
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
		int voteValue = miscDAO.like(msgId, loggedUser.getNick(), Boolean.parseBoolean(upvote));
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

	private String getMessages(HttpServletRequest req, HttpServletResponse res, NavigationMessage message) {
		String forum = req.getParameter("forum");
		MessagesDTO messages = messagesDAO.getMessages(forum, null, PAGE_SIZE, getPageNr(req), hiddenForums(req));
		req.setAttribute("messages", messages.getMessages());
		req.setAttribute("totalSize", messages.getMaxNrOfMessages());
		req.setAttribute("resultSize", messages.getMessages().size());
		addSpecificParam(req, "forum", forum);
		if (forum == null) {
			setWebsiteTitlePrefix(req, "");
		} else {
			setWebsiteTitlePrefix(req, forum.equals("") ? "Forum principale" : forum);
		}
		setNavigationMessage(req, message);
		setAntiXssToken(req);
		return "messages.jsp";
	}

	static void banIP(final String ip) {
		synchronized(BANNED_IPs) {
			BANNED_IPs.add(ip);
		}
	}

	@Action
	String mobileComposer(HttpServletRequest req, HttpServletResponse res) {
		setWebsiteTitlePrefix(req, "Nuovo messaggio");
		final String ip = IPMemStorage.requestToIP(req);
		if (adminDAO.blockTorExitNodes()) {
			if (CacheTorExitNodes.check(ip)) {
				req.setAttribute("warnTorUser", "true");
			}
		}

		String sMessageId = req.getParameter("messageId");
		String sReplyToId = req.getParameter("replyToId");
		if (sMessageId != null) { // true per edit messaggio proprio
			long messageId = Long.parseLong(sMessageId);
			setWebsiteTitlePrefix(req, "Modifica messaggio");
			MessageDTO msg = messagesDAO.getMessage(messageId);
			if (msg.getAuthor().getNick().equals(login(req).getNick()) && !login(req).isBanned()) {
				req.setAttribute("messageId", sMessageId);
				req.setAttribute("replyToId", sReplyToId);
				req.setAttribute("subject", msg.getSubject());
				req.setAttribute("text", msg.getText().replaceAll("<BR>", "\r\n"));
				req.setAttribute("forum", msg.getForum());
			} else {
				return null;
			}
		} else if (sReplyToId != null) { // risposta a un messaggio
			long replyToId = Long.parseLong(sReplyToId);
			MessageDTO parent = messagesDAO.getMessage(replyToId);
			setWebsiteTitlePrefix(req, "Rispondi a " + parent.getAuthor().getNick());
			if (StringUtils.isEmpty(parent.getAuthor().getNick())) {
				setWebsiteTitlePrefix(req, "Rispondi ad un anonimo");
			}
			req.setAttribute("messageId", "-1");
			req.setAttribute("replyToId", sReplyToId);
			req.setAttribute("subject", getReplySubject(parent.getSubject()));
			req.setAttribute("text", getReplyText(parent.getText(), req.getParameter("type"), parent.getAuthor().getNick()));
			req.setAttribute("forum", parent.getForum());
		} else {
			req.setAttribute("messageId", "-1");
			req.setAttribute("replyToId", "-1");
			req.setAttribute("forums", miscDAO.getForums());
		}
		req.setAttribute("username", login(req).getNick());
		return "composer.jsp";
	}

}
