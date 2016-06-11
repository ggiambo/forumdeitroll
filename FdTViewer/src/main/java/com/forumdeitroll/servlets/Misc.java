package com.forumdeitroll.servlets;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.DAOFactory;
import com.github.bingoohuang.patchca.background.BackgroundFactory;
import com.github.bingoohuang.patchca.custom.ConfigurableCaptchaService;
import com.github.bingoohuang.patchca.filter.AbstractFilterFactory;
import com.github.bingoohuang.patchca.filter.FilterFactory;
import com.github.bingoohuang.patchca.filter.predefined.CurvesRippleFilterFactory;
import com.github.bingoohuang.patchca.filter.predefined.DoubleRippleFilterFactory;
import com.github.bingoohuang.patchca.utils.encoder.EncoderHelper;
import com.github.bingoohuang.patchca.word.RandomWordFactory;

/**
 * Servlet "speciale" che non necessita di tutto l'ambaradan di MainFilter e MainServlet
 *
 */
public class Misc extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(Misc.class);

	private byte[] notAuthenticated;
	private byte[] noAvatar;

	private static ConfigurableCaptchaService captchaService;

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

			captchaService = new ConfigurableCaptchaService();
			captchaService.setHeight(50);
			captchaService.setWidth(150);
			BackgroundFactory gradientColorBackgroundFactory = new BackgroundFactory() {
				@Override
					public void fillBackground(BufferedImage dest) {
						GradientPaint gp = new GradientPaint(0, 0, Color.MAGENTA, dest.getWidth(), 0, Color.CYAN);

						Graphics2D g = dest.createGraphics();
						g.setPaint(gp);
						g.fillRect(0, 0, dest.getWidth(), dest.getHeight());
					}
			};
			captchaService.setBackgroundFactory(gradientColorBackgroundFactory);
			RandomWordFactory wordFactory = new RandomWordFactory();
			wordFactory.setMaxLength(6);
			wordFactory.setMinLength(6);
			captchaService.setWordFactory(wordFactory);

		} catch (IOException e) {
			LOG.error(e);
			throw new ServletException("Cannot read default images", e);
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

	private void redirectTo(HttpServletRequest req, HttpServletResponse res) throws IOException {
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
	private void getAvatar(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String nick = req.getParameter("nick");
		res.setHeader("Cache-Control", "max-age=3600");
		AuthorDTO author = DAOFactory.getAuthorsDAO().getAuthor(nick);
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

	private void getUserSignatureImage(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String nick = req.getParameter("nick");
		res.setHeader("Cache-Control", "max-age=3600");
		AuthorDTO author = DAOFactory.getAuthorsDAO().getAuthor(nick);
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
	private void getCaptcha(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setHeader("Cache-Control", "no-store");
		res.setHeader("Pragma", "no-cache");
		res.setDateHeader("Expires", 0);
		res.setContentType("image/jpeg");
		String answer = EncoderHelper.getChallangeAndWriteImage(captchaService, "png", res.getOutputStream());
		req.getSession().setAttribute("captcha", answer);
	}


	/**
	 * Cancella l'utente loggato dalla sessione
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	private void logoutAction(HttpServletRequest req, HttpServletResponse res) {
		boolean mobileView = MainServlet.isMobileView(req);
		req.getSession().invalidate();
		if (mobileView) {
			req.getSession().setAttribute("mobileView", "true");
		}
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

		String endpoint = "http://forumdeitroll.com/motorino/search?q=" + URLEncoder.encode(search, "UTF-8");
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
		String endpoint = "http://www.telize.com/geoip/" + ip + "?callback=" + callback;
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

	public static void setCaptchaLevel(int captchaLevel) {

		RandomWordFactory randomWordFactory = new RandomWordFactory();
		randomWordFactory.setMaxLength(6);
		randomWordFactory.setMinLength(6);

		FilterFactory filterFactory;
		switch (captchaLevel) {
			case 1:
				randomWordFactory.setCharacters("1234567890");
				filterFactory = new AbstractFilterFactory() {
					@Override
					protected List<BufferedImageOp> getFilters() {
						return Collections.EMPTY_LIST;
					}
				};
				break;
			default:
			case 2:
				filterFactory = new DoubleRippleFilterFactory();
				randomWordFactory.setCharacters("1234567890");
				break;
			case 3:
				filterFactory = new CurvesRippleFilterFactory(captchaService.getColorFactory());
				break;
		}

		captchaService.setWordFactory(randomWordFactory);
		captchaService.setFilterFactory(filterFactory);
	}

}
