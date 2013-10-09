package com.forumdeitroll.servlets;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.forumdeitroll.markup.InputSanitizer;
import com.forumdeitroll.markup.RenderOptions;
import com.forumdeitroll.markup.Renderer;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.profiler.UserProfile;
import com.forumdeitroll.profiler.UserProfiler;
import com.forumdeitroll.servlets.Action.Method;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Minichat extends MainServlet {

	private static final long serialVersionUID = -5851412573386020328L;

	public static int MAX_MESSAGE_SIZE = 200;
	public static int MAX_MESSAGE_NUMBER = 20;

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
		String content = req.getParameter("content");
		content = StringUtils.abbreviate(content, MAX_MESSAGE_SIZE);
		StringReader in = new StringReader(InputSanitizer.sanitizeText(content));
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
	
	@Action(method=Method.POST)
	String check(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO author = login(req);
		if (author == null || !author.isValid() || author.isBanned()) {
			return null;
		}
		Date lastCheck = new Date(Long.parseLong(req.getParameter("lastCheck")));
		boolean inMessageToRead = false;
		boolean messageToRead = false;
		for (Message message : messages) {
			if (message.content.toLowerCase().contains(author.getNick().toLowerCase())) {
				if (message.when.after(lastCheck)) {
					inMessageToRead = true;
					break;
				}
			} else if (message.when.after(lastCheck)) {
				messageToRead = true;
			}
		}
		res.setContentType("application/json");
		res.getWriter().println("{ \"inMessageToRead\" : "+inMessageToRead+", \"messageToRead\" : "+messageToRead+" }");
		res.getWriter().flush();
		return null;
	}
	
	private static class DateSerializer implements JsonSerializer<Date> {
		@Override
		public JsonElement serialize(Date arg0, Type arg1, JsonSerializationContext arg2) {
			return new Gson().toJsonTree(new SimpleDateFormat("HH:mm").format(arg0));
		}
	}
	
	@Action(method=Method.POST)
	String refresh(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Date lastCheck = new Date(Long.parseLong(req.getParameter("lastCheck")));
		LinkedList<Message> new_messages = new LinkedList<Message>();
		for (Message message : messages) {
			if (message.when.after(lastCheck)) {
				new_messages.add(message);
			}
		}
		res.setContentType("application/json");
		GsonBuilder b = new GsonBuilder();
		b.registerTypeAdapter(Date.class, new DateSerializer());
		res.getWriter().println(b.create().toJson(new_messages));
		res.getWriter().flush();
		return null;
	}
}
