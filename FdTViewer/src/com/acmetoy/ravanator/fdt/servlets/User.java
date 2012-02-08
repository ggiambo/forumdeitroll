package com.acmetoy.ravanator.fdt.servlets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.QuoteDTO;

public class User extends MainServlet {

	private static final long serialVersionUID = 1L;

	private static final int MAX_SIZE_AVATAR_BYTES = 512*1024;
	private static final long MAX_SIZE_AVATAR_WIDTH = 100;
	private static final long MAX_SIZE_AVATAR_HEIGHT = 100;
	
	public static final String PREF_SHOWANONIMG = "showAnonImg";
	public static final String PREF_EMBEDDYT = "embeddYt";

	protected GiamboAction init = new GiamboAction("init", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			AuthorDTO loggedUser = login(req);
			setWebsiteTitle(req, "Forum dei troll");
			if (loggedUser != null && loggedUser.isValid()) {
				return "user.jsp";
			}
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction.action(req,  res);
		}
	};

	/**
	 * Mostra la pagina di login
	 * @param req
	 * @param res
	 * @return
	 */
	protected GiamboAction loginAction = new GiamboAction("loginAction", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			setWebsiteTitle(req, "Login @ Forum dei Troll");
			return "login.jsp";
		}
	};

	/**
	 * Update della password
	 * @param req
	 * @param res
	 * @return
	 */
	protected GiamboAction updatePass = new GiamboAction("updatePass", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			AuthorDTO loggedUser = (AuthorDTO)req.getSession().getAttribute(MainServlet.LOGGED_USER_SESSION_ATTR);
			if (loggedUser == null || !loggedUser.isValid()) {
				setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
				return loginAction.action(req,  res);
			}

			// user loggato, check pass
			String actualPass = req.getParameter("actualPass");
			if (StringUtils.isEmpty(actualPass)) {
				setNavigationMessage(req, NavigationMessage.warn("Inserisci la password attuale"));
				return "user.jsp";
			}

			if (!loggedUser.passwordIs(actualPass)) {
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
	};

	/**
	 * Update avatar
	 * @param req
	 * @param res
	 * @return
	 */
	protected GiamboAction updateAvatar = new GiamboAction("updateAvatar", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			AuthorDTO loggedUser = (AuthorDTO)req.getSession().getAttribute(MainServlet.LOGGED_USER_SESSION_ATTR);
			if (loggedUser == null || !loggedUser.isValid()) {
				setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
				return loginAction.action(req,  res);
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
			Iterator<FileItem> it = uploadHandler.parseRequest(req).iterator();
			if (it.hasNext()) {
				FileItem avatar = it.next();
				if (avatar.getSize() > MAX_SIZE_AVATAR_BYTES) {
					setNavigationMessage(req, NavigationMessage.warn("Megalomane, avatar troppo grande, al massimo 512K !"));
					return "user.jsp";
				}
				// carica l'immagine
				BufferedImage image = ImageIO.read(avatar.getInputStream());
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
	};

	/**
	 * Pagina per registrare un nuovo user
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction registerAction = new GiamboAction("registerAction", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			req.getSession().removeAttribute(LOGGED_USER_SESSION_ATTR);
			setWebsiteTitle(req, "Registrazione @ Forum dei Troll");
			return "register.jsp";
		}
	};

	/**
	 * Registra nuovo user
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction registerNewUser = new GiamboAction("registerNewUser", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
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
			if (StringUtils.isEmpty(nick) || nick.length() > 20) {
				setNavigationMessage(req, NavigationMessage.warn("Impossibile registrare questo nick: Troppo lungo o troppo corto"));
				return "register.jsp";
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
	};

	/**
	 * Carica le frasi celebri dal database
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction getQuotes = new GiamboAction("getQuotes", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			AuthorDTO loggedUser = (AuthorDTO)req.getSession().getAttribute(MainServlet.LOGGED_USER_SESSION_ATTR);
			if (loggedUser == null || !loggedUser.isValid()) {
				setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
				return loginAction.action(req,  res);
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
	};

	/**
	 * Update di una frase celebre
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction updateQuote = new GiamboAction("updateQuote", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			AuthorDTO loggedUser = (AuthorDTO)req.getSession().getAttribute(MainServlet.LOGGED_USER_SESSION_ATTR);
			if (loggedUser == null || !loggedUser.isValid()) {
				setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
				return loginAction.action(req,  res);
			}

			Long quoteId = Long.parseLong(req.getParameter("quoteId"));
			String content = req.getParameter("quote_" + quoteId);
			if (StringUtils.isEmpty(content) || content.length() < 3 || content.length() > 100) {
				setNavigationMessage(req, NavigationMessage.warn("Minimo 3 caratteri, massimo 100"));
				return getQuotes.action(req, res);
			}

			QuoteDTO quote = new QuoteDTO();
			quote.setContent(content);
			quote.setId(quoteId);
			quote.setNick(loggedUser.getNick());

			getPersistence().insertUpdateQuote(quote);
			return getQuotes.action(req, res);
		}
	};

	/**
	 * Cancella una quote
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction removeQuote = new GiamboAction("removeQuote", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			AuthorDTO loggedUser = (AuthorDTO)req.getSession().getAttribute(MainServlet.LOGGED_USER_SESSION_ATTR);
			if (loggedUser == null || !loggedUser.isValid()) {
				setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
				return loginAction.action(req,  res);
			}
			
			Long quoteId = Long.parseLong(req.getParameter("quoteId"));
			QuoteDTO quote = new QuoteDTO();
			quote.setNick(loggedUser.getNick());
			quote.setId(quoteId);

			getPersistence().removeQuote(quote);
			return getQuotes.action(req, res);
		}
	};

	/**
	 * Tutte le informazioni dell'utente
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	protected GiamboAction getUserInfo =  new GiamboAction("getUserInfo", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			String nick = req.getParameter("nick");
			AuthorDTO author = getPersistence().getAuthor(nick);
			req.setAttribute("author", author);
			req.setAttribute("quotes", getPersistence().getQuotes(author));
			return "userInfo.jsp";
		}
	};
	
	/**
	 * Cambia le preference per mostrare/nascondere le immagini di ANOninmo
	 */
	protected GiamboAction updatePreferences =  new GiamboAction("updatePreferences", ONPOST|ONGET) {
		public String action(HttpServletRequest req, HttpServletResponse res) throws Exception {
			AuthorDTO loggedUser = (AuthorDTO)req.getSession().getAttribute(MainServlet.LOGGED_USER_SESSION_ATTR);
			if (loggedUser == null || !loggedUser.isValid()) {
				setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
				return loginAction.action(req,  res);
			}
			
			// setta le preferences
			for (String key : new String[] {PREF_SHOWANONIMG, PREF_EMBEDDYT}) {
				String value = req.getParameter(key);
				if (StringUtils.isNotEmpty(value)) {
					loggedUser.setPreferences(getPersistence().setPreference(loggedUser, key, "checked"));
				} else {
					loggedUser.setPreferences(getPersistence().setPreference(loggedUser, key, ""));
				}
				
			}
			
			return "user.jsp";
		}
	};

}
