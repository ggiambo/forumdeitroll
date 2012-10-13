package com.forumdeitroll.servlets;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Collections;
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
			Method m = this.getClass().getDeclaredMethod(action, JsonWriter.class, Map.class);
			JsonWriter jsw = initWriter(ResultCode.OK, writer);
			m.invoke(this, jsw, Collections.unmodifiableMap(req.getParameterMap()));
			closeWriter(jsw, time);
			res.setContentType("application/json");
			String callback = req.getParameter("callback");
			if (callback != null) {
				writer.getBuilder().insert(0, callback + "(").append(")");
			}
			res.getWriter().write(writer.getBuilder().toString());
		} catch (NoSuchMethodException e) {
			printUsage(req, res);
			return;
		} catch (Exception e) {
			handleException(e, res);
			return;
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		JsonWriter writer = initWriter(ResultCode.ERROR, res.getWriter());
		writer.value("POST not implemented");
		writer.endObject();
		writer.flush();
		writer.close();
	}
	
	/**
	 * Ritorna una stringa JSON contenente tutti i forums
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getForums(JsonWriter writer, Map<String, String[]> params) throws IOException {
		List<String> forums = persistence.getForums();
		writer.beginObject();
		writer.name("forums");
		writer.beginArray();
		for (String forum : forums) {
			writer.value(forum);
		}
		writer.endArray();
		writer.endObject();
	}
	
	/**
	 * Ritorna una stringa JSON contenente i threads ordinati temporalmente decrescentemente.
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getThreads(JsonWriter writer, Map<String, String[]> params) throws IOException {
		int page = getPage(params);
		int pageSize = getPageSize(params);
		String forum = getForum(params);
		
		ThreadsDTO result = persistence.getThreads(forum, pageSize, page, false);
		
		writer.beginObject();
		writer.name("page").value(page);
		writer.name("pageSize").value(pageSize);
		writer.name("forum").value(forum);
		writer.name("resultSize").value(result.getMessages().size());
		writer.name("totalSize").value(result.getMaxNrOfMessages());
		writer.name("threads");
		
		writer.beginArray();
		for (ThreadDTO threadDTO : result.getMessages()) {
			encodeThread(threadDTO, writer);
		}
		writer.endArray();
		
		writer.endObject(); // "threads"
	}
	
	/**
	 * Ritorna una stringa JSON contenente i messaggi ordinati temporalmente decrescentemente. 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getMessages(JsonWriter writer, Map<String, String[]> params) throws IOException {
		int page = getPage(params);
		int pageSize = getPageSize(params);
		String forum = getForum(params);
		
		MessagesDTO result = persistence.getMessages(forum, pageSize, page, false);
		
		writer.beginObject();
		writer.name("page").value(page);
		writer.name("pageSize").value(pageSize);
		writer.name("forum").value(forum);
		writer.name("resultSize").value(result.getMessages().size());
		writer.name("totalSize").value(result.getMaxNrOfMessages());
		writer.name("messages");
		
		writer.beginArray();
		for (MessageDTO messageDTO : result.getMessages()) {
			encodeMessage(messageDTO, writer);
		}
		writer.endArray();
		
		writer.endObject(); // "messages"
	}
	
	/**
	 * Ritorna una stringa JSON contenente i messaggi di un singolo thread. 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getThread(JsonWriter writer, Map<String, String[]> params) throws IOException {
		long threadId = getThreadId(params);
		if (threadId == -1) {
			throw new IOException("missing parameter 'threadId'");
		}
		
		List<MessageDTO> result = persistence.getMessagesByThread(threadId);
		
		writer.beginObject();
		writer.name("threadId").value(threadId);
		writer.name("resultSize").value(result.size());
		writer.name("messages");
		
		writer.beginArray();
		for (MessageDTO messageDTO : result) {
			encodeMessage(messageDTO, writer);
		}
		writer.endArray();
		
		writer.endObject(); // "messages"
	}
	
	/**
	 * Ritorna una stringa JSON contenente un singolo messaggio 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getMessage(JsonWriter writer, Map<String, String[]> params) throws IOException {
		long msgId = getMsgId(params);
		if (msgId == -1) {
			throw new IOException("missing parameter 'msgId'");
		}
		
		MessageDTO result = persistence.getMessage(msgId);
		encodeMessage(result, writer);
	}
	
	/**
	 * Ritorna una stringa JSON contenente l'autore
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getAuthor(JsonWriter writer, Map<String, String[]> params) throws IOException {
		String[] nick = params.get("nick");
		if (nick == null || nick.length == 0) {
			throw new IOException("missing parameter 'nick'");
		}
		
		AuthorDTO result = persistence.getAuthor(nick[0]);
		writer.beginObject();
		encodeAuthor(result, writer);
		writer.endObject();
	}
	
	/**
	 * Ritorna una stringa JSON contenente gli autori
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getAuthors(JsonWriter writer, Map<String, String[]> params) throws IOException {
		boolean onlyActive = false;
		String[] paramOnlyActive = params.get("onlyActive");
		if (paramOnlyActive != null && paramOnlyActive.length > 0) {
			onlyActive = Boolean.parseBoolean(paramOnlyActive[0]);
		}
		
		List<AuthorDTO> result = persistence.getAuthors(onlyActive);
		
		writer.beginObject();
		writer.name("resultSize").value(result.size());
		writer.name("authors");
		
		writer.beginArray();
		for (AuthorDTO authorDTO : result) {
			writer.beginObject();
			encodeAuthor(authorDTO, writer);
			writer.endObject();
		}
		writer.endArray();
		
		writer.endObject(); // "authors"
	}
	
	/**
	 * Ritorna una stringa JSON contenente le quotes dell'autore
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getQuotes(JsonWriter writer, Map<String, String[]> params) throws IOException {
		String[] nick = params.get("nick");
		if (nick == null || nick.length == 0) {
			throw new IOException("missing parameter 'nick'");
		}
		
		AuthorDTO author = persistence.getAuthor(nick[0]);
		List<QuoteDTO> result = persistence.getQuotes(author);
		
		writer.beginObject();
		writer.name("resultSize").value(result.size());
		writer.name("quotes");
		writer.beginArray();
		for (QuoteDTO quote : result) {
			writer.beginObject();
			writer.name("quote");
			writer.beginObject();
			writer.name("id").value(quote.getId());
			writer.name("content").value(quote.getContent());
			writer.endObject();
			writer.endObject();
		}
		writer.endArray();
		
		writer.endObject(); // "quotes"
	}
	
	protected void getEmos(JsonWriter writer, Map<String, String[]> params) throws IOException {
		writer.beginObject();
		
		writer.name("classic");
		writer.beginObject();
		Map<String, String[]> emoMap = Messages.getEmoMap();
		writer.name("resultSize").value(emoMap.size());
		writer.name("emos");
		writer.beginArray();
		for (Map.Entry<String, String[]> entry : emoMap.entrySet()) {
			writer.beginObject();
			writer.name("emo");
			writer.beginObject();
			writer.name("id").value(entry.getValue()[0]);
			writer.name("url").value("images/emo/" + entry.getKey() + ".gif");
			writer.endObject();
			writer.endObject();
		}
		writer.endArray();
		writer.endObject(); // classic

		writer.name("extended");
		writer.beginObject();
		Map<String, String[]> emoExtendedMap = Messages.getEmoExtendedMap();
		writer.name("resultSize").value(emoExtendedMap.size());
		writer.name("emos");
		writer.beginArray();
		for (Map.Entry<String, String[]> entry : emoExtendedMap.entrySet()) {
			writer.beginObject();
			writer.name("emo");
			writer.beginObject();
			writer.name("id").value(entry.getValue()[0]);
			writer.name("url").value("images/emoextended/" + entry.getKey() + ".gif");
			writer.endObject();
			writer.endObject();
		}
		writer.endArray();
		writer.endObject(); // extendedEmo
		
		writer.endObject();
	}
	
	/**
	 * fornisce l'ultimo id della tabella
	 * @param writer
	 * @param params
	 * @throws IOException
	 */
	protected void getLastId(JsonWriter writer, Map<String, String[]> params) throws IOException {
		long id = persistence.getLastId();
		writer.beginObject();
		writer.name("id").value(id);
		writer.endObject();
	}
	
	/**
	 * Scrive l'exception in formato JSON direttamente nella response.
	 * @param e
	 * @param res
	 * @throws IOException
	 */
	private void handleException(Exception e, HttpServletResponse res) throws IOException {
		JsonWriter writer = initWriter(ResultCode.ERROR, res.getWriter());
		writer.beginArray();
		for (String sf : ExceptionUtils.getStackFrames(e)) {
			writer.value(sf.replaceAll("^\t", ""));
		}
		writer.endArray();
		writer.endObject();
		writer.flush();
		writer.close();
	}
	
	/**
	 * Codifica JSON un ThreadDTO
	 * @param threadDTO
	 * @param writer
	 * @throws Exception
	 */
	private void encodeThread(ThreadDTO threadDTO, JsonWriter writer) throws IOException {
		writer.beginObject();
		writer.name("thread");
		writer.beginObject();
		writer.name("id").value(threadDTO.getId());
		writer.name("date").value(threadDTO.getDate().getTime());
		writer.name("subject").value(threadDTO.getSubject());
		writer.name("forum").value(threadDTO.getForum());
		writer.name("numberOfMessages").value(threadDTO.getNumberOfMessages());
		AuthorDTO author = threadDTO.getAuthor();
		if (author.isValid()) {
			encodeAuthor(author, writer);
		}
		writer.endObject();
		writer.endObject();
	}
	
	/**
	 * Codifica JSON un MessageDTO
	 * @param threadDTO
	 * @param writer
	 * @throws Exception
	 */
	private void encodeMessage(MessageDTO messageDTO, JsonWriter writer) throws IOException {
		writer.beginObject();
		writer.name("message");
		writer.beginObject();
		writer.name("id").value(messageDTO.getId());
		writer.name("date").value(messageDTO.getDate().getTime());
		writer.name("subject").value(messageDTO.getSubject());
		writer.name("forum").value(messageDTO.getForum() == null ? "" : messageDTO.getForum());
		writer.name("parentId").value(messageDTO.getParentId());
		writer.name("threadId").value(messageDTO.getThreadId());
		writer.name("text").value(messageDTO.getText());
		AuthorDTO author = messageDTO.getAuthor();
		if (author.isValid()) {
			encodeAuthor(author, writer);
		}
		writer.endObject();
		writer.endObject();
	}
	
	/**
	 * Codifica JSON un AuthorDTO
	 * @param threadDTO
	 * @param writer
	 * @throws Exception
	 */
	private void encodeAuthor(AuthorDTO author, JsonWriter writer) throws IOException {
		writer.name("author");
		writer.beginObject();
		writer.name("nick").value(author.getNick());
		writer.name("messages").value(author.getMessages());
		writer.name("active").value(StringUtils.isNotEmpty(author.getHash()));
		if (author.getAvatar() != null) {
			writer.name("avatar").value(new BASE64Encoder().encode(author.getAvatar()));
		}
		writer.endObject();
	}
	
	/**
	 * Inizializza un writer JSON con quel resultCode e un oggetto "content".
	 * @param resultCode
	 * @param out
	 * @return
	 * @throws IOException
	 */
	private JsonWriter initWriter(ResultCode resultCode, Writer out) throws IOException {
		JsonWriter writer = new JsonWriter(out);
		writer.setIndent("  ");
		writer.beginObject();
		writer.name("resultCode").value(resultCode.toString());
		writer.name("content");
		return writer;
	}
	
	/**
	 * Chiude un writer JSON precedentemente aperto tramite {@link #initWriter(ResultCode, Writer)}
	 * @param writer
	 * @throws IOException
	 */
	private void closeWriter(JsonWriter writer, long time) throws IOException {
		writer.name("executionTimeMs").value(System.currentTimeMillis() - time);
		writer.endObject();
		writer.flush();
		writer.close();
	}
	
	private int getPage(Map<String, String[]> params) {
		String[] paramPage = params.get("page");
		if (paramPage != null && paramPage.length > 0) {
			return Integer.parseInt(paramPage[0]);
		}
		return 0;
	}
	
	private String getForum(Map<String, String[]> params) {
		String[] paramForum = params.get("forum");
		if (paramForum != null && paramForum.length > 0) {
			 return paramForum[0];
		}
		return null;
	}
	
	private int getPageSize(Map<String, String[]> params) {
		String[] paramPageSize = params.get("pageSize");
		if (paramPageSize != null && paramPageSize.length > 0) {
			return Math.min(MAX_PAGE_SIZE, Integer.parseInt(paramPageSize[0]));
		}
		return DEFAULT_PAGE_SIZE;
	}
	
	private long getThreadId(Map<String, String[]> params) {
		String[] paramThreadId = params.get("threadId");
		if (paramThreadId == null || paramThreadId.length == 0) {
			return -1;
		}
		return Long.parseLong(paramThreadId[0]);
	}	
	
	private long getMsgId(Map<String, String[]> params) {
		String[] msgId = params.get("msgId");
		if (msgId == null || msgId.length == 0) {
			return -1;
		}
		return Long.parseLong(msgId[0]);
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
	
	private void printUsage(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/pages/jsonUsage.html").forward(req, res);
	}
	
}