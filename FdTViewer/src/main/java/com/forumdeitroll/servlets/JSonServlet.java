package com.forumdeitroll.servlets;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.forumdeitroll.SingleValueCache;
import com.forumdeitroll.markup.Emoticon;
import com.forumdeitroll.markup.Emoticons;
import com.forumdeitroll.markup.RenderOptions;
import com.forumdeitroll.markup.Renderer;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.DAOFactory;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.MessagesDTO;
import com.forumdeitroll.persistence.QuoteDTO;
import com.forumdeitroll.persistence.ThreadDTO;
import com.forumdeitroll.persistence.ThreadsDTO;
import com.forumdeitroll.persistence.dao.ThreadsDAO;
import com.forumdeitroll.util.IPMemStorage;
import com.forumdeitroll.util.VisitorCounters;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

/*
QUESTA CLASSE E` UN MERDAIO, IL 90% DEL CODICE IN QUESTA CLASSE E` STATO COPINCOLLATO QUI SENZA RIGUARDO.
CHI HA SCRITTO QUESTA CLASSE (AKA GIAMBO) E` UN MONGOLOIDE E SI DOVREBBE VERGOGNARE.
DOVE CAZZO E` IL CODICE CHE CONTROLLA LO STATO DI BAN DEL POST? DOVE CAZZO E` IL CODICE CHE AGGIORNA IPMEMSTORAGE.
QUANDO SI SCRIVE UNA NUOVA INTERFACCIA DI UN SERVIZIO NON SI FA COPINCOLLANDO IL CODICE DELLA VECCHIA INTERFACCIA IMBECILLE.
*/
public class JSonServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_PAGE_SIZE = 25;

	private static final int MAX_PAGE_SIZE = 100;


	private SingleValueCache<List<String>> cachedForums = new SingleValueCache<List<String>>(60 * 60 * 1000) {
		@Override protected List<String> update() {
			return  DAOFactory.getMiscDAO().getForums();
		}
	};

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
			final Map<String, String[]> parmap = new HashMap<String, String[]>(req.getParameterMap());
			parmap.put("IP", new String[]{ IPMemStorage.requestToIP(req) });
			m.invoke(this, writer, Collections.unmodifiableMap(parmap), time);
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
		if (action.startsWith("get")) {
			res.setHeader("Access-Control-Allow-Origin", "*");
		}
		res.getWriter().write(writer.getBuilder().toString());
		VisitorCounters.add(req);
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
		String order = getStringValue(params, "sort", "start");
		int pageSize = getPageSize(params);
		String forum = getStringValue(params, "forum", null);

		if (pageSize > 200) {
			pageSize = 200;
		}

		final ThreadsDAO tdao = DAOFactory.getThreadsDAO();
		ThreadsDTO result = order.equals("start") ? tdao.getThreads(forum, pageSize, page, null) : tdao.getThreadsByLastPost(forum, pageSize, page, null);

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
	protected void getMessages(StringBuilderWriter writer, Map<String, String[]> params, long time) throws Exception {
		int page = getIntValue(params, "page", 0);
		int pageSize = getPageSize(params);
		String forum = getStringValue(params, "forum", null);
		String renderOpts = getStringValue(params, "renderOpts", null);
		RenderOptions opts = renderOpts != null ? new Gson().fromJson(renderOpts, RenderOptions.class) : null;

		String nick = getStringValue(params, "nick", null);
		MessagesDTO result = DAOFactory.getMessagesDAO().getMessages(forum, nick, pageSize, page, null);

		if (opts != null) {
			for (MessageDTO message : result.getMessages()) {
				StringWriter out = new StringWriter();
				Renderer.render(new StringReader(message.getText()), out, opts);
				message.setText(out.toString());
			}
		}

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

		List<MessageDTO> result = DAOFactory.getMessagesDAO().getMessagesByThread(threadId);

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

		MessageDTO result = DAOFactory.getMessagesDAO().getMessage(msgId);
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

		AuthorDTO result = DAOFactory.getAuthorsDAO().getAuthor(nick);

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

		int page = getIntValue(params, "page", 0);
		int pageSize = getPageSize(params);

		List<AuthorDTO> result = DAOFactory.getAuthorsDAO().getAuthors(onlyActive, pageSize, page);

		JsonWriter out = initJsonWriter(ResultCode.OK, writer);

		out.beginObject();
		out.name("resultSize").value(result.size());
		out.name("page").value(page);
		out.name("pageSize").value(pageSize);
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
		List<QuoteDTO> result;
		String nick = getStringValue(params, "nick", null);
		if (nick == null) {
			result = DAOFactory.getQuotesDAO().getAllQuotes();
		} else {
			AuthorDTO author = DAOFactory.getAuthorsDAO().getAuthor(nick);
			result = DAOFactory.getQuotesDAO().getQuotes(author);
		}

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
			out.name("nick").value(quote.getNick());
			out.endObject();
			out.endObject();
		}
		out.endArray();

		out.endObject(); // "quotes"

		closeJsonWriter(out, time);
	}

	/**
	 * Ritorna una stringa JSON contenente tutte le emoticons
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
	 * @param params
	 * @throws IOException
	 */
	protected void getLastId(StringBuilderWriter writer, Map<String, String[]> params, long time) throws IOException {
		long id = DAOFactory.getMiscDAO().getLastId();

		JsonWriter out = initJsonWriter(ResultCode.OK, writer);

		out.beginObject();
		out.name("id").value(id);
		out.endObject();

		closeJsonWriter(out, time);
	}

	/**
	 * Posta un messaggio (Nuovo o editato).
	 */
	protected void addMessage(StringBuilderWriter writer, Map<String, String[]> params, long time) throws IOException {
//		final MessagePenetrator mp = new MessagePenetrator();
//
//		mp
//			.setForum(getStringValue(params, "forum", null))
//			.setParentId(getStringValue(params, "msgId", null))
//			.setSubject(getStringValue(params, "subject", null))
//			.setText(getStringValue(params, "text", null))
//			.setCredentials(
//				null,
//				getStringValue(params, "nick", null), getStringValue(params, "password", ""),
//				null, "nocaptcha")
//			.setType(getStringValue(params, "type", null));
//
//		if (!StringUtils.isEmpty(getStringValue(params, "anonymous", null))) {
//			mp.setAnonymous();
//		}
//
//		mp.isBanned(null, getStringValue(params, "IP", null), null);
//
//		final MessageDTO msg = mp.penetrate();
//		if (msg == null) {
//			writeErrorMessage(writer, mp.Error(), time);
//			return;
//		}
//
//		JsonWriter out = initJsonWriter(ResultCode.OK, writer);
//
//		out.beginObject();
//		out.name("id").value(msg.getId());
//		out.endObject();
//
//		closeJsonWriter(out, time);
	}

	/**
	 * Scrive l'exception in formato JSON direttamente nella response.
	 * @param e
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
		out.name("lastId").value(threadDTO.getLastId());
		AuthorDTO author = threadDTO.getAuthor();
		if (author.isValid()) {
			encodeAuthor(author, out);
		}
		out.endObject();
		out.endObject();
	}

	/**
	 * Codifica JSON un MessageDTO
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
			out.name("avatar").value("//forumdeitroll.com/Misc?action=getAvatar&nick=" + URLEncoder.encode(author.getNick(), "UTF-8") );
		}
		out.endObject();
	}

	/**
	 * Inizializza un writer JSON con quel resultCode e un oggetto "content".
	 * @param resultCode
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
