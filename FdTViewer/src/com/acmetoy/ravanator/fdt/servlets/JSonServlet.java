package com.acmetoy.ravanator.fdt.servlets;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;

import sun.misc.BASE64Encoder;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.IPersistence;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.acmetoy.ravanator.fdt.persistence.MessagesDTO;
import com.acmetoy.ravanator.fdt.persistence.PersistenceFactory;
import com.acmetoy.ravanator.fdt.persistence.ThreadDTO;
import com.acmetoy.ravanator.fdt.persistence.ThreadsDTO;
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
		try {
			long time = System.currentTimeMillis();
			StringWriter writer = new StringWriter();
			Method m = this.getClass().getDeclaredMethod(action, JsonWriter.class, Map.class);
			JsonWriter jsw = initWriter(ResultCode.OK, writer);
			m.invoke(this, jsw, req.getParameterMap());
			closeWriter(jsw, time);
			res.getWriter().write(writer.getBuffer().toString());
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
	 * Parametro neccessario "page".
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getThreads(JsonWriter writer, Map<String, String[]> params) throws IOException {
		int page = 0;
		String[] paramPage = params.get("page");
		if (paramPage != null && paramPage.length > 0) {
			page = Integer.parseInt(paramPage[0]);
		}
		
		int pageSize = DEFAULT_PAGE_SIZE;
		String[] paramPageSize = params.get("pageSize");
		if (paramPageSize != null && paramPageSize.length > 0) {
			pageSize = Math.min(MAX_PAGE_SIZE, Integer.parseInt(paramPageSize[0]));
		}
		
		ThreadsDTO result = persistence.getThreads(pageSize, page, false);
		
		writer.beginObject();
		writer.name("page").value(page);
		writer.name("pageSize").value(pageSize);
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
	 * Parametro neccessario "page".
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getMessages(JsonWriter writer, Map<String, String[]> params) throws IOException {
		int page = 0;
		String[] paramPage = params.get("page");
		if (paramPage != null && paramPage.length > 0) {
			page = Integer.parseInt(paramPage[0]);
		}
		
		int pageSize = DEFAULT_PAGE_SIZE;
		String[] paramPageSize = params.get("pageSize");
		if (paramPageSize != null && paramPageSize.length > 0) {
			pageSize = Math.min(MAX_PAGE_SIZE, Integer.parseInt(paramPageSize[0]));
		}
		
		MessagesDTO result = persistence.getMessagesByDate(pageSize, page, false);
		
		writer.beginObject();
		writer.name("page").value(page);
		writer.name("pageSize").value(pageSize);
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
	 * Parametro neccessario "page".
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected void getThread(JsonWriter writer, Map<String, String[]> params) throws IOException {
		long threadId = 0;
		String[] paramThreadId = params.get("threadId");
		if (paramThreadId == null || paramThreadId.length == 0) {
			throw new IOException("missing parameter 'threadId'");
		}
		threadId = Long.parseLong(paramThreadId[0]);
		
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
	 * Scrive l'exception in formato JSON direttamente nella response.
	 * @param e
	 * @param res
	 * @throws IOException
	 */
	private void handleException(Exception e, HttpServletResponse res) throws IOException {
		JsonWriter writer = initWriter(ResultCode.ERROR, res.getWriter());
		writer.beginArray();
		for (String sf : ExceptionUtils.getStackFrames(e)) {
			writer.value(sf);
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
		writer.name("date").value(threadDTO.getDate().toString());
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
		writer.name("date").value(messageDTO.getDate().toString());
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
	
}
