package com.forumdeitroll.servlets;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.gimpy.RippleGimpyRenderer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.producer.NumbersAnswerProducer;

import org.apache.log4j.Logger;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.IPersistence;
import com.forumdeitroll.persistence.PersistenceFactory;

/**
 * Servlet "speciale" che non necessita di tutto l'ambaradan di MainFilter e MainServlet
 *
 */
public class Misc extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(Misc.class);
	
	private IPersistence persistence;
	
	private byte[] notAuthenticated;
	private byte[] noAvatar;
	
	/**
	 * Inizializza le immagini per non autenticato o autenticato senza avatar
	 */
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
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String action = req.getParameter("action");
		if ("getAvatar".equals(action)) {
			getAvatar(req, res);
		} else if ("getCaptcha".equals(action)) {
			getCaptcha(req, res);
		} else if ("logoutAction".equals(action)) {
			logoutAction(req, res);
		} else {
			LOG.error("action '" + action + "' conosciuta");
		}
	}
	
	/**
	 * Scrive direttamente nella response i bytes che compongono l'avatar
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	private void getAvatar(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String nick = req.getParameter("nick");
		res.setHeader("Cache-Control", "max-age=3600");
		AuthorDTO author = persistence.getAuthor(nick);
		ServletOutputStream out = res.getOutputStream();
		if (author.isValid()) {
			if (author.getAvatar() != null) {
				out.write(author.getAvatar());
			} else {
				out.write(noAvatar);
			}
		} else {
			out.write(notAuthenticated);
		}
		out.flush();
		out.close();
	}

	/**
	 * Genera un captcha
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	private void getCaptcha(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
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
	}
	

	/**
	 * Cancella l'utente loggato dalla sessione
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	private void logoutAction(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.removeAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		req.getSession().removeAttribute(MainServlet.LOGGED_USER_SESS_ATTR);
		req.getSession().invalidate();
		res.setStatus(302);
		res.setContentType("text/html");
		res.setHeader("Location", "Messages?disclaimer=OK");
	}

}
