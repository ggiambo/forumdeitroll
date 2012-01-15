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

	@Override
	public String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO author = login(req);
		setWebsiteTitle(req, "Forum dei troll");
		if (author != null && author.isValid()) {
			req.setAttribute("author", author);
			return "user.jsp";
		}
		setNavigationMessage(req, "Passuord ezzere sbaliata !");
		return loginAction(req,  res);
	}

	/**
	 * Mostra la pagina di login
	 * @param req
	 * @param res
	 * @return
	 */
	public String loginAction(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return "login.jsp";
	}

	/**
	 * Update della password
	 * @param req
	 * @param res
	 * @return
	 */
	public String updatePass(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO author = login(req);
		if (author == null || !author.isValid()) {
			setNavigationMessage(req, "Passuord ezzere sbaliata !");
			return loginAction(req,  res);
		}
		req.setAttribute("author", author);

		// user loggato, check pass
		String actualPass = req.getParameter("actualPass");
		if (StringUtils.isEmpty(actualPass)) {
			setNavigationMessage(req, "Inserisci la password attuale");
			return "user.jsp";
		}

		if (!author.passwordIs(actualPass)) {
			setNavigationMessage(req, "Password attuale sbagliata, non fare il furmiga");
			return "user.jsp";
		}

		String pass1 = req.getParameter("pass1");
		String pass2 = req.getParameter("pass2");

		if (StringUtils.isEmpty(pass1) || StringUtils.isEmpty(pass2)) {
			setNavigationMessage(req, "Inserisci una password");
			return "user.jsp";
		}
		if (!pass1.equals(pass2)) {
			setNavigationMessage(req, "Le due password non sono uguali");
			return "user.jsp";
		}

		if (!getPersistence().updateAuthorPassword(author, pass1)) {
			setNavigationMessage(req, "Errore in User.updatePass / updateAuthorPassword -- molto probabilmente e` colpa di sarrusofono, faglielo sapere -- sempre ammesso che tu riesca a postare sul forum a questo punto :(");
			return "user.jsp";
		}

		setNavigationMessage(req, "Password modificata con successo !");
		return "user.jsp";
	}

	/**
	 * Update avatar
	 * @param req
	 * @param res
	 * @return
	 */
	public String updateAvatar(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO author = login(req);
		if (author == null || !author.isValid()) {
			setNavigationMessage(req, "Passuord ezzere sbaliata !");
			return loginAction(req,  res);
		}
		req.setAttribute("author", author);
		if (!ServletFileUpload.isMultipartContent(req)) {
			setNavigationMessage(req, "Nessun avatar caricato");
			return "user.jsp";
		}

		// piglia l'immagine dal multipart request
		DiskFileItemFactory  fileItemFactory = new DiskFileItemFactory ();
		fileItemFactory.setSizeThreshold(MAX_SIZE_AVATAR_BYTES); // grandezza massima 512bytes
		fileItemFactory.setRepository(new File(System.getProperty("java.io.tmpdir")));
		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		Iterator<FileItem> it = uploadHandler.parseRequest(req).iterator();
		if (it.hasNext()) {
			FileItem avatar = it.next();
			if (avatar.getSize() > MAX_SIZE_AVATAR_BYTES) {
				setNavigationMessage(req, "Megalomane, avatar troppo grande, al massimo 512K !");
				return "user.jsp";
			}
			// carica l'immagine
			BufferedImage image = ImageIO.read(avatar.getInputStream());
			int w = image.getWidth();
			int h = image.getHeight();
			if (w > MAX_SIZE_AVATAR_WIDTH || h > MAX_SIZE_AVATAR_HEIGHT) {
				setNavigationMessage(req, "Dimensione massima consentita: 100x100px");
				return "user.jsp";
			}
			// modifica author
			author.setAvatar(avatar.get());
			getPersistence().updateAuthor(author);
		} else {
			setNavigationMessage(req, "Nessun Avatar ?");
			return "user.jsp";
		}

		// fuck yeah 8) !
		setNavigationMessage(req, "Avatar modificato con successo !");
		return "user.jsp";
	}

	/**
	 * Pagina per registrare un nuovo user
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String registerAction(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.getSession().removeAttribute(LOGGED_USER_SESSION_ATTR);
		return "register.jsp";
	}

	/**
	 * Registra nuovo user
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String registerNewUser(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String nick = req.getParameter("nick");
		req.setAttribute("nick", nick);
		// check del captcha
		String captcha = req.getParameter("captcha");
		String correctAnswer = (String)req.getSession().getAttribute("captcha");
		if ((correctAnswer == null) || !correctAnswer.equals(captcha)) {
			setNavigationMessage(req, "Captcha non corretto");
			return "register.jsp";
		}
		// registra il nick
		if (StringUtils.isEmpty(nick) || nick.length() > 20) {
			setNavigationMessage(req, "Impossibile registrare questo nick: Troppo lungo o troppo corto");
			return "register.jsp";
		}
		String pass = req.getParameter("pass");
		if (StringUtils.isEmpty(pass) || pass.length() > 20) {
			setNavigationMessage(req, "Scegli una password migliore, giovane jedi ...");
			return "register.jsp";
		}
		AuthorDTO author = getPersistence().registerUser(nick, pass);
		if (!author.isValid()) {
			setNavigationMessage(req, "Impossibile registrare questo nick, probabilmente gia' esiste");
			return "register.jsp";
		}
		// login
		login(req);
		req.setAttribute("author", author);
		return "user.jsp";
	}

	/**
	 * Carica le frasi celebri dal database
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String getQuotes(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO author = login(req);
		if (author == null || !author.isValid()) {
			setNavigationMessage(req, "Passuord ezzere sbaliata !");
			return loginAction(req,  res);
		}
		req.setAttribute("author", author);
		List<QuoteDTO> list = getPersistence().getQuotes(author);
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
	public String updateQuote(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO author = login(req);
		if (author == null || !author.isValid()) {
			setNavigationMessage(req, "Passuord ezzere sbaliata !");
			return loginAction(req,  res);
		}
		req.setAttribute("author", author);

		Long quoteId = Long.parseLong(req.getParameter("quoteId"));
		String content = req.getParameter("quote_" + quoteId);
		if (StringUtils.isEmpty(content) || content.length() < 3 || content.length() > 100) {
			setNavigationMessage(req, "Minimo 3 caratteri, massimo 100");
			return getQuotes(req, res);
		}

		QuoteDTO quote = new QuoteDTO();
		quote.setContent(content);
		quote.setId(quoteId);
		quote.setNick(author.getNick());

		getPersistence().insertUpdateQuote(quote);
		return getQuotes(req, res);
	}

	/**
	 * Cancella una quote
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String removeQuote(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO author = login(req);
		if (author == null || !author.isValid()) {
			setNavigationMessage(req, "Passuord ezzere sbaliata !");
			return loginAction(req,  res);
		}
		req.setAttribute("author", author);

		Long quoteId = Long.parseLong(req.getParameter("quoteId"));
		QuoteDTO quote = new QuoteDTO();
		quote.setNick(author.getNick());
		quote.setId(quoteId);

		getPersistence().removeQuote(quote);
		return getQuotes(req, res);
	}

	/**
	 * Lista di tutti i PVT
	 */
	public String getPrivateMessages(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO author = login(req);
		if (author == null || !author.isValid()) {
			setNavigationMessage(req, "Passuord ezzere sbaliata !");
			return loginAction(req,  res);
		}
		req.setAttribute("author", author);

		int pageNr = Integer.parseInt(req.getParameter("pageNr"));

		req.setAttribute("privateMessages", getPersistence().getPrivateMessages(author, 15, pageNr));
		return "privateMessages.jsp";

	}

}
