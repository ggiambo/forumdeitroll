package com.forumdeitroll.servlets;

import static java.lang.Character.UnicodeBlock.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;

import com.forumdeitroll.PasswordUtils;
import com.forumdeitroll.RandomPool;
import com.forumdeitroll.markup.InputSanitizer;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.QuoteDTO;
import com.forumdeitroll.servlets.Action.Method;
import com.forumdeitroll.util.CacheTorExitNodes;
import com.forumdeitroll.util.IPMemStorage;
import com.forumdeitroll.util.Ratelimiter;
import com.google.gson.stream.JsonWriter;

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
	public static final String PREF_HIDE_FAKE_ADS = "hideFakeAds";
	public static final List<String> PREF_THEMES = Arrays.asList("Classico", "Scuro", "Flat");

	public static final String ALL_FORUMS = "allForums";

	public static final String ANTI_XSS_TOKEN = "anti-xss-token";

	public static final int LOGIN_TIME_LIMIT = 3 * 60 * 1000;
	public static final int LOGIN_NUMBER_LIMIT = 5;

	protected final Ratelimiter<String> loginRatelimiter = new Ratelimiter<String>(LOGIN_TIME_LIMIT, LOGIN_NUMBER_LIMIT);

	private static final List<UnicodeBlock> ALLOWED_UNICODE_BLOCKS = Arrays.asList(BASIC_LATIN, LATIN_1_SUPPLEMENT);

	@Override
	public void doBefore(HttpServletRequest req, HttpServletResponse res) {
		req.setAttribute(ALL_FORUMS, getPersistence().getForums());
	}

	@Action
	String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (loginRatelimiter.limited(IPMemStorage.requestToIP(req))) {
			setNavigationMessage(req, NavigationMessage.warn("Hai rotto il cazzo"));
			return loginAction(req, res);
		}

		AuthorDTO loggedUser = login(req);
		setWebsiteTitlePrefix(req, "");
		if (loggedUser != null && loggedUser.isValid()) {
			req.setAttribute(PREF_HIDDEN_FORUMS, getPersistence().getHiddenForums(loggedUser));
			return "user.jsp";
		}
		loginRatelimiter.increment(IPMemStorage.requestToIP(req));
		setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
		return loginAction(req,  res);
	}

	/**
	 * Mostra la pagina di login
	 * @param req
	 * @param res
	 * @return
	 */
	@Action
	String loginAction(HttpServletRequest req, HttpServletResponse res) throws Exception {
		setWebsiteTitlePrefix(req, "Login");
		return "login.jsp";
	}

	/**
	 * Update della password
	 * @param req
	 * @param res
	 * @return
	 */
	@Action
	String updatePass(HttpServletRequest req, HttpServletResponse res) throws Exception {
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

		if (!getPersistence().updateAuthorPassword(loggedUser, pass1)) {
			setNavigationMessage(req, NavigationMessage.error("Errore in User.updatePass / updateAuthorPassword -- molto probabilmente e` colpa di sarrusofono, faglielo sapere -- sempre ammesso che tu riesca a postare sul forum a questo punto :("));
			return "user.jsp";
		}

		setNavigationMessage(req, NavigationMessage.info("Password modificata con successo !"));
		return "user.jsp";
	}

	/**
	 * Update avatar
	 * @param req
	 * @param res
	 * @return
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
			getPersistence().updateAuthor(loggedUser);
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
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action
	String registerAction(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.removeAttribute(LOGGED_USER_REQ_ATTR);
		setWebsiteTitlePrefix(req, "Registrazione");
		return "register.jsp";
	}

	/**
	 * Registra nuovo user
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action
	String registerNewUser(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (getPersistence().blockTorExitNodes()) {
			if (CacheTorExitNodes.check(IPMemStorage.requestToIP(req))) {
				if (!availableTorRegistrations.available()) {
					setNavigationMessage(req, NavigationMessage.warn("Iscrizioni tramite TOR sopra il limite orario"));
					return "register.jsp";
				}
			}
		}

		String nick = req.getParameter("nick");
		req.setAttribute("nick", nick);
		// check del captcha
		String captcha = req.getParameter("captcha");
		String correctAnswer = (String)req.getSession().getAttribute("captcha");
		if ((correctAnswer == null) || !correctAnswer.equals(captcha)) {
			setNavigationMessage(req, NavigationMessage.warn("Captcha non corretto"));
			return "register.jsp";
		}
		// registra il nick
		if (StringUtils.isEmpty(nick) || nick.length() > 40) {
			setNavigationMessage(req, NavigationMessage.warn("Impossibile registrare questo nick: Troppo lungo o troppo corto"));
			return "register.jsp";
		}
		for (char c : nick.toCharArray()) {
			if (!ALLOWED_UNICODE_BLOCKS.contains(Character.UnicodeBlock.of(c))) {
				setNavigationMessage(req, NavigationMessage.warn("Impossibile registrare questo nick: C'e' un carattere monello '" + c + "'"));
				return "register.jsp";
			}
		}
		String pass = req.getParameter("pass");
		if (StringUtils.isEmpty(pass) || pass.length() > 20) {
			setNavigationMessage(req, NavigationMessage.warn("Scegli una password migliore, giovane jedi ..."));
			return "register.jsp";
		}
		AuthorDTO loggedUser = getPersistence().registerUser(nick, pass);
		if (!loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Impossibile registrare questo nick, probabilmente gia' esiste"));
			return "register.jsp";
		}
		// login
		login(req);
		req.setAttribute("loggedUser", loggedUser);
		return "user.jsp";
	}

	/**
	 * Carica le frasi celebri dal database
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action
	String getQuotes(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}

		List<QuoteDTO> list = getPersistence().getQuotes(loggedUser);
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
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action
	String updateQuote(HttpServletRequest req, HttpServletResponse res) throws Exception {
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

		getPersistence().insertUpdateQuote(quote);
		return getQuotes(req,  res);
	}

	/**
	 * Cancella una quote
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action
	String removeQuote(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}

		Long quoteId = Long.parseLong(req.getParameter("quoteId"));
		QuoteDTO quote = new QuoteDTO();
		quote.setNick(loggedUser.getNick());
		quote.setId(quoteId);

		getPersistence().removeQuote(quote);
		return getQuotes(req,  res);
	}

	/**
	 * Tutte le informazioni dell'utente
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action
	String getUserInfo(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String nick = req.getParameter("nick");
		AuthorDTO author = getPersistence().getAuthor(nick);
		req.setAttribute("author", author);
		req.setAttribute("quotes", getPersistence().getQuotes(author));

		final AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);

		if ((loggedUser != null) && "yes".equals(loggedUser.getPreferences().get("super"))) {
			final String token = RandomPool.getString(3);
			req.getSession().setAttribute(ANTI_XSS_TOKEN, token);
			req.setAttribute("token", token);
		}

		return "userInfo.jsp";
	}

	/**
	 *
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
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

		if ((token == null) || (inToken == null) || !token.equals(inToken)) {
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

		final AuthorDTO author = getPersistence().getAuthor(nick);

		if ((author == null) || !author.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Il nickname è sparito!?"));
			return getUserInfo(req,  res);
		}

		if (!StringUtils.isEmpty(pass)) {
			if (StringUtils.isEmpty(pass2) || !pass2.equals(pass)) {
				setNavigationMessage(req, NavigationMessage.warn("Password sbagliata"));
				return getUserInfo(req,  res);
			}

			if (!getPersistence().updateAuthorPassword(author, pass)) {
				setNavigationMessage(req, NavigationMessage.error("Errore in User.editUser / updateAuthorPassword -- molto probabilmente e` colpa di sarrusofono, faglielo sapere -- sempre ammesso che tu riesca a postare sul forum a questo punto :("));
				return getUserInfo(req,  res);
			}
		}

		final String pedonizeThread = req.getParameter("pedonizeThread");
		if (!StringUtils.isEmpty(pedonizeThread)) {
			author.setPreferences(getPersistence().setPreference(author, "pedonizeThread", pedonizeThread));
		} else {
			author.setPreferences(getPersistence().setPreference(author, "pedonizeThread", ""));
		}

		return getUserInfo(req,  res);
	}

	/**
	 * Cambia le preferences
	 */
	@Action
	String updatePreferences(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}

		// setta le preferences
		for (String key : new String[] {PREF_SHOWANONIMG, PREF_EMBEDDYT, PREF_COLLAPSE_QUOTES, PREF_HIDE_BANNERONE,
				PREF_MSG_MAX_HEIGHT, PREF_AUTO_REFRESH, PREF_HIDE_SIGNATURE, PREF_COMPACT_SIGNATURE, PREF_BLOCK_HEADER,
				PREF_LARGE_STYLE, PREF_HIDE_FAKE_ADS}) {
			String value = req.getParameter(key);
			if (StringUtils.isNotEmpty(value)) {
				loggedUser.setPreferences(getPersistence().setPreference(loggedUser, key, "checked"));
				if (key.equals(PREF_LARGE_STYLE)) {
					loggedUser.setPreferences(getPersistence().setPreference(loggedUser, "sidebarStatus", "hide"));
				}
			} else {
				loggedUser.setPreferences(getPersistence().setPreference(loggedUser, key, ""));
				if (key.equals(PREF_LARGE_STYLE)) {
					loggedUser.setPreferences(getPersistence().setPreference(loggedUser, "sidebarStatus", "show"));
				}
			}
		}

		String[] hiddenForums = req.getParameterValues(PREF_HIDDEN_FORUMS);
		List<String> forumsToHide = new ArrayList<String>();
		if (hiddenForums != null) {
			forumsToHide.addAll(Arrays.asList(hiddenForums));
		}
		getPersistence().setHiddenForums(loggedUser, forumsToHide);
		req.setAttribute(PREF_HIDDEN_FORUMS, getPersistence().getHiddenForums(loggedUser));

		String theme = req.getParameter(PREF_THEME);
		if (StringUtils.isNotEmpty(theme)) {
			loggedUser.setPreferences(getPersistence().setPreference(loggedUser, PREF_THEME, theme));
		}

		return "user.jsp";
	}

	/**
	 * Mostra le notifiche
	 * @param req
	 * @param res
	 * @return
	 */
	@Action
	String getNotifications(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}

		setWebsiteTitlePrefix(req, "Notifiche");
		req.setAttribute("notificationsFrom", getPersistence().getNotifications(loggedUser.getNick(), null));
		req.setAttribute("notificationsTo", getPersistence().getNotifications(null, loggedUser.getNick()));

		return "notifications.jsp";
	}

	/**
	 * Mostra le notifiche
	 * @param req
	 * @param res
	 * @return
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
		getPersistence().removeNotification(loggedUser.getNick(), null, notificationId);

		return getNotifications(req, res);
	}

	/**
	 * Notifica un utente
	 * @param req
	 * @param res
	 * @return
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

		if (getPersistence().getNotifications(loggedUser.getNick(), null).size() > 9) {
			writer.beginObject();
			writer.name("resultCode").value("ERROR");
			writer.name("content").value("Hai gia' 10 notifiche: Vergognati spammone !!");
			writer.endObject();
			writer.flush();
			writer.close();
			return null;
		}

		getPersistence().createNotification(loggedUser.getNick(), toNick, msgId);
		writer.beginObject();
		writer.name("resultCode").value("OK");
		writer.endObject();
		writer.flush();
		writer.close();

		return null;
	}

	/**
	 * Firma
	 * @param req
	 * @param res
	 * @return
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
		for (@SuppressWarnings("unchecked")
		Iterator<FileItem> it = uploadHandler.parseRequest(req).iterator(); it.hasNext(); ) {
			FileItem item = it.next();
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
				getPersistence().updateAuthor(loggedUser);
				loggedUser.setPreferences(getPersistence().setPreference(loggedUser, "signature", signature));
			} else {
				loggedUser.setPreferences(getPersistence().setPreference(loggedUser, "signature", signature));
			}
		} else if ("Elimina".equals(submitBtn)) {
			loggedUser.setSignatureImage(null);
			getPersistence().updateAuthor(loggedUser);
			loggedUser.setPreferences(getPersistence().setPreference(loggedUser, "signature", ""));
		} else {
			setNavigationMessage(req, NavigationMessage.error("Nessuna operazione eseguita !"));
			return "user.jsp";
		}
		setNavigationMessage(req, NavigationMessage.info("Firma modificato con successo !"));
		return "user.jsp";

	}

	protected static final class AvailableTorRegistrations {
		protected int available = 1;
		protected long lastUpdate = System.currentTimeMillis();
		protected static final double HOURLY_PROBABILITY = 0.2;
		protected static final int MAX_HOURS = 6;

		public boolean available() {
			synchronized(this) {
				if (available > 0) {
					--available;
					return true;
				}

				final long now = System.currentTimeMillis();
				final long interval = now - lastUpdate;

				long intervalInHours = interval  / (60 * 60 * 1000);

				if (intervalInHours <= 0) return false;

				lastUpdate = now;

				if (intervalInHours > MAX_HOURS) intervalInHours = MAX_HOURS;

				for (int i = 0; i < intervalInHours; ++i) {
					if (Math.random() < HOURLY_PROBABILITY) ++available;
				}

				return available();
			}
		}
	}

	protected static AvailableTorRegistrations availableTorRegistrations = new AvailableTorRegistrations();
}
