package com.acmetoy.ravanator.fdt.servlets;

import java.awt.Color;
import java.io.Writer;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.gimpy.RippleGimpyRenderer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.producer.NumbersAnswerProducer;

import org.apache.commons.lang3.StringUtils;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;

public class Messages extends MainServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Pattern PATTERN_QUOTE = Pattern.compile("<BR>(&gt;\\ ?)*");
	
	private static final Map<String, String> EMO_MAP = new HashMap<String, String>();	
	static {
		EMO_MAP.put("1", " :)");
		EMO_MAP.put("2", " :D");
		EMO_MAP.put("3", " ;)");
		EMO_MAP.put("4", " :o");
		EMO_MAP.put("5", " :p");
		EMO_MAP.put("6", " :\\");
		EMO_MAP.put("7", " :@");
		EMO_MAP.put("8", " :s");
		EMO_MAP.put("9", " :$");
		EMO_MAP.put("10", " :(");
		EMO_MAP.put("11", ":'(");
		EMO_MAP.put("12", " :|");
		EMO_MAP.put("13", " 8)");
		EMO_MAP.put("angelo", " O)");
		EMO_MAP.put("anonimo", "(anonimo)");
		EMO_MAP.put("diavoletto", " @^");
		EMO_MAP.put("fantasmino", "(ghost)");
		EMO_MAP.put("geek", "(geek)");
		EMO_MAP.put("idea", "(idea)");
		EMO_MAP.put("love", "(love)");
		EMO_MAP.put("loveamiga", "(amiga)");
		EMO_MAP.put("loveapple", "(apple)");
		EMO_MAP.put("loveatari", "(atari)");
		EMO_MAP.put("lovec64", "(c64)");
		EMO_MAP.put("lovelinux", "(linux)");
		EMO_MAP.put("lovewin", "(win)");
		EMO_MAP.put("newbie", "(newbie)");
		EMO_MAP.put("noia3", " :-o");
		EMO_MAP.put("nolove", "(nolove)");
		EMO_MAP.put("pirata", " p)");
		EMO_MAP.put("robot", "(cylon)");
		EMO_MAP.put("rotfl", "(rotfl)");
		EMO_MAP.put("troll1", "(troll1)");
		EMO_MAP.put("troll2", "(troll2)");
		EMO_MAP.put("troll3", "(troll3)");
		EMO_MAP.put("troll4", "(troll4)");
		EMO_MAP.put("troll", "(troll)");
	}
	
	@Override
	public String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return getByPage(req, res);
	}

	/**
	 * I messaggi di questa pagina (Dimensione PAGE_SIZE) in ordine di data
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String getByPage(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setAttribute("messages", getPersistence().getMessagesByDate(PAGE_SIZE, getPageNr(req)));
		setNavigationMessage(req, "Ordinati cronologicamente");
		return "messages.jsp";
	}

	/**
	 * I messaggi di questo autore in ordine di data
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String getByAuthor(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String author = req.getParameter("author");
		req.setAttribute("specificParams", "&author=" + author);
		setNavigationMessage(req, "Messaggi scritti da <i>" + author + "</i>");
		req.setAttribute("messages", getPersistence().getMessagesByAuthor(author, PAGE_SIZE, getPageNr(req)));
		return "messages.jsp";
	}

	public String getByForum(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String forum = req.getParameter("forum");
		req.setAttribute("specificParams", "&forum=" + forum);
		setNavigationMessage(req, "Forum <i>" + forum + "</i>");
		req.setAttribute("messages", getPersistence().getMessagesByForum(forum, PAGE_SIZE, getPageNr(req)));
		return "messages.jsp";
	}

	/**
	 * Ricerca in tutti i messaggi
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String search(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String search = req.getParameter("search");
		req.setAttribute("specificParams", "&search=" + search);
		req.setAttribute("messages", getPersistence().searchMessages(search, PAGE_SIZE, getPageNr(req)));
		return "messages.jsp";
	}

	/**
	 * Popola il div per la risposta/quota messaggio
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String newMessage(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String forum = req.getParameter("forum");
		req.setAttribute("forum", forum);
		req.setAttribute("parentId", -1);
		setNavigationMessage(req, forum);
		// faccine - ordinate per key
		TreeMap<String, String> emoMap = new TreeMap<String, String>(EMO_MAP);
		req.setAttribute("emoMap", emoMap);
		return "newMessage.jsp";
	}

	/**
	 * Popola il div per la risposta/quota messaggio
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String showReplyDiv(HttpServletRequest req, HttpServletResponse res) throws Exception {
		res.setCharacterEncoding("UTF-8");
		String type = req.getParameter("type");
		String parentId = req.getParameter("parentId");
		req.setAttribute("parentId", parentId);
		if ("quote".equals(type)) {
			MessageDTO msgDTO = getPersistence().getMessage(Long.parseLong(parentId));
			String text = msgDTO.getText().trim();

			// quote
			Matcher m = PATTERN_QUOTE.matcher(text);
			while (m.find()) {
				String group = m.group(0);
				int nrQuotes = group.replace(" ", "").replace("<BR>", "").length() / 4; // 4 = "&gt;".length();
				nrQuotes++;
				StringBuilder newBegin = new StringBuilder("\r\n");
				for (int j = 0; j < nrQuotes; j++) {
					newBegin.append("> ");
				}
				text = m.replaceFirst(newBegin.toString());
				m = PATTERN_QUOTE.matcher(text);
			}

			String author = msgDTO.getAuthor();
			text = "\r\nScritto da: " + (author != null ? author : "") + "\r\n>" + text;
			msgDTO.setText(text);
			req.setAttribute("message", msgDTO);
		}

		// faccine - ordinate per key
		TreeMap<String, String> emoMap = new TreeMap<String, String>(EMO_MAP);
		req.setAttribute("emoMap", emoMap);

		return "incReplyMessage.jsp";
	}

	/**
	 * Inserisce un nuovo messaggio
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public String insertMessage(HttpServletRequest req, HttpServletResponse res) throws Exception {


		// check username and pass
		String nick = req.getParameter("nick");
		nick = StringUtils.isEmpty(nick) ? null : nick.trim();
		String pass = req.getParameter("pass");
		pass = StringUtils.isEmpty(pass) ? null : pass.trim();
		AuthorDTO author = null;
		if (nick != null && pass != null) {
			author = getPersistence().getAuthor(nick, md5(pass));
			if (author == null) {
				res.setContentType("text/plain");
				res.setStatus(500);
				Writer w = res.getWriter();
				w.write("Username o password non corretti");
				w.flush();
				w.close();
				return null;
			}
			author.setMessages(author.getMessages() + 1);
		}

		// se non autenticato, richiedi captcha
		if (author == null) {
			String captcha = req.getParameter("captcha");
			String correctAnswer = (String)req.getSession().getAttribute("captcha");
			if ((correctAnswer == null) || !correctAnswer.equals(captcha)) {
				res.setContentType("text/plain");
				res.setStatus(500);
				Writer w = res.getWriter();
				w.write("No no no ! Il captcha non e' corretto !");
				w.flush();
				w.close();
				return null;
			}
		}

		String text = req.getParameter("text");

		// replace dei caratteri HTML
		text = text.replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\n", "<BR>");

		// restore <i>, <b>, <u> e <s>
		for (String t : new String[] {"i", "b", "u", "s"}) {
			text = text.replaceAll("(?i)&lt;" + t + "&gt;", "<" + t + ">");
			text = text.replaceAll("(?i)&lt;/" + t + "&gt;", "</" + t + ">");
		}

		// evita inject in img
		Pattern p = Pattern.compile("\\[img\\]((.*?)\"(.*?))\\[/img\\]");
		Matcher m = p.matcher(text);
		while (m.find()) {
			String replace =  m.group(1).replaceAll("\"", "");
			text = m.replaceFirst(Matcher.quoteReplacement("[img]" + replace + "[/img]"));
			 m = p.matcher(text);
		}

		// testo di almeno di 10 caratteri ...
		if (StringUtils.isEmpty(text) || text.length() < 10) {
			res.setContentType("text/plain");
			res.setStatus(500);
			Writer w = res.getWriter();
			w.write("Un po di fantasia, scrivi almeno 10 caratteri ...");
			w.flush();
			w.close();
			return null;
		}

		// testo al massimo di 5000 caratteri ...
		if (text.length() > 5000) {
			res.setContentType("text/plain");
			res.setStatus(500);
			Writer w = res.getWriter();
			w.write("Sei piu' logorroico di una Wakka, stai sotto i 5000 caratteri !");
			w.flush();
			w.close();
			return null;
		}

		// reply o messaggio nuovo ?
		MessageDTO msgOut = getPersistence().getMessage(Long.parseLong(req.getParameter("parentId")));
		String forum;
		String subject;
		if (msgOut.getId() == -1) {
			forum = req.getParameter("forum");
			subject = req.getParameter("subject");
		} else {
			forum = msgOut.getForum();
			subject = msgOut.getSubject();
		}

		// subject almeno di 5 caratteri, cribbio !
		if (StringUtils.isEmpty(subject) || subject.trim().length() < 5) {
			res.setContentType("text/plain");
			res.setStatus(500);
			Writer w = res.getWriter();
			w.write("Subject almeno di 5 caratteri, cribbio !");
			w.flush();
			w.close();
			return null;
		}

		MessageDTO msg = new MessageDTO();
		msg.setAuthor(author != null ? nick : null);
		msg.setDate(new Date());
		msg.setForum(StringUtils.isEmpty(forum) ? null : forum);
		msg.setParentId(msgOut.getId());
		msg.setSubject(subject);
		msg.setText(text);
		msg.setThreadId(msgOut.getThreadId());

		// incrementa il numero di messaggi scritti
		if (author != null) {
			getPersistence().updateAuthor(author);
		}

		long msgId = getPersistence().insertMessage(msg);
		if (msgOut.getId() == -1) {
			msg.setThreadId(msgId);
		}

		// redirect
		res.setContentType("text/plain");
		Writer out = res.getWriter();
		out.write("/Threads?action=getByThread&threadId=" + msg.getThreadId() + "&random=" + Math.random() + "#msg" + msgId);
		out.flush();
		out.close();

		return null;
	}

	public String getCaptcha(HttpServletRequest req, HttpServletResponse res) throws Exception {
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

	public static Map<String, String> getEmoMap() {
		return new HashMap<String, String>(EMO_MAP);
	}

	private String md5(String input) throws NoSuchAlgorithmException {
		String result = input;
		if (input != null) {
			MessageDigest md = MessageDigest.getInstance("MD5"); // or "SHA-1"
			md.update(input.getBytes());
			BigInteger hash = new BigInteger(1, md.digest());
			result = hash.toString(16);
			while (result.length() < 32) {
				result = "0" + result;
			}
		}
		return result;
	}

}