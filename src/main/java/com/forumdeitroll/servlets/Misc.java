package com.forumdeitroll.servlets;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.DAOFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Servlet "speciale" che non necessita di tutto l'ambaradan di MainFilter e MainServlet
 *
 */
public class Misc extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(Misc.class);

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
			int count;
			while ((count = is.read(buffer)) != -1) {
				bos.write(buffer, 0, count);
			}
			notAuthenticated = bos.toByteArray();

			// default
			is = config.getServletContext().getResourceAsStream("/images/avatardefault.gif");
			bos = new ByteArrayOutputStream();
			buffer = new byte[1024];
			while ((count = is.read(buffer)) != -1) {
				bos.write(buffer, 0, count);
			}
			noAvatar = bos.toByteArray();

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
	 * Cancella l'utente loggato dalla sessione
	 */
	private void logoutAction(HttpServletRequest req, HttpServletResponse res) {
		boolean mobileView = MainServlet.isMobileView(req);
		req.getSession().invalidate();
		if (mobileView) {
			req.getSession().setAttribute("mobileView", "true");
		}
		res.setStatus(302);
		res.setContentType("text/html");
		res.setHeader("Location", "Messages");
	}

	private void getDisclaimer(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/pages/disclaimer.jsp").forward(req, res);
	}

	/**
	 * Metodo di utilità per aggirare la same origin policy quando si testa in locale il motorino
	 * wrappando le chiamate
	 */
	private void searchAjax(HttpServletRequest req, HttpServletResponse res) throws IOException {
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
	 */
	private void freegeoip(HttpServletRequest req, HttpServletResponse res) throws IOException {
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

}
