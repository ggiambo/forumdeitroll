package com.acmetoy.ravanator.fdt.servlets;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.gimpy.RippleGimpyRenderer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.producer.NumbersAnswerProducer;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.IPersistence;
import com.acmetoy.ravanator.fdt.persistence.PersistenceFactory;
import com.acmetoy.ravanator.fdt.persistence.QuoteDTO;

public abstract class MainServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(MainServlet.class);

	private static final long serialVersionUID = 1L;

	protected static final int PAGE_SIZE = 20;

	public static final String LOGGED_USER_SESSION_ATTR = "loggedUser";

	private byte[] notAuthenticated;
	private byte[] noAvatar;

	private IPersistence persistence;

	protected final Map<String, GiamboAction> mapGet = new HashMap<String, GiamboAction>();
	protected final Map<String, GiamboAction> mapPost = new HashMap<String, GiamboAction>();

	protected static final int ONGET = 0x01;
	protected static final int ONPOST = 0x02;

	protected volatile List<String> cachedForums = null;
	protected long cachedForumsTimestamp = -1;
	protected static final long FORUMS_CACHE_EXPIRATION_TIME = 60 * 60 * 1000;

	protected abstract class GiamboAction {
		public GiamboAction(final String name, final int when) {
			if ((when & ONGET) != 0) {
				mapGet.put(name, this);
			}

			if ((when & ONPOST) != 0) {
				mapPost.put(name, this);
			}
		}

		public abstract String action(HttpServletRequest req, HttpServletResponse res) throws Exception;
	}


	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);

		try {
			// anonimo
			InputStream is = config.getServletContext().getResourceAsStream("/images/avataranonimo.gif");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int count = -1;
			while ((count = is.read(buffer)) != -1) {
				bos.write(buffer, 0, count);
			}
			notAuthenticated = bos.toByteArray();

			// default
			is = config.getServletContext().getResourceAsStream("/images/avatardefault.gif");
			bos = new ByteArrayOutputStream();
			buffer = new byte[1024];
			count = -1;
			while ((count = is.read(buffer)) != -1) {
				bos.write(buffer, 0, count);
			}
			noAvatar = bos.toByteArray();

			//
			config.getServletContext().setAttribute("PAGE_SIZE", PAGE_SIZE);

		} catch (IOException e) {
			LOG.error(e);
			throw new ServletException("Cannot read default images", e);
		}

		try {
			persistence = PersistenceFactory.getInstance();
		} catch (Exception e) {
			LOG.fatal(e);
			throw new ServletException("Cannot instantiate persistence", e);
		}
	}

	public final void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		doDo(req, res, mapGet);
	}

	public final void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		doDo(req, res, mapPost);
	}

	protected void maybeUpdateCachedForums() {
		if ((cachedForums == null) || (System.currentTimeMillis() - cachedForumsTimestamp > FORUMS_CACHE_EXPIRATION_TIME)) {
			synchronized (this) {
				if ((cachedForums == null) || (System.currentTimeMillis() - cachedForumsTimestamp > FORUMS_CACHE_EXPIRATION_TIME)) {
					cachedForumsTimestamp = System.currentTimeMillis();
					final List<String> forums = getPersistence().getForums();
					cachedForums = forums;
				}
			}
		}
	}

	public final void doDo(HttpServletRequest req, HttpServletResponse res, final Map<String, GiamboAction> map) throws IOException {
		// I love UTF-8
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");

		// mostrare di disclaimer ?
		if (req.getSession(false) == null && !"OK".equals(req.getParameter("disclaimer"))) {
			try {
				getServletContext().getRequestDispatcher("/pages/disclaimer.jsp").forward(req, res);
			} catch (ServletException e) {
				handleException(e, req, res);
			}
			return;
		}

		// actual time
		req.setAttribute("currentTimeMillis", System.currentTimeMillis());

		maybeUpdateCachedForums();

		// forums
		req.setAttribute("forums", cachedForums);

		// sidebar status
		setSidebarStatusInSession(req, res);

		// action
		String action = req.getParameter("action");
		if (action == null || action.trim().length() == 0) {
			action = "init";
		}
		req.setAttribute("action", action);
		req.setAttribute("servlet", this.getClass().getName());

		// hack per persistere la sessione -- sarrusofono
		final HttpSession session = req.getSession();
		if (session.isNew()) {
			String id = session.getId();
			res.setHeader("Set-Cookie", String.format("JSESSIONID=%s;Max-Age=%d;Path=/", id, 365*24*60*60));
		}

		// random quote
		req.setAttribute("randomQuote", getRandomQuote(req, res));
		
		// update loggedUser in session
		AuthorDTO loggedUser = (AuthorDTO)session.getAttribute(LOGGED_USER_SESSION_ATTR);
		if (loggedUser != null && loggedUser.isValid()) {
			session.setAttribute(LOGGED_USER_SESSION_ATTR, getPersistence().getAuthor(loggedUser.getNick()));
		}

		try {
			// call via reflection
			GiamboAction giamboAction = map.get(action);
			if (giamboAction == null) {
				throw new IllegalArgumentException("Azione sconosciuta: " + action);
			} else {
				String pageForward = giamboAction.action(req, res);
				// forward
				if (pageForward != null) {
					getServletContext().getRequestDispatcher("/pages/" + pageForward).forward(req, res);
				}
			}
		} catch (Exception e) {
			handleException(e, req, res);
		}
	}

	/**
	 * La persistence inizializzata
	 * @return
	 */
	protected final IPersistence getPersistence() {
		return persistence;
	}

	/**
	 * Scrive direttamente nella response i bytes che compongono l'avatar
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction getAvatar = new GiamboAction("getAvatar", ONGET|ONPOST) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			String nick = req.getParameter("nick");
			AuthorDTO author = getPersistence().getAuthor(nick);
			if (author.isValid()) {
				if (author.getAvatar() != null) {
					res.getOutputStream().write(author.getAvatar());
				} else {
					res.getOutputStream().write(noAvatar);
				}
			} else {
				res.getOutputStream().write(notAuthenticated);
			}
			return null;
		}
	};

	/**
	 * Tenta un login: Ritorna AuthorDTO valido se OK, AuthorDTO invalido se e'
	 * stato inserito un captcha giusto, null se autenticazione fallita.
	 * @param req
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	protected AuthorDTO login(final HttpServletRequest req) throws NoSuchAlgorithmException {
		// check username and pass, se inseriti
		String nick = req.getParameter("nick");
		String pass = req.getParameter("pass");
		if (!StringUtils.isEmpty(nick) && !StringUtils.isEmpty(pass)) {
			final AuthorDTO author = getPersistence().getAuthor(nick);
			if (author.passwordIs(pass)) {
				// ok, ci siamo loggati con successo, salvare nella sessione
				req.getSession().setAttribute(LOGGED_USER_SESSION_ATTR, author);
				if (!author.newAuth()) {
					// CODICE DI MIGRAZIONE DELLA FUNZIONE DI HASHING DELLE PASSWORD
					// autenticazione con username e password effettuata con successo ma
					// nome utente e password sono salvati con la vecchia funzione di hashing
					getPersistence().updateAuthorPassword(author, pass);
				}
				return author;
			} else {
				return new AuthorDTO();
			}
		}

		// se non e` stato specificato nome utente e password tentiamo l'autenticazione tramite sessione
		final AuthorDTO author = (AuthorDTO)req.getSession().getAttribute(LOGGED_USER_SESSION_ATTR);
		if (author != null) {
			if (!author.newAuth()) {
				// CODICE DI MIGRAZIONE DELLA FUNZIONE DI HASHING DELLE PASSWORD
				// l'utente e` loggato con il cookie ma la password usa sempre l'hashing vecchio, piallare il
				// cookie in modo che debba rifare il login e aggiornare l'hashing.
				req.getSession().removeAttribute(LOGGED_USER_SESSION_ATTR);
			}
			return author;
		}

		// captcha corretto, restituisce l'Author di default
		return new AuthorDTO();
	}

	/**
	 * Cancella l'utente loggato dalla sessione
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction logoutAction = new GiamboAction("logoutAction", ONGET|ONPOST) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			req.getSession().removeAttribute(LOGGED_USER_SESSION_ATTR);
			req.getSession().invalidate();
			return mapGet.get("init").action(req, res);
		}
	};

	/**
	 * Setta lo stato della sidebar nella session, nelle preferenze utente o nel cookie se non e' gia' stato fatto.
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private void setSidebarStatusInSession(HttpServletRequest req, HttpServletResponse res) {
		String sidebarStatus = (String) req.getSession().getAttribute("sidebarStatus");
		if (StringUtils.isEmpty(sidebarStatus)) {
			// proviamo a leggerla dalle preferences dell'utente
			AuthorDTO loggedUser = (AuthorDTO)req.getSession().getAttribute(MainServlet.LOGGED_USER_SESSION_ATTR);
			if (loggedUser != null && loggedUser.isValid()) {
				sidebarStatus = getPersistence().getPreferences(loggedUser).getProperty("sidebarStatus");
				if (StringUtils.isEmpty(sidebarStatus)) {
					// update preference - default "show"
					sidebarStatus = "show";
					getPersistence().setPreference(loggedUser, "sidebarStatus", sidebarStatus);
				}
			} else {
				// utente non loggato, andiamo di cookie ...
				if (req.getCookies() != null) {
					for (Cookie cookie : req.getCookies()) {
						if ("sidebarStatus".equals(cookie.getName())) {
							sidebarStatus = cookie.getValue();
						}
					}
				}
				if (StringUtils.isEmpty(sidebarStatus)) {
					// update nel cookie - default "show"
					sidebarStatus = "show";
					res.addCookie(new Cookie("sidebarStatus", sidebarStatus));
				}
			}
			// settiamo nella session
			req.getSession().setAttribute("sidebarStatus", sidebarStatus);
		}
	}

	/**
	 * Setta nella session lo stato della sidebar (Aperta/chiusa)
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction updateSidebarStatus = new GiamboAction("updateSidebarStatus", ONGET|ONPOST) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			String sidebarStatus = req.getParameter("sidebarStatus");
			AuthorDTO loggedUser = (AuthorDTO)req.getSession().getAttribute(MainServlet.LOGGED_USER_SESSION_ATTR);
			if (loggedUser != null && loggedUser.isValid()) {
				// settiamo nelle preferences dell'utente
				getPersistence().setPreference(loggedUser, "sidebarStatus", sidebarStatus);
			} else {
				// settiamo nel cookie
				boolean found = false;
				if (req.getCookies() != null) {
					for (Cookie cookie : req.getCookies()) {
						if ("sidebarStatus".equals(cookie.getName())) {
							cookie.setValue(sidebarStatus);
							found = true;
						}
					}
				}
				if (!found) {
					res.addCookie(new Cookie("sidebarStatus", sidebarStatus));
				}
			}
			// settiamo nella session
			req.getSession().setAttribute("sidebarStatus", sidebarStatus);

			return null;
		}
	};

	/**
	 * Ritorna la pagina attuale se definita come req param, altrimenti 0. Setta il valore come req attr.
	 * @param req
	 * @return
	 */
	protected int getPageNr(HttpServletRequest req) {
		// pageNr
		String pageNr = req.getParameter("pageNr");
		if (pageNr == null) {
			pageNr = "0";
		}
		req.setAttribute("pageNr", pageNr);
		return Integer.parseInt(pageNr);
	}

	/**
	 * Messaggio mostrato tra l'header e la navigazione
	 * @param req
	 * @param navigationMessage
	 */
	protected void setNavigationMessage(HttpServletRequest req, NavigationMessage navigationMessage) {
		req.setAttribute("navigationMessage", navigationMessage);
	}

	/**
	 * Imposta il titolo del sito
	 * @param req
	 * @param websiteTitle
	 */
	protected void setWebsiteTitle(HttpServletRequest req, String websiteTitle) {
		req.setAttribute("websiteTitle", StringEscapeUtils.escapeHtml4(websiteTitle));
	}

	/**
	 * Genera un captcha
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction getCaptcha = new GiamboAction("getCaptcha", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			Captcha captcha = new Captcha.Builder(150, 50)
					.addText(new NumbersAnswerProducer(6))
					.addBackground(new GradiatedBackgroundProducer(Color.MAGENTA, Color.CYAN))
					.gimp(new RippleGimpyRenderer())
					.build();
			res.setHeader("Cache-Control", "no-store");
			res.setHeader("Pragma", "no-cache");
			res.setDateHeader("Expires", 0);
			res.setContentType("image/jpeg");
			CaptchaServletUtil.writeImage(res, captcha.getImage());
			req.getSession().setAttribute("captcha", captcha.getAnswer());
			return null;
		}
	};

	public static class NavigationMessage {

		static enum TYPE {
			INFO, WARN, ERROR;
		}

		private String content;

		private TYPE type;

		private NavigationMessage(TYPE type, String content) {
			this.type = type;
			this.content = content;
		}

		public static NavigationMessage info(String content) {
			return new NavigationMessage(TYPE.INFO, content);
		}

		public static NavigationMessage warn(String content) {
			return new NavigationMessage(TYPE.WARN, content);
		}

		public static NavigationMessage error(String content) {
			return new NavigationMessage(TYPE.ERROR, content);
		}

		public String getContent() {
			return content;
		}

		public String getType() {
			return type.name();
		}

	}

	/**
	 * Ritorna una random quote tra quelle esistenti
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public QuoteDTO getRandomQuote(HttpServletRequest req, HttpServletResponse res) {
		QuoteDTO quote = getPersistence().getRandomQuote();
		quote.setContent(StringEscapeUtils.escapeHtml4(quote.getContent()));
		return quote;
	}

	/**
	 * Scrive l'exception nel log, mostra la pagina d'errore o scrive
	 * direttamete la stacktrace nella response
	 *
	 * @param e
	 * @param req
	 * @param res
	 * @throws IOException
	 */
	private void handleException(Exception e, HttpServletRequest req, HttpServletResponse res) throws IOException {
		LOG.error(e);
		req.setAttribute("exceptionStackTrace", ExceptionUtils.getStackTrace(e));
		try {
			getServletContext().getRequestDispatcher("/pages/error.jsp").forward(req, res);
		} catch (ServletException e1) {
			for (StackTraceElement elem : e1.getStackTrace()) {
				res.getWriter().write(elem + "<br/>");
			}
		}
	}
}