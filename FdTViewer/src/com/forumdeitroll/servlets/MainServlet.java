package com.forumdeitroll.servlets;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.forumdeitroll.PasswordUtils;
import com.forumdeitroll.RandomPool;
import com.forumdeitroll.SingleValueCache;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.IPersistence;
import com.forumdeitroll.persistence.PersistenceFactory;
import com.forumdeitroll.persistence.QuoteDTO;

public abstract class MainServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(MainServlet.class);

	private static final long serialVersionUID = 1L;

	public static final int PAGE_SIZE = 20;

	public static final String LOGGED_USER_REQ_ATTR = "loggedUser";
	private static final String LOGGED_USER_SESS_ATTR = "loggedUserNick";
	public static final String SESSION_IS_BANNED = "sessionIsBanned";

	public static final String ANTI_XSS_TOKEN = "anti_xss_token";

	private IPersistence persistence;

	private Map<String, Method> actionMethodCache;

	protected SingleValueCache<List<String>> cachedForums = new SingleValueCache<List<String>>(60 * 60 * 1000) {
		@Override protected List<String> update() {
			return getPersistence().getForums();
		}
	};

	protected SingleValueCache<List<QuoteDTO>> cachedQuotes = new SingleValueCache<List<QuoteDTO>>(60 * 60 * 1000) {
		@Override protected List<QuoteDTO> update() {
			return getPersistence().getAllQuotes();
		}
	};

	protected SingleValueCache<List<String>> cachedTitles = new SingleValueCache<List<String>>(60 * 60 * 1000) {
		@Override protected List<String> update() {
			return getPersistence().getTitles();
		}
	};

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			persistence = PersistenceFactory.getInstance();
		} catch (Exception e) {
			LOG.fatal(e);
			throw new ServletException("Cannot instantiate persistence", e);
		}
		actionMethodCache = new HashMap<String, Method>();
	}

	public final void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		doGetPost(req, res, Action.Method.GET);
	}

	public final void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		doGetPost(req, res, Action.Method.POST);
	}

	private void doGetPost(HttpServletRequest req, HttpServletResponse res, Action.Method method) throws IOException {
		try {
			doBefore(req, res);
			String page = doDo(req, res, method);
			doAfter(req, res);
			// forward
			if (page != null) {
				if (req.getAttribute("websiteTitle") == null)
					setWebsiteTitlePrefix(req, "");
				req.setAttribute("page", page);
				getServletContext().getRequestDispatcher("/pages/main.jsp").forward(req, res);
			}
		} catch (Exception e) {
			handleException(e, req, res);
		}
	}

	/**
	 * Called before {@link #doDo(HttpServletRequest, HttpServletResponse, com.forumdeitroll.servlets.Action.Method)}
	 * @param req
	 * @param res
	 */
	public void doBefore(HttpServletRequest req, HttpServletResponse res) {
		// implement in subclassed
	}

	/**
	 * Called after {@link #doDo(HttpServletRequest, HttpServletResponse, com.forumdeitroll.servlets.Action.Method)}
	 * @param req
	 * @param res
	 */
	public void doAfter(HttpServletRequest req, HttpServletResponse res) {
		// implement in subclassed
	}

	protected void userSessionBanContagion(final HttpServletRequest req, final AuthorDTO loggedUser) {
		if (!loggedUser.isValid()) return;

		if (req.getSession().getAttribute(SESSION_IS_BANNED) != null) {
			// sessione bannata contagia utente
			if (!(loggedUser.isBanned())) {
				getPersistence().updateAuthorPassword(loggedUser, null);
			}
		}

		if (loggedUser.isBanned()) {
			// utente bannato contagia sessione
			req.getSession().setAttribute(SESSION_IS_BANNED, "yes");
		}
	}

	private final String doDo(HttpServletRequest req, HttpServletResponse res, Action.Method method) throws Exception {

		String servlet = this.getClass().getSimpleName();
		req.setAttribute("servlet", servlet);

		// forums
		req.setAttribute("forums", cachedForums.get());

		// random quote
		req.setAttribute("randomQuote", getRandomQuoteDTO(req, res));

		// javascript maGGico
		req.setAttribute("javascript", persistence.getSysinfoValue("javascript"));

		// user
		String loggedUserNick = (String)req.getSession().getAttribute(LOGGED_USER_SESS_ATTR);
		String sidebarStatus = null;
		String blockHeaderStatus = null;
		if (!StringUtils.isEmpty(loggedUserNick)) {
			// update loggedUser in session
			AuthorDTO loggedUser = persistence.getAuthor(loggedUserNick);
			req.setAttribute(LOGGED_USER_REQ_ATTR, loggedUser);

			userSessionBanContagion(req, loggedUser);

			// pvts ?
			req.setAttribute("hasPvts", getPersistence().checkForNewPvts(loggedUser));
			// sidebar status come attributo nel reques
			sidebarStatus = loggedUser.getPreferences().get("sidebarStatus");
			blockHeaderStatus = loggedUser.getPreferences().get("blockHeader");
		} else {
			// status sidebar nel cookie ?
			if (req.getCookies() != null) {
				for (Cookie cookie : req.getCookies()) {
					if ("sidebarStatus".equals(cookie.getName())) {
						sidebarStatus = cookie.getValue();
						break;
					} else if ("blockHeader".equals(cookie.getName())) {
						blockHeaderStatus = cookie.getValue();
					}
				}
			}
		}
		if (StringUtils.isEmpty(sidebarStatus)) {
			sidebarStatus = "show";
		}
		req.setAttribute("sidebarStatus", sidebarStatus);
		if (blockHeaderStatus == null) {
			blockHeaderStatus = "";
		}
		req.setAttribute("blockHeader", blockHeaderStatus);


		// execute action
		String action = (String)req.getAttribute("action");
		if (action == null) {
			LOG.error("action è null per la request "+req.getRequestURI()+": hai messo l'<url-pattern> nel filter (web.xml)?");
		}
		Method actionMethod = getActionMethod(action);
		if (actionMethod == null) {
			throw new IllegalArgumentException("Azione sconosciuta: " + action);
		}
		Action a = actionMethod.getAnnotation(Action.class);
		if (a == null) {
			throw new IllegalArgumentException("L'action " + action + " non e' definita");
		}
		if (a.method() == Action.Method.GETPOST || a.method() == method) {
			return (String)actionMethod.invoke(this, new Object[] {req, res});
		}
		throw new IllegalArgumentException("Azione " + action + " non permette il metodo " + method);
	}

	/**
	 * Action chiamata quando nessuna e' definita
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action
	abstract String init(HttpServletRequest req, HttpServletResponse res) throws Exception;

	/**
	 * La persistence inizializzata
	 * @return
	 */
	protected final IPersistence getPersistence() {
		return persistence;
	}

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
			if (PasswordUtils.hasUserPassword(author, pass)) {
				// ok, ci siamo loggati con successo, salvare come attributo nel req
				req.setAttribute(LOGGED_USER_REQ_ATTR, author);
				req.getSession().setAttribute(LOGGED_USER_SESS_ATTR, nick);
				if (!author.newAuth()) {
					// CODICE DI MIGRAZIONE DELLA FUNZIONE DI HASHING DELLE PASSWORD
					// autenticazione con username e password effettuata con successo ma
					// nome utente e password sono salvati con la vecchia funzione di hashing
					getPersistence().updateAuthorPassword(author, pass);
				}
				return author;
			}
			return new AuthorDTO(null);
		}

		// se non e` stato specificato nome utente e password tentiamo l'autenticazione tramite sessione
		final AuthorDTO author = (AuthorDTO)req.getAttribute(LOGGED_USER_REQ_ATTR);
		if (author != null) {
			if (!author.newAuth()) {
				// CODICE DI MIGRAZIONE DELLA FUNZIONE DI HASHING DELLE PASSWORD
				// l'utente e` loggato con il cookie ma la password usa sempre l'hashing vecchio, piallare il
				// cookie in modo che debba rifare il login e aggiornare l'hashing.
				req.removeAttribute(LOGGED_USER_REQ_ATTR);
			}
			return author;
		}

		// captcha corretto, restituisce l'Author di default
		return new AuthorDTO(null);
	}

	/**
	 * Setta nella session lo stato della sidebar (Aperta/chiusa)
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action
	String updateSidebarStatus(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String sidebarStatus = req.getParameter("sidebarStatus");
		if (StringUtils.isEmpty(sidebarStatus)) {
			return null;
		}
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser != null && loggedUser.isValid()) {
			// settiamo nelle preferences dell'utente
			getPersistence().setPreference(loggedUser, "sidebarStatus", sidebarStatus);
			loggedUser.getPreferences().put("sidebarStatus", sidebarStatus);
		} else {
			// settiamo nel cookie
			if (req.getCookies() != null) {
				for (Cookie cookie : req.getCookies()) {
					if ("sidebarStatus".equals(cookie.getName())) {
						cookie.setValue(sidebarStatus);
						res.addCookie(new Cookie("sidebarStatus", sidebarStatus));
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Setta nella session lo stato dell'header fisso
	 */
	@Action
	String updateBlockHeaderStatus(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String blockHeaderStatus = req.getParameter("blockHeader");
		if (blockHeaderStatus == null) {
			return null;
		}
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser != null && loggedUser.isValid()) {
			getPersistence().setPreference(loggedUser, "blockHeader", blockHeaderStatus);
			loggedUser.getPreferences().put("blockHeader", blockHeaderStatus);
		} else {
			boolean cookieSet = false;
			if (req.getCookies() != null) {
				for (Cookie cookie : req.getCookies()) {
					if ("blockHeader".equals(cookie.getName())) {
						cookie.setValue(blockHeaderStatus);
						res.addCookie(new Cookie("blockHeader", blockHeaderStatus));
						cookieSet = true;
					}
				}
			}
			if (!cookieSet) {
				res.addCookie(new Cookie("blockHeader", blockHeaderStatus));
			}
		}
		return null;
	}

	/**
	 * Ritorna la pagina attuale se definita come req param, altrimenti 0. Setta il valore come req attr.
	 * @param req
	 * @return
	 */
	protected int getPageNr(HttpServletRequest req) {
		String page = req.getParameter("page");
		if (page == null) {
			page = "0";
		}
		req.setAttribute("page", page);
		return Integer.parseInt(page);
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
	protected void setWebsiteTitlePrefix(HttpServletRequest req, String prefix) {
		if (StringUtils.isNotEmpty(prefix)) {
			prefix = prefix + " @ ";
		}
		if (Math.random() > .5) {
			req.setAttribute("websiteTitle", StringEscapeUtils.escapeHtml4(prefix + "Forum dei Troll"));
		} else {
			List<String> titles = cachedTitles.get();
			if (titles.isEmpty()) {
				req.setAttribute("websiteTitle", StringEscapeUtils.escapeHtml4(prefix + "Forum dei Troll"));
			} else {
				String title = prefix + "Forum " + titles.get(new Random().nextInt(titles.size()));
				req.setAttribute("websiteTitle", StringEscapeUtils.escapeHtml4(title));
			}
		}
	}

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
	QuoteDTO getRandomQuoteDTO(HttpServletRequest req, HttpServletResponse res) {
		if (cachedQuotes.get().size() > 0) {
			final QuoteDTO quote = cachedQuotes.get().get(RandomPool.insecureInt(cachedQuotes.get().size()));
			final QuoteDTO newquote = new QuoteDTO(quote);
			newquote.setContent(StringEscapeUtils.escapeHtml4(quote.getContent()));
			return newquote;
		}
		return new QuoteDTO();
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
		LOG.error(e.getMessage(), e);
		req.setAttribute("exceptionStackTrace", ExceptionUtils.getStackTrace(e));
		try {
			getServletContext().getRequestDispatcher("/pages/error.jsp").forward(req, res);
		} catch (ServletException e1) {
			for (StackTraceElement elem : e1.getStackTrace()) {
				res.getWriter().write(elem + "<br/>");
			}
		}
	}

	void addSpecificParam(HttpServletRequest req, String key, String value) {
		@SuppressWarnings("unchecked")
		Map<String, String> specificParams = (Map<String, String>)req.getAttribute("specificParams");
		if (specificParams == null) {
			specificParams = new HashMap<String, String>();
			req.setAttribute("specificParams", specificParams);
		}
		if (value != null) {
			specificParams.put(key, value);
		}
	}

	protected void setAntiXssToken(final HttpServletRequest req) {
		req.getSession().setAttribute(ANTI_XSS_TOKEN, RandomPool.getString(3));
	}

	protected boolean antiXssOk(final HttpServletRequest req) {
		final String token = (String)req.getSession().getAttribute(ANTI_XSS_TOKEN);
		final String inToken = req.getParameter("token");

		return (token != null) && (inToken != null) && token.equals(inToken);
	}

	protected boolean hideProcCatania(HttpServletRequest req) throws NoSuchAlgorithmException {
		String forum = req.getParameter("forum");
		if (IPersistence.FORUM_PROC.equals(forum)) {
			return false; // nascondere la proc quando si consulta la proc :P ?
		}
		AuthorDTO loggedUser = login(req);
		if (loggedUser.isValid()) {
			return StringUtils.isNotEmpty(loggedUser.getPreferences().get(User.PREF_HIDE_PROC_CATANIA));
		}
		return false; // utenti non registrati: mostra
	}

	/**
	 * Mappa action -> Actionmethod.
	 * @param action
	 * @return
	 */
	private final Method getActionMethod(String action) {
		Method actionMethod = null;
		if (!actionMethodCache.containsKey(action)) {
			Class servletClass = this.getClass();
			synchronized (actionMethodCache) {
				if (!actionMethodCache.containsKey(action)) {
					while (actionMethod == null && MainServlet.class.isAssignableFrom(servletClass)) {
						// search action method also in superclasses
						try {
							actionMethod = servletClass.getDeclaredMethod(action, new Class[] {HttpServletRequest.class, HttpServletResponse.class});
						} catch (NoSuchMethodException e) {
							servletClass = servletClass.getSuperclass();
						}
					}
					// Una key puo' avere value == null, cosi' da evitare la ricerca in futuro.
					actionMethodCache.put(action, actionMethod);
				}
			}
		} else {
			actionMethod = actionMethodCache.get(action);
		}
		return actionMethod;
	}
}
