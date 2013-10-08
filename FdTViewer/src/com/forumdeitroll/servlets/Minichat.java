package com.forumdeitroll.servlets;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.forumdeitroll.markup.InputSanitizer;
import com.forumdeitroll.markup.RenderOptions;
import com.forumdeitroll.markup.Renderer;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.profiler.UserProfile;
import com.forumdeitroll.profiler.UserProfiler;
import com.forumdeitroll.servlets.Action.Method;
import com.google.gson.Gson;

public class Minichat extends MainServlet {

	private static final long serialVersionUID = -5851412573386020328L;

	public static int MAX_MESSAGE_SIZE = 200;
	public static int MAX_MESSAGE_NUMBER = 10;

	public static class Message {
		private String author, content;
		private Date when;

		public void setAuthor(String author) {
			this.author = author;
		}

		public String getAuthor() {
			return author;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getContent() {
			return content;
		}

		public void setWhen(Date when) {
			this.when = when;
		}

		public Date getWhen() {
			return when;
		}
	}

	private static LinkedList<Message> messages = new LinkedList<Message>();

	@Override
	@Action(method=Method.GET)
	String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setAttribute("messages", messages);
		getServletContext().getRequestDispatcher("/pages/minichat/minichat.jsp").forward(req, res);
		return null;
	}
	
	private static RenderOptions opts = new RenderOptions() {{
			authorIsAnonymous = false;
			buffersize = MAX_MESSAGE_SIZE;
			collapseQuotes = false;
			embedYoutube = false;
			renderImages = false;
	}};
	
	@Action(method=Method.POST)
	String send(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// legit request?
		AuthorDTO author = login(req);
		if (author == null || !author.isValid() || author.isBanned()) {
			return null;
		}
		UserProfile candidate = new Gson().fromJson(req.getParameter("jsonProfileData"), UserProfile.class);
		candidate.setIpAddress(req.getHeader("X-Forwarded-For") != null ? req.getHeader("X-Forwarded-For") : req.getRemoteAddr());
		candidate.setNick(author.getNick());
		UserProfile profile = UserProfiler.getInstance().guess(candidate);
		if (profile.isBannato()) {
			return null;
		}
		// pre-render html
		StringReader in = new StringReader(InputSanitizer.sanitizeText(req.getParameter("content")));
		StringWriter out = new StringWriter();
		Renderer.render(in, out, opts);

		Message message = new Message();
		message.author = author.getNick();
		message.content = out.getBuffer().toString();
		message.when = new Date();
		messages.add(message);
		if (messages.size() > MAX_MESSAGE_NUMBER)
			messages.remove();
		res.setContentType("application/json");
		res.getWriter().println("{ \"status\" : \"OK\" }");
		res.getWriter().flush();
		return null;
	}
}
