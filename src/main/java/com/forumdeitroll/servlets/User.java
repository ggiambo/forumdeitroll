package com.forumdeitroll.servlets;

import com.forumdeitroll.FdTConfig;
import com.forumdeitroll.PasswordUtils;
import com.forumdeitroll.RandomPool;
import com.forumdeitroll.ReCaptchaUtils;
import com.forumdeitroll.markup.InputSanitizer;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.PrivateMsgDTO;
import com.forumdeitroll.persistence.QuoteDTO;
import com.forumdeitroll.servlets.Action.Method;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URLEncoder;
import java.util.*;

public class User extends MainServlet {

	private static final long serialVersionUID = 1L;

	public static final int MAX_SIZE_AVATAR_BYTES = 512*1024;
	public static final long MAX_SIZE_AVATAR_WIDTH = 100;
	public static final long MAX_SIZE_AVATAR_HEIGHT = 100;

	public static final int MAX_SIZE_SIGNATURE_BYTES = 512*1024;
	public static final long MAX_SIZE_SIGNATURE_WIDTH = 530;
	public static final long MAX_SIZE_SIGNATURE_HEIGHT = 50;

	public static final String PREF_SHOWANONIMG = "showAnonImg";
	public static final String PREF_EMBEDDYT = "embeddYt";
	public static final String PREF_COLLAPSE_QUOTES = "collapseQuotes";
	public static final String PREF_HIDDEN_FORUMS = "hiddenForums";
	public static final String PREF_HIDE_BANNERONE = "hideBannerone";
	public static final String PREF_MSG_MAX_HEIGHT = "msgMaxHeight";
	public static final String PREF_AUTO_REFRESH = "autoRefresh";
	public static final String PREF_HIDE_SIGNATURE = "hideSignature";
	public static final String PREF_COMPACT_SIGNATURE = "compactSignature";
	public static final String PREF_BLOCK_HEADER = "blockHeader";
	public static final String PREF_LARGE_STYLE = "largeStyle";
	public static final String PREF_THEME = "theme";
	public static final String PREF_USER_TITLE = "userTitle";
	public static final String PREF_MESSAGE_FILTER = "messageFilter";

	public static final List<String> PREF_THEMES = Arrays.asList("Classico", "Scuro", "Flat");

	private static final String ALL_FORUMS = "allForums";

	private static final String ANTI_XSS_TOKEN = "anti-xss-token";

	@Override
	public void doBefore(HttpServletRequest req, HttpServletResponse res) {
		req.setAttribute(ALL_FORUMS, cachedForums.get());
	}

