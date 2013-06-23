package com.forumdeitroll.servlets;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

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

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
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
		} else if ("getDisclaimer".equals(action)) {
			getDisclaimer(req, res);
		} else if ("redirectTo".equals(action)) {
			redirectTo(req, res);
		} else if ("getUserSignatureImage".equals(action)) {
			getUserSignatureImage(req, res);
		} else if ("searchAjax".equals(action)) {
			searchAjax(req, res);
		} else if ("freegeoip".equals(action)) {
			freegeoip(req, res);
		} else {
			LOG.error("action '" + action + "' conosciuta");
		}
	}
	
	private void redirectTo(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String url = req.getParameter("url");
		url = StringEscapeUtils.unescapeHtml4(url);
		url = url.replaceAll("&apos;", "'");
		res.setHeader("Location", url);
		res.sendError(302);
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
	
	private void getUserSignatureImage(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String nick = req.getParameter("nick");
		res.setHeader("Cache-Control", "max-age=3600");
		AuthorDTO author = persistence.getAuthor(nick);
		ServletOutputStream out = res.getOutputStream();
		out.write(author.getSignatureImage());
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
		req.getSession().invalidate();
		res.setStatus(302);
		res.setContentType("text/html");
		res.setHeader("Location", "Messages?disclaimer=OK");
	}
	
	private void getDisclaimer(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String originalURL =
			req.getHeader("Referer") != null
				? req.getHeader("Referer")
				: req.getHeader("Referrer") != null
					? req.getHeader("Referrer")
					: null;
		if (StringUtils.isEmpty(originalURL)) {
			originalURL = "Messages";
		}
		req.setAttribute("originalURL", originalURL);
		getServletContext().getRequestDispatcher("/pages/disclaimer.jsp").forward(req, res);
	}
	
	/**
	 * Metodo di utilità per aggirare la same origin policy quando si testa in locale il motorino
	 * wrappando le chiamate
	 * 
	 * @param req
	 * @param res
	 * @throws ServletException
	 * @throws IOException
	 */
	private void searchAjax(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String search = req.getParameter("q");
		String sort = req.getParameter("sort");
		String page = req.getParameter("p");
		
		String endpoint = "http://forumdeitroll.com/motorino/search?q=" + URLEncoder.encode(search);
		if (!StringUtils.isEmpty(sort)) {
			endpoint += "&sort=" + sort; //date,rdate,rank
		}
		if (!StringUtils.isEmpty(page)) {
			endpoint += "&p=" + page;
		}
		
		System.out.println(endpoint);
		InputStream in = new URL(endpoint).openStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[512];
		int count;
		while ((count = in.read(buffer)) != -1) {
			out.write(buffer, 0, count);
		}
		in.close();
		
		res.setContentType("application/json");
		res.getOutputStream().write(out.toByteArray());
	}
	
	/**
	 * wrapper del servizio di freegeoip, per aggirare la same-origin policy a partire da firefox 22/23 
	 * http://freegeoip.net/json/{ip}
	 * @param req
	 * @param res
	 * @throws ServletException
	 * @throws IOException
	 */
	private void freegeoip(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String ip = req.getParameter("ip");
		String callback = req.getParameter("callback");
		String endpoint = "http://freegeoip.net/json/" + ip + "?callback=" + callback;
		InputStream in = new URL(endpoint).openStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[512];
		int count;
		while ((count = in.read(buffer)) != -1) {
			out.write(buffer, 0, count);
		}
		in.close();
		res.setContentType("text/javascript");
		res.getOutputStream().write(out.toByteArray());
	}
}
