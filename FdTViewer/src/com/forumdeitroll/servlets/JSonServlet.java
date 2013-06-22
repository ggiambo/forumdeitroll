package com.forumdeitroll.servlets;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import sun.misc.BASE64Encoder;

import com.forumdeitroll.PasswordUtils;
import com.forumdeitroll.SingleValueCache;
import com.forumdeitroll.markup.Emoticon;
import com.forumdeitroll.markup.Emoticons;
import com.forumdeitroll.markup.InputSanitizer;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.IPersistence;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.MessagesDTO;
import com.forumdeitroll.persistence.PersistenceFactory;
import com.forumdeitroll.persistence.QuoteDTO;
import com.forumdeitroll.persistence.ThreadDTO;
import com.forumdeitroll.persistence.ThreadsDTO;
import com.google.gson.stream.JsonWriter;

public class JSonServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_PAGE_SIZE = 25;

	private static final int MAX_PAGE_SIZE = 100;

	private IPersistence persistence;
	
	private SingleValueCache<List<String>> cachedForums = new SingleValueCache<List<String>>(60 * 60 * 1000) {
		@Override protected List<String> update() {
			return persistence.getForums();
		}
	};
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			persistence = PersistenceFactory.getInstance();
		} catch (Exception e) {
			throw new ServletException("Cannot instantiate persistence", e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String action = req.getParameter("action");
		if (StringUtils.isEmpty(action)) {
			printUsage(req, res);
			return;
		}
		long time = System.currentTimeMillis();
		StringBuilderWriter writer = new StringBuilderWriter();
		try {
			Method m = this.getClass().getDeclaredMethod(action, StringBuilderWriter.class, Map.class, Long.TYPE);
			m.invoke(this, writer, Collections.unmodifiableMap(req.getParameterMap()), time);
		} catch (NoSuchMethodException e) {
			printUsage(req, res);
			return;
		} catch (InvocationTargetException e) {
			handleException(e.getTargetException(), writer, time);
		} catch (IllegalAccessException e) {
			handleException(e.getCause(), writer, time);
		} finally {
			// wrap with the callback
			String callback = req.getParameter("callback");
			if (callback != null) {
				writer.getBuilder().insert(0, callback + "(").append(")");
			}
			// flush and close
			writer.flush();
			writer.close();
		}
		// write in response
		res.setContentType("application/json; charset=UTF-8");
		res.getWriter().write(writer.getBuilder().toString());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}

	/**
	 * Ritorna una stringa JSON contenente tutti i forums
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getForums(StringBuilderWriter writer, Map<String, String[]> params, long time) throws IOException {

		JsonWriter out = initJsonWriter(ResultCode.OK, writer);

		List<String> forums = cachedForums.get();
		out.beginObject();
		out.name("forums");
		out.beginArray();
		for (String forum : forums) {
			out.value(forum);
		}
		out.endArray();
		out.endObject();

		closeJsonWriter(out, time);
	}

	/**
	 * Ritorna una stringa JSON contenente i threads ordinati temporalmente decrescentemente.
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getThreads(StringBuilderWriter writer, Map<String, String[]> params, long time) throws IOException {
		int page = getIntValue(params, "page", 0);
		int pageSize = getPageSize(params);
		String forum = getStringValue(params, "forum", null);

		ThreadsDTO result = persistence.getThreads(forum, pageSize, page, false);

		JsonWriter out = initJsonWriter(ResultCode.OK, writer);

		out.beginObject();
		out.name("page").value(page);
		out.name("pageSize").value(pageSize);
		out.name("forum").value(forum);
		out.name("resultSize").value(result.getMessages().size());
		out.name("totalSize").value(result.getMaxNrOfMessages());
		out.name("threads");

		out.beginArray();
		for (ThreadDTO threadDTO : result.getMessages()) {
			encodeThread(threadDTO, out);
		}
		out.endArray();

		out.endObject(); // "threads"

		closeJsonWriter(out, time);
	}

	/**
	 * Ritorna una stringa JSON contenente i messaggi ordinati temporalmente decrescentemente.
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getMessages(StringBuilderWriter writer, Map<String, String[]> params, long time) throws IOException {
		int page = getIntValue(params, "page", 0);
		int pageSize = getPageSize(params);
		String forum = getStringValue(params, "forum", null);

		String nick = getStringValue(params, "nick", null);
		MessagesDTO result = persistence.getMessages(forum, nick, pageSize, page, false);

		JsonWriter out = initJsonWriter(ResultCode.OK, writer);

		out.beginObject();
		out.name("page").value(page);
		out.name("pageSize").value(pageSize);
		out.name("forum").value(forum);
		out.name("resultSize").value(result.getMessages().size());
		out.name("totalSize").value(result.getMaxNrOfMessages());
		out.name("messages");

		out.beginArray();
		for (MessageDTO messageDTO : result.getMessages()) {
			encodeMessage(messageDTO, out);
		}
		out.endArray();

		out.endObject(); // "messages"

		closeJsonWriter(out, time);
	}

	/**
	 * Ritorna una stringa JSON contenente i messaggi di un singolo thread.
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getThread(StringBuilderWriter writer, Map<String, String[]> params, long time) throws IOException {
		long threadId = getLongValue(params, "threadId", -1);
		if (threadId == -1) {
			writeErrorMessage(writer, "Manca il parametro 'threadId'", time);
			return;
		}

		List<MessageDTO> result = persistence.getMessagesByThread(threadId);

		JsonWriter out = initJsonWriter(ResultCode.OK, writer);

		out.beginObject();
		out.name("threadId").value(threadId);
		out.name("resultSize").value(result.size());
		out.name("messages");

		out.beginArray();
		for (MessageDTO messageDTO : result) {
			encodeMessage(messageDTO, out);
		}
		out.endArray();

		out.endObject(); // "messages"

		closeJsonWriter(out, time);
	}

	/**
	 * Ritorna una stringa JSON contenente un singolo messaggio
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getMessage(StringBuilderWriter writer, Map<String, String[]> params, long time) throws IOException {
		long msgId = getLongValue(params, "msgId", -1);
		if (msgId == -1) {
			writeErrorMessage(writer, "Manca il parametro 'msgId'", time);
			return;
		}

		JsonWriter out = initJsonWriter(ResultCode.OK, writer);

		MessageDTO result = persistence.getMessage(msgId);
		encodeMessage(result, out);

		closeJsonWriter(out, time);
	}

	/**
	 * Ritorna una stringa JSON contenente l'autore
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getAuthor(StringBuilderWriter writer, Map<String, String[]> params, long time) throws IOException {
		String nick = getStringValue(params, "nick", null);
		if (nick == null) {
			writeErrorMessage(writer, "Manca il parametro  'nick'", time);
			return;
		}

		AuthorDTO result = persistence.getAuthor(nick);

		JsonWriter out = initJsonWriter(ResultCode.OK, writer);

		out.beginObject();
		encodeAuthor(result, out);
		out.endObject();

		closeJsonWriter(out, time);
	}

	/**
	 * Ritorna una stringa JSON contenente gli autori
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getAuthors(StringBuilderWriter writer, Map<String, String[]> params, long time) throws IOException {
		boolean onlyActive = false;
		String paramOnlyActive = getStringValue(params, "onlyActive", null);
		if (paramOnlyActive != null) {
			onlyActive = Boolean.parseBoolean(paramOnlyActive);
		}

		List<AuthorDTO> result = persistence.getAuthors(onlyActive);

		JsonWriter out = initJsonWriter(ResultCode.OK, writer);

		out.beginObject();
		out.name("resultSize").value(result.size());
		out.name("authors");

		out.beginArray();
		for (AuthorDTO authorDTO : result) {
			out.beginObject();
			encodeAuthor(authorDTO, out);
			out.endObject();
		}
		out.endArray();

		out.endObject(); // "authors"

		closeJsonWriter(out, time);
	}

	/**
	 * Ritorna una stringa JSON contenente le quotes dell'autore
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getQuotes(StringBuilderWriter writer, Map<String, String[]> params, long time) throws IOException {
		String nick = getStringValue(params, "nick", null);
		if (nick == null) {
			writeErrorMessage(writer, "Manca il parametro  'nick'", time);
			return;
		}

		AuthorDTO author = persistence.getAuthor(nick);
		List<QuoteDTO> result = persistence.getQuotes(author);

		JsonWriter out = initJsonWriter(ResultCode.OK, writer);

		out.beginObject();
		out.name("resultSize").value(result.size());
		out.name("quotes");
		out.beginArray();
		for (QuoteDTO quote : result) {
			out.beginObject();
			out.name("quote");
			out.beginObject();
			out.name("id").value(quote.getId());
			out.name("content").value(quote.getContent());
			out.endObject();
			out.endObject();
		}
		out.endArray();

		out.endObject(); // "quotes"

		closeJsonWriter(out, time);
	}

	/**
	 * Ritorna una stringa JSON contenente tutte le emoticons
	 * @param out
	 * @param params
	 * @throws IOException
	 */
	protected void getEmos(StringBuilderWriter writer, Map<String, String[]> params, long time) throws IOException {

		JsonWriter out = initJsonWriter(ResultCode.OK, writer);

		out.beginObject();

		out.name("classic");
		out.beginObject();
		Emoticons emoticons = Emoticons.getInstance();
		out.name("resultSize").value(emoticons.serieClassica.size());
		out.name("emos");
		out.beginArray();
		for (Emoticon e : emoticons.serieClassica) {
			out.beginObject();
			out.name("emo");
			out.beginObject();
			out.name("id").value(e.sequence);
			out.name("url").value("images/emo/" + e.imgName + ".gif");
			out.endObject();
			out.endObject();
		}
		out.endArray();
		out.endObject(); // classic

		out.name("extended");
		out.beginObject();
		out.name("resultSize").value(emoticons.serieEstesa.size());
		out.name("emos");
		out.beginArray();
		for (Emoticon e : emoticons.serieEstesa) {
			out.beginObject();
			out.name("emo");
			out.beginObject();
			out.name("id").value(e.sequence);
			out.name("url").value("images/emoextended/" + e.imgName + ".gif");
			out.endObject();
			out.endObject();
		}
		out.endArray();
		out.endObject(); // extendedEmo

		out.endObject();

		closeJsonWriter(out, time);
	}

	/**
	 * fornisce l'ultimo id della tabella
	 * @param out
	 * @param params
	 * @throws IOException
	 */
	protected void getLastId(StringBuilderWriter writer, Map<String, String[]> params, long time) throws IOException {
		long id = persistence.getLastId();

		JsonWriter out = initJsonWriter(ResultCode.OK, writer);

		out.beginObject();
		out.name("id").value(id);
		out.endObject();

		closeJsonWriter(out, time);
	}

	/**
	 * Posta un messaggio (Nuovo o editato).
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void addMessage(StringBuilderWriter writer, Map<String, String[]> params, long time) throws IOException, NoSuchAlgorithmException {

		String type = getStringValue(params, "type", null);
		if (type == null) {
			writeErrorMessage(writer, "Manca il parametro 'type'", time);
			return;
		}

		MessageDTO message = new MessageDTO();

		// text
		String text = getStringValue(params, "text", null);
		if (text == null || text.length() < 5) {
			writeErrorMessage(writer, "Un po di fantasia, scrivi almeno 5 caratteri ...", time);
			return;
		}
		if (text.length() > Messages.MAX_MESSAGE_LENGTH) {
			writeErrorMessage(writer, "Sei piu' logorroico di una Wakka, stai sotto i " + Messages.MAX_MESSAGE_LENGTH + " caratteri !", time);
			return;
		}
		message.setText(InputSanitizer.sanitizeText(text));

		// subject
		String subject = getStringValue(params, "subject", null);
		if (subject == null || subject.length() < 3) {
			writeErrorMessage(writer, "Oggetto di almeno di 3 caratteri, cribbio !", time);
			return;
		}
		if (subject.length() > Messages.MAX_SUBJECT_LENGTH) {
			writeErrorMessage(writer, "LOL oggetto piu' lungo di " + Messages.MAX_SUBJECT_LENGTH + " caratteri !", time);
			return;
		}
		message.setSubject(InputSanitizer.sanitizeSubject(subject));

		// author - non e' possibile postare da ANOnimi !
		String nick = getStringValue(params, "nick", null);
		if (nick == null) {
			writeErrorMessage(writer, "Non puoi postare da ANOnimo", time);
			return;
		}
		String password = getStringValue(params, "password", "");
		if (password == null) {
			writeErrorMessage(writer, "Manca la password", time);
			return;
		}
		AuthorDTO author = persistence.getAuthor(nick);
		if (!PasswordUtils.hasUserPassword(author, password)) {
			writeErrorMessage(writer, "Password errata", time);
			return;
		}
		String anonymous = getStringValue(params, "anonymous", null);
		if (StringUtils.isEmpty(anonymous)) {
			message.setAuthor(author);
		} else {
			message.setAuthor(new AuthorDTO(author));
		}

		if (type.equals("new")) {
			// forum
			String forum = getStringValue(params, "forum", null);
			if (forum != null && !forum.equals(IPersistence.FORUM_ASHES)) {
				forum = InputSanitizer.sanitizeForum(forum);
				if (!persistence.getForums().contains(forum)) {
					writeErrorMessage(writer, "Ma che cacchio di forum e' '" + forum + "' ?!?", time);
					return;
				} else {
					message.setForum(forum);
				}
			}
			author.setMessages(author.getMessages() + 1);
			message.setDate(new Date());
			persistence.updateAuthor(author);
		} else if (type.equals("edit") || type.equals("quote") || type.equals("reply")) {
			// msgId / parentId
			long msgId = getLongValue(params, "msgId", -1);
			if (msgId == -1) {
				writeErrorMessage(writer, "Manca il parametro 'msgId'", time);
				return;
			}

			// evita il post in un altro forum !
			MessageDTO oldMessage = persistence.getMessage(msgId);
			message.setForum(oldMessage.getForum());

			if (type.equals("edit")) {
				// check se l'autore e' lo stesso
				AuthorDTO messageAuthor = oldMessage.getAuthor();
				if (messageAuthor != null && !nick.equalsIgnoreCase(messageAuthor.getNick())) {
					writeErrorMessage(writer, "Imbroglione, non puoi modificare questo messaggio !", time);
					return;
				}
				// nuovo testo & subject
				oldMessage.setText(message.getText() + "<BR><BR><b>**Modificato dall'autore il " +
						new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()) + "**</b>");
				oldMessage.setSubject(message.getSubject());
				message = oldMessage;
			} else { // quote / reply
				message.setDate(new Date());
				message.setParentId(msgId);
				message.setThreadId(oldMessage.getThreadId());
			}
		} else {
			writeErrorMessage(writer, "Tipo '" + type + "' non valido", time);
			return;
		}

		message = persistence.insertMessage(message);

		JsonWriter out = initJsonWriter(ResultCode.OK, writer);

		out.beginObject();
		out.name("id").value(message.getId());
		out.endObject();

		closeJsonWriter(out, time);
	}

	/**
	 * Scrive l'exception in formato JSON direttamente nella response.
	 * @param e
	 * @param res
	 * @throws IOException
	 */
	private void handleException(Throwable e, Writer writer, long time) throws IOException {

		JsonWriter out = initJsonWriter(ResultCode.ERROR, writer);

		out.beginObject();
		out.name("message").value(ExceptionUtils.getMessage(e));
		out.name("stackTrace");
		out.beginArray();
		for (String sf : ExceptionUtils.getStackFrames(e)) {
			out.value(sf.replaceAll("^\t", ""));
		}
		out.endArray();
		out.endObject();

		closeJsonWriter(out, time);

	}

	private void writeErrorMessage(Writer writer, String message, long time) throws IOException {

		JsonWriter out = initJsonWriter(ResultCode.ERROR, writer);

		out.beginObject();
		out.name("message").value(message);

		out.name("executionTimeMs").value(System.currentTimeMillis() - time);
		out.endObject();

		closeJsonWriter(out, time);

	}

	/**
	 * Codifica JSON un ThreadDTO
	 * @param threadDTO
	 * @param out
	 * @throws Exception
	 */
	private void encodeThread(ThreadDTO threadDTO, JsonWriter out) throws IOException {
		out.beginObject();
		out.name("thread");
		out.beginObject();
		out.name("id").value(threadDTO.getId());
		out.name("date").value(threadDTO.getDate().getTime());
		out.name("subject").value(threadDTO.getSubject());
		out.name("forum").value(threadDTO.getForum());
		out.name("numberOfMessages").value(threadDTO.getNumberOfMessages());
		AuthorDTO author = threadDTO.getAuthor();
		if (author.isValid()) {
			encodeAuthor(author, out);
		}
		out.endObject();
		out.endObject();
	}

	/**
	 * Codifica JSON un MessageDTO
	 * @param threadDTO
	 * @param out
	 * @throws Exception
	 */
	private void encodeMessage(MessageDTO messageDTO, JsonWriter out) throws IOException {
		out.beginObject();
		out.name("message");
		out.beginObject();
		out.name("id").value(messageDTO.getId());
		out.name("date").value(messageDTO.getDate().getTime());
		out.name("subject").value(messageDTO.getSubject());
		out.name("forum").value(messageDTO.getForum() == null ? "" : messageDTO.getForum());
		out.name("parentId").value(messageDTO.getParentId());
		out.name("threadId").value(messageDTO.getThreadId());
		out.name("text").value(messageDTO.getText());
		AuthorDTO author = messageDTO.getAuthor();
		if (author.isValid()) {
			encodeAuthor(author, out);
		}
		out.endObject();
		out.endObject();
	}

	/**
	 * Codifica JSON un AuthorDTO
	 * @param threadDTO
	 * @param out
	 * @throws Exception
	 */
	private void encodeAuthor(AuthorDTO author, JsonWriter out) throws IOException {
		out.name("author");
		out.beginObject();
		out.name("nick").value(author.getNick());
		out.name("messages").value(author.getMessages());
		out.name("active").value(StringUtils.isNotEmpty(author.getHash()));
		if (author.getAvatar() != null) {
			out.name("avatar").value(new BASE64Encoder().encode(author.getAvatar()));
		}
		out.endObject();
	}

	/**
	 * Inizializza un writer JSON con quel resultCode e un oggetto "content".
	 * @param resultCode
	 * @param out
	 * @return
	 * @throws IOException
	 */
	private JsonWriter initJsonWriter(ResultCode resultCode, Writer writer) throws IOException {
		JsonWriter out = new JsonWriter(writer);
		out.setHtmlSafe(true);
		out.setIndent("  ");
		out.beginObject();
		out.name("resultCode").value(resultCode.toString());
		out.name("content");
		return out;
	}

	/**
	 * Chiude un writer JSON precedentemente aperto tramite {@link #initJsonWriter(ResultCode, Writer)}
	 * @param out
	 * @throws IOException
	 */
	private void closeJsonWriter(JsonWriter out, long time) throws IOException {
		out.name("executionTimeMs").value(System.currentTimeMillis() - time);
		out.endObject();
		out.flush();
		out.close();
	}

	private int getIntValue(Map<String, String[]> params, String name, int defaultValue) {
		String[] param = params.get(name);
		if (param != null && param.length > 0 && StringUtils.isNotEmpty(param[0])) {
			return Integer.parseInt(param[0]);
		}
		return defaultValue;
	}

	private long getLongValue(Map<String, String[]> params, String name, long defaultValue) {
		String[] param = params.get(name);
		if (param != null && param.length > 0 && StringUtils.isNotEmpty(param[0])) {
			return Long.parseLong(param[0]);
		}
		return defaultValue;
	}

	private String getStringValue(Map<String, String[]> params, String name, String defaultValue) {
		String[] param = params.get(name);
		if (param != null && param.length > 0 && StringUtils.isNotEmpty(param[0])) {
			return param[0];
		}
		return defaultValue;
	}

	private int getPageSize(Map<String, String[]> params) {
		String[] paramPageSize = params.get("pageSize");
		if (paramPageSize != null && paramPageSize.length > 0) {
			return Math.min(MAX_PAGE_SIZE, Integer.parseInt(paramPageSize[0]));
		}
		return DEFAULT_PAGE_SIZE;
	}

	/**
	 * Result code per la risposta
	 * @author giambo
	 *
	 */
	private enum ResultCode {
		OK("OK", 1),
		ERROR("ERROR", -1);
		private String humanCode;
		private int code;
		ResultCode(String humanCode, int code) {
			this.humanCode = humanCode;
			this.code = code;
		}
		public String toString() {
			return humanCode + ":" + code;
		}
	}

	/**
	 * Reindirizza alla pagina che spiega come funziona tutto l'ambaradan
	 * @param req
	 * @param res
	 * @throws ServletException
	 * @throws IOException
	 */
	private void printUsage(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/pages/jsonUsage.html").forward(req, res);
	}

}