	@Action
	String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = login(req);
		setWebsiteTitlePrefix(req, "");
		if (loggedUser != null && loggedUser.isValid()) {
			req.setAttribute(PREF_HIDDEN_FORUMS, authorsDAO.getHiddenForums(loggedUser));
			if (isMobileView(req)) {
				res.setHeader("Location", "Messages?action=getMessages");
				res.sendError(HttpServletResponse.SC_TEMPORARY_REDIRECT);
				return null;
			}
			return "user.jsp";
		}
		setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
		return loginAction(req,  res);
	}

	/**
	 * Mostra la pagina di login
	 */
	@Action
	String loginAction(HttpServletRequest req, HttpServletResponse res) {
		setWebsiteTitlePrefix(req, "Login");
		return "login.jsp";
	}

	/**
	 * Update della password
	 */
	@Action
	String updatePass(HttpServletRequest req, HttpServletResponse res) {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}

		// user loggato, check pass
		String actualPass = req.getParameter("actualPass");
		if (StringUtils.isEmpty(actualPass)) {
			setNavigationMessage(req, NavigationMessage.warn("Inserisci la password attuale"));
			return "user.jsp";
		}

		if (!PasswordUtils.hasUserPassword(loggedUser, actualPass)) {
			setNavigationMessage(req, NavigationMessage.warn("Password attuale sbagliata, non fare il furmiga"));
			return "user.jsp";
		}

		String pass1 = req.getParameter("pass1");
		String pass2 = req.getParameter("pass2");

		if (StringUtils.isEmpty(pass1) || StringUtils.isEmpty(pass2)) {
			setNavigationMessage(req, NavigationMessage.warn("Inserisci una password"));
			return "user.jsp";
		}
		if (!pass1.equals(pass2)) {
			setNavigationMessage(req, NavigationMessage.warn("Le due password non sono uguali"));
			return "user.jsp";
		}

		if (!authorsDAO.updateAuthorPassword(loggedUser, pass1)) {
			setNavigationMessage(req, NavigationMessage.error("Errore in User.updatePass / updateAuthorPassword -- molto probabilmente e` colpa di sarrusofono, faglielo sapere -- sempre ammesso che tu riesca a postare sul forum a questo punto :("));
			return "user.jsp";
		}

		setNavigationMessage(req, NavigationMessage.info("Password modificata con successo !"));
		return "user.jsp";
	}

	/**
	 * Update avatar
	 */
	@Action
	String updateAvatar(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}

		if (!ServletFileUpload.isMultipartContent(req)) {
			setNavigationMessage(req, NavigationMessage.warn("Nessun avatar caricato"));
			return "user.jsp";
		}

		// piglia l'immagine dal multipart request
		DiskFileItemFactory  fileItemFactory = new DiskFileItemFactory ();
		fileItemFactory.setSizeThreshold(MAX_SIZE_AVATAR_BYTES); // grandezza massima 512Kbytes
		fileItemFactory.setRepository(new File(System.getProperty("java.io.tmpdir")));
		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		@SuppressWarnings("unchecked")
		Iterator<FileItem> it = uploadHandler.parseRequest(req).iterator();
		if (it.hasNext()) {
			FileItem avatar = it.next();
			if (avatar.getSize() > MAX_SIZE_AVATAR_BYTES) {
				setNavigationMessage(req, NavigationMessage.warn("Megalomane, avatar troppo grande, al massimo 512K !"));
				return "user.jsp";
			}
			// carica l'immagine
			BufferedImage image = ImageIO.read(avatar.getInputStream());
			if (image == null) {
				setNavigationMessage(req, NavigationMessage.warn("Formato imamgine sconosciuta"));
				return "user.jsp";
			}
			int w = image.getWidth();
			int h = image.getHeight();
			if (w > MAX_SIZE_AVATAR_WIDTH || h > MAX_SIZE_AVATAR_HEIGHT) {
				setNavigationMessage(req, NavigationMessage.warn("Dimensione massima consentita: 100x100px"));
				return "user.jsp";
			}
			// modifica loggedUser
			loggedUser.setAvatar(avatar.get());
			authorsDAO.updateAuthor(loggedUser);
		} else {
			setNavigationMessage(req, NavigationMessage.warn("Nessun Avatar ?"));
			return "user.jsp";
		}

		// fuck yeah 8) !
		setNavigationMessage(req, NavigationMessage.info("Avatar modificato con successo !"));
		return "user.jsp";
	}

	/**
	 * Pagina per registrare un nuovo user
	 */
	@Action
	String registerAction(HttpServletRequest req, HttpServletResponse res) {
		req.removeAttribute(LOGGED_USER_REQ_ATTR);
		setWebsiteTitlePrefix(req, "Registrazione");
		req.setAttribute("captchakey", FdTConfig.getProperty("recaptcha.key.client"));
		return "register.jsp";
	}

	/**
	 * Registra nuovo user
	 */
	@Action
	String registerNewUser(HttpServletRequest req, HttpServletResponse res) {
		req.setAttribute("captchakey", FdTConfig.getProperty("recaptcha.key.client"));

		String nick = req.getParameter("nick");
		req.setAttribute("nick", nick);
		// check del captcha
		if (!ReCaptchaUtils.verifyReCaptcha(req)) {
			setNavigationMessage(req, NavigationMessage.warn("Captcha non corretto"));
			return "register.jsp";
		}
		// registra il nick
		if (StringUtils.isEmpty(nick) || nick.length() > 40) {
			setNavigationMessage(req, NavigationMessage.warn("Impossibile registrare questo nick: Troppo lungo o troppo corto"));
			return "register.jsp";
		}
		String pass = req.getParameter("pass");
		if (StringUtils.isEmpty(pass) || pass.length() > 20) {
			setNavigationMessage(req, NavigationMessage.warn("Scegli una password migliore, giovane jedi ..."));
			return "register.jsp";
		}
		AuthorDTO loggedUser = authorsDAO.registerUser(nick, pass);
		if (!loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Impossibile registrare questo nick, probabilmente gia' esiste"));
			return "register.jsp";
		}
		notifyNewUser(loggedUser, req.getParameter("motivation"));
		// login
		login(req);
		req.setAttribute("loggedUser", loggedUser);
		return "user.jsp";
	}

	/**
	 * Carica le frasi celebri dal database
	 */
	@Action
	String getQuotes(HttpServletRequest req, HttpServletResponse res) {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}

		List<QuoteDTO> list = quotesDAO.getQuotes(loggedUser);
		int size = list.size();
		if (size < 5) {
			for (int i = 0; i < 5 - size; i++) {
				QuoteDTO dto = new QuoteDTO();
				dto.setId(-i);
				list.add(dto);
			}
		}

		req.setAttribute("quote", list);
		return "quote.jsp";
	}

	/**
	 * Update di una frase celebre
	 */
	@Action
	String updateQuote(HttpServletRequest req, HttpServletResponse res) {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}

		Long quoteId = Long.parseLong(req.getParameter("quoteId"));
		String content = req.getParameter("quote_" + quoteId);
		if (StringUtils.isEmpty(content) || content.length() < 3 || content.length() > 150) {
			setNavigationMessage(req, NavigationMessage.warn("Minimo 3 caratteri, massimo 150"));
			return getQuotes(req,  res);
		}

		QuoteDTO quote = new QuoteDTO();
		quote.setContent(content);
		quote.setId(quoteId);
		quote.setNick(loggedUser.getNick());

		quotesDAO.insertUpdateQuote(quote);
		return getQuotes(req,  res);
	}

	/**
	 * Cancella una quote
	 */
	@Action
	String removeQuote(HttpServletRequest req, HttpServletResponse res) {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}

		Long quoteId = Long.parseLong(req.getParameter("quoteId"));
		QuoteDTO quote = new QuoteDTO();
		quote.setNick(loggedUser.getNick());
		quote.setId(quoteId);

		quotesDAO.removeQuote(quote);
		return getQuotes(req,  res);
	}

	/**
	 * Tutte le informazioni dell'utente
	 */
	@Action
	String getUserInfo(HttpServletRequest req, HttpServletResponse res) {
		String nick = req.getParameter("nick");
		AuthorDTO author = authorsDAO.getAuthor(nick);
		req.setAttribute("author", author);
		req.setAttribute("quotes", quotesDAO.getQuotes(author));

		final AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);

		if ((loggedUser != null) && "yes".equals(loggedUser.getPreferences().get("super"))) {
			final String token = RandomPool.getString(3);
			req.getSession().setAttribute(ANTI_XSS_TOKEN, token);
			req.setAttribute("token", token);
		}

		return "userInfo.jsp";
	}

	@Action(method=Method.POST)
	String edit(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Non sei loggato !"));
			return loginAction(req,  res);
		}

		if (!("yes".equals(loggedUser.getPreferences().get("super")))) {
			setNavigationMessage(req, NavigationMessage.warn("Non sei superutente, non puoi fare questa cosa!"));
			return loginAction(req,  res);
		}

		final String token = (String)req.getSession().getAttribute(ANTI_XSS_TOKEN);
		final String inToken = req.getParameter("token");

		if ((token == null) || !token.equals(inToken)) {
			setNavigationMessage(req, NavigationMessage.warn("Verifica token fallita"));
			return getUserInfo(req,  res);
		}

		final String nick = req.getParameter("nick");

		if (nick == null) {
			setNavigationMessage(req, NavigationMessage.warn("Nessun nickname specificato"));
			return init(req,  res);
		}

		final String pass = req.getParameter("pass");
		final String pass2 = req.getParameter("pass2");

		final AuthorDTO author = authorsDAO.getAuthor(nick);

		if ((author == null) || !author.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Il nickname è sparito!?"));
			return getUserInfo(req,  res);
		}

		if (!StringUtils.isEmpty(pass)) {
			if (StringUtils.isEmpty(pass2) || !pass2.equals(pass)) {
				setNavigationMessage(req, NavigationMessage.warn("Password sbagliata"));
				return getUserInfo(req,  res);
			}

			if (!authorsDAO.updateAuthorPassword(author, pass)) {
				setNavigationMessage(req, NavigationMessage.error("Errore in User.editUser / updateAuthorPassword -- molto probabilmente e` colpa di sarrusofono, faglielo sapere -- sempre ammesso che tu riesca a postare sul forum a questo punto :("));
				return getUserInfo(req,  res);
			}
		}

		final String pedonizeThread = req.getParameter("pedonizeThread");
		if (!StringUtils.isEmpty(pedonizeThread)) {
			author.setPreferences(authorsDAO.setPreference(author, "pedonizeThread", pedonizeThread));
		} else {
			author.setPreferences(authorsDAO.setPreference(author, "pedonizeThread", ""));
		}

		final boolean isEnabled = "yes".equals(req.getParameter("isEnabled"));
		authorsDAO.enableUser(author, isEnabled);

		return getUserInfo(req,  res);
	}

	/**
	 * Cambia le preferences
	 */
	@Action
	String updatePreferences(HttpServletRequest req, HttpServletResponse res) {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}

		// setta le preferences
		for (String key : new String[] {PREF_SHOWANONIMG, PREF_EMBEDDYT, PREF_COLLAPSE_QUOTES, PREF_HIDE_BANNERONE,
				PREF_MSG_MAX_HEIGHT, PREF_AUTO_REFRESH, PREF_HIDE_SIGNATURE, PREF_COMPACT_SIGNATURE, PREF_BLOCK_HEADER,
				PREF_LARGE_STYLE}) {
			String value = req.getParameter(key);
			if (StringUtils.isNotEmpty(value)) {
				loggedUser.setPreferences(authorsDAO.setPreference(loggedUser, key, "checked"));
				if (key.equals(PREF_LARGE_STYLE)) {
					loggedUser.setPreferences(authorsDAO.setPreference(loggedUser, "sidebarStatus", "hide"));
				}
			} else {
				loggedUser.setPreferences(authorsDAO.setPreference(loggedUser, key, ""));
				if (key.equals(PREF_LARGE_STYLE)) {
					loggedUser.setPreferences(authorsDAO.setPreference(loggedUser, "sidebarStatus", "show"));
				}
			}
		}

		String[] hiddenForums = req.getParameterValues(PREF_HIDDEN_FORUMS);
		List<String> forumsToHide = new ArrayList<>();
		if (hiddenForums != null) {
			forumsToHide.addAll(Arrays.asList(hiddenForums));
		}
		authorsDAO.setHiddenForums(loggedUser, forumsToHide);
		req.setAttribute(PREF_HIDDEN_FORUMS, authorsDAO.getHiddenForums(loggedUser));

		String theme = req.getParameter(PREF_THEME);
		if (StringUtils.isNotEmpty(theme)) {
			loggedUser.setPreferences(authorsDAO.setPreference(loggedUser, PREF_THEME, theme));
		}

		String userTitle = req.getParameter(PREF_USER_TITLE);
		if (userTitle!=null) {
			loggedUser.setPreferences(authorsDAO.setPreference(loggedUser, PREF_USER_TITLE, userTitle));
		}

		String messageFilter = req.getParameter(PREF_MESSAGE_FILTER);
		if (messageFilter!=null) {
			messageFilter = StringUtils.join(messageFilter.split("[\\r\\n]+"), "\n");
			loggedUser.setPreferences(authorsDAO.setPreference(loggedUser, PREF_MESSAGE_FILTER, messageFilter));
		}

		return "user.jsp";
	}

	/**
	 * Mostra le notifiche
	 */
	@Action
	String getNotifications(HttpServletRequest req, HttpServletResponse res) {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}

		setWebsiteTitlePrefix(req, "Notifiche");
		req.setAttribute("notificationsFrom", miscDAO.getNotifications(loggedUser.getNick(), null));
		req.setAttribute("notificationsTo", miscDAO.getNotifications(null, loggedUser.getNick()));

		return "notifications.jsp";
	}

	/**
	 * Mostra le notifiche
	 */
	@Action(method=Method.POST)
	String removeNotification(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}

		setWebsiteTitlePrefix(req, "Notifiche");

		long notificationId = Long.parseLong(req.getParameter("notificationId"));
		miscDAO.removeNotification(loggedUser.getNick(), null, notificationId);

		return getNotifications(req, res);
	}

	/**
	 * Notifica un utente
	 */
	@Action(method=Method.POST)
	String notifyUser(HttpServletRequest req, HttpServletResponse res) throws Exception {
		JsonWriter writer = new JsonWriter(res.getWriter());
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			writer.beginObject();
			writer.name("resultCode").value("ERROR");
			writer.name("content").value("Hue', guaglio', che stai affa' ?!?");
			writer.endObject();
			writer.flush();
			writer.close();
			return null;
		}

		String toNick = req.getParameter("toNick");
		if (StringUtils.isEmpty(toNick)) {
			writer.beginObject();
			writer.name("resultCode").value("ERROR");
			writer.name("content").value("Il destinatario della notifica e' vuoto");
			writer.endObject();
			writer.flush();
			writer.close();
			return null;
		}

		long msgId;
		try {
			msgId = Long.parseLong(req.getParameter("msgId"));
		} catch (NumberFormatException e) {
			writer.beginObject();
			writer.name("resultCode").value("ERROR");
			writer.name("content").value("L'id del messaggio " + req.getParameter("msgId") + " mi e' incomprensibile :S ...");
			writer.endObject();
			writer.flush();
			writer.close();
			return null;
		}

		if (miscDAO.getNotifications(loggedUser.getNick(), null).size() > 9) {
			writer.beginObject();
			writer.name("resultCode").value("ERROR");
			writer.name("content").value("Hai gia' 10 notifiche: Vergognati spammone !!");
			writer.endObject();
			writer.flush();
			writer.close();
			return null;
		}

		miscDAO.createNotification(loggedUser.getNick(), toNick, msgId);
		writer.beginObject();
		writer.name("resultCode").value("OK");
		writer.endObject();
		writer.flush();
		writer.close();

		return null;
	}

	/**
	 * Firma
	 */
	@Action(method=Method.POST)
	String updateSignature(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}
		String submitBtn = null;
		String signature = null;
		byte[] signature_image = null;
		DiskFileItemFactory  fileItemFactory = new DiskFileItemFactory ();
		fileItemFactory.setSizeThreshold(MAX_SIZE_SIGNATURE_BYTES); // grandezza massima 512Kbytes
		fileItemFactory.setRepository(new File(System.getProperty("java.io.tmpdir")));
		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		for (FileItem item : uploadHandler.parseRequest(req)) {
			if (item.isFormField()) {
				if (item.getFieldName().equals("submitBtn")) {
					submitBtn = item.getString();
				} else if (item.getFieldName().equals("signature")) {
					signature = InputSanitizer.sanitizeText(item.getString("UTF-8"));
				}
			} else {
				if (item.getSize() == 0) {
					continue;
				}
				if (item.getSize() > MAX_SIZE_SIGNATURE_BYTES) {
					setNavigationMessage(req, NavigationMessage.warn("Megalomane, firma troppo grande, al massimo 512K !"));
					return "user.jsp";
				}
				BufferedImage image = ImageIO.read(item.getInputStream());
				if (image == null) {
					setNavigationMessage(req, NavigationMessage.warn("Formato imamgine sconosciuta"));
					return "user.jsp";
				}
				int w = image.getWidth();
				int h = image.getHeight();
				if (w > MAX_SIZE_SIGNATURE_WIDTH || h > MAX_SIZE_SIGNATURE_HEIGHT) {
					setNavigationMessage(req, NavigationMessage.warn("Dimensione massima consentita: 530x50px"));
					return "user.jsp";
				}
				signature_image = item.get();
			}
		}
		if ("Modifica".equals(submitBtn)) {
			if (signature.length() > 200) {
				setNavigationMessage(req, NavigationMessage.error("Yawn, resta sotto i 200 caratteri !"));
				return "user.jsp";
			}
			if (signature_image != null) {
				loggedUser.setSignatureImage(signature_image);
				authorsDAO.updateAuthor(loggedUser);
				loggedUser.setPreferences(authorsDAO.setPreference(loggedUser, "signature", signature));
			} else {
				loggedUser.setPreferences(authorsDAO.setPreference(loggedUser, "signature", signature));
			}
		} else if ("Elimina".equals(submitBtn)) {
			loggedUser.setSignatureImage(null);
			authorsDAO.updateAuthor(loggedUser);
			loggedUser.setPreferences(authorsDAO.setPreference(loggedUser, "signature", ""));
		} else {
			setNavigationMessage(req, NavigationMessage.error("Nessuna operazione eseguita !"));
			return "user.jsp";
		}
		setNavigationMessage(req, NavigationMessage.info("Firma modificato con successo !"));
		return "user.jsp";

	}

	@Action(method=Method.POST)
	String extLogin(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String callback = req.getParameter("callback");
		if (StringUtils.isEmpty(callback)) {
			setNavigationMessage(req, NavigationMessage.warn("callback non impostato."));
			return "user.jsp";
		}
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Devi essere loggato per accedere alla funzione."));
			return loginAction(req, res);
		}
		String key = loginsDAO.createLogin(loggedUser.getNick());
		String redirectUrl = callback + URLEncoder.encode(key, "UTF-8");
		res.setHeader("Location", redirectUrl);
		res.sendError(HttpServletResponse.SC_MOVED_TEMPORARILY);
		return null;
	}

	private void notifyNewUser(AuthorDTO newUser, String motivation) {
		PrivateMsgDTO msg = new PrivateMsgDTO();
		msg.setSubject("Nuova supplica di " + newUser.getNick());
		msg.setText("La sua motivazione:</br>" +
				"<i>"  + motivation + "</i></br></br>" +
				" Clicka [url=User?action=getUserInfo&nick=" + newUser.getNick() + "]qui[/url] per decidere se ammetterlo nella Grande Famiglia o fanculizzarlo.");

		AuthorDTO suora = adminDAO.getAuthor("::1");
		Collection<String> admins = adminDAO.getAdmins();
		privateMsgDAO.sendAPvtForGreatGoods(suora, msg, admins.toArray(new String[0]));
	}

}
