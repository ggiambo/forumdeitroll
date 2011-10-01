package com.acmetoy.ravanator.fdt.persistence.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.acmetoy.ravanator.fdt.persistence.Persistence;
import com.acmetoy.ravanator.fdt.persistence.ThreadDTO;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

@Deprecated
public class MongoPersistence implements Persistence {

	private static final Logger LOG = Logger.getLogger(MongoPersistence.class);

	private DB db;

	public MongoPersistence(Properties config) throws Exception {
		String host = config.getProperty("host");
		int port = Integer.parseInt(config.getProperty("port"));
		String dbname = config.getProperty("dbname");
		db = new Mongo(host, port).getDB(dbname);
		// check structure
		DBCollection msgs = db.getCollection("messages");
		msgs.ensureIndex("id");
		DBCollection auth = db.getCollection("authors");
		auth.ensureIndex("nick");
	}

	public void insertMessage(MessageDTO message) {
		BasicDBObject msg = new BasicDBObject();
		msg.put("id", message.getId());
		msg.put("text", message.getText());
		msg.put("date", message.getDate());
		msg.put("subject", message.getSubject());
		msg.put("threadId", message.getThreadId());
		if (message.getParentId() != -1) {
			msg.put("parentId", message.getParentId());
		}
		if (message.getAuthor() != null) {
			msg.put("author", message.getAuthor());
		}
		if (message.getForum() != null) {
			msg.put("forum", message.getForum());
		}
		db.getCollection("messages").save(msg);
		LOG.info("Persisted message '" + message.getId() + "'");
	}

	public boolean hasMessage(long id) {
		return db.getCollection("messages").find(new BasicDBObject("id", id)).count() > 0;
	}

	public MessageDTO getMessage(long id) {
		DBObject msg = new BasicDBObject();
		msg.put("id", id);
		return toMessageDTO(db.getCollection("messages").findOne(msg).toMap());
		
		
	}

	public List<MessageDTO> getMessagesByDate(int limit) {
		List<MessageDTO> ret = new ArrayList<MessageDTO>(limit);
		DBCursor cur = db.getCollection("messages").find(new BasicDBObject()).sort(new BasicDBObject("date", -1)).limit(limit);
		while (cur.hasNext()) {
			ret.add(toMessageDTO(cur.next().toMap()));
		}
		return ret;
	}

	public List<MessageDTO> getMessagesByDate(int limit, int page) {
		List<MessageDTO> ret = new ArrayList<MessageDTO>(limit);
		DBCursor cur = db.getCollection("messages").find(new BasicDBObject()).sort(new BasicDBObject("date", -1)).skip(page * limit).limit(
				limit);
		while (cur.hasNext()) {
			ret.add(toMessageDTO(cur.next().toMap()));
		}
		return ret;
	}
	
	public List<ThreadDTO> getThreads(int limit, int page) {
		List<ThreadDTO> ret = new ArrayList<ThreadDTO>(limit);
		BasicDBObject query = new BasicDBObject("id", new BasicDBObject("$in", db.getCollection("messages").distinct("threadId")));
		DBCursor cur = db.getCollection("messages").find(query).sort(new BasicDBObject("date", -1)).skip(page * limit).limit(
				limit);
		while (cur.hasNext()) {
			ret.add(toThreadDTO(cur.next().toMap()));
		}
		return ret;
	}

	public List<MessageDTO> getMessagesByThread(long threadId) {
		DBCursor cur = db.getCollection("messages").find(new BasicDBObject("threadId", threadId));
		List<MessageDTO> ret = new ArrayList<MessageDTO>(cur.count());
		while (cur.hasNext()) {
			ret.add(toMessageDTO(cur.next().toMap()));
		}
		return ret;
	}

	public long getLastMessageId() {
		DBObject o = db.getCollection("messages").findOne(new BasicDBObject(), new BasicDBObject("id", 1));
		if (o == null) {
			return -1;
		}
		return (Long) o.get("id");
	}

	public final boolean hasAuthor(String nick) {
		return db.getCollection("authors").find(new BasicDBObject("nick", nick)).count() > 0;
	}

	public void insertAuthor(AuthorDTO author) {
		BasicDBObject msg = new BasicDBObject();
		msg.put("nick", author.getNick());
		msg.put("ranking", author.getRanking());
		msg.put("messages", author.getMessages());
		msg.put("avatar", author.getAvatar());
		db.getCollection("authors").save(msg);
		LOG.info("Persisted author '" + author.getNick() + "'");
	}
	
	public void updateAuthor(AuthorDTO author) {
		insertAuthor(author);
	}

	public AuthorDTO getAuthor(String nick) {
		BasicDBObject author = new BasicDBObject();
		author.put("nick", nick);
		DBObject out = db.getCollection("authors").findOne(author);
		if (out == null) {
			return null;
		}
		return toAuthorDTO(out.toMap());
	}
	
	public List<MessageDTO> getMessagesByAuthor(String author, int limit, int page) {
		List<MessageDTO> ret = new ArrayList<MessageDTO>(limit);
		DBCursor cur = db.getCollection("messages").find(new BasicDBObject("author", author)).sort(new BasicDBObject("date", -1)).skip(page * limit).limit(
				limit);
		while (cur.hasNext()) {
			ret.add(toMessageDTO(cur.next().toMap()));
		}
		return ret;
	}
	
	public long countMessages() {
		throw new RuntimeException("MongoPersistence is deprecated, use MySQL");
	}

	public List<MessageDTO> searchMessages(String search, int pageSize, int pageNr) {
		throw new RuntimeException("MongoPersistence is deprecated, use MySQL");
	}
	
	public List<Long> getParentIds(int limit, int page) {
		throw new RuntimeException("MongoPersistence is deprecated, use MySQL");
	}
	
	private MessageDTO toMessageDTO(Map<?, ?> map) {
		MessageDTO out = new MessageDTO();
		try {
			BeanUtils.populate(out, map);
		} catch (Exception e) {
			LOG.info("Cannot populate MessageDTO with " + map);
		}
		return out;
	}
	
	private ThreadDTO toThreadDTO(Map<?, ?> map) {
		ThreadDTO out = new ThreadDTO();
		try {
			BeanUtils.populate(out, map);
		} catch (Exception e) {
			LOG.info("Cannot populate ThreadDTO with " + map);
		}
		return out;
	}
	
	private AuthorDTO toAuthorDTO(Map<?, ?> map) {
		AuthorDTO out = new AuthorDTO();
		try {
			BeanUtils.populate(out, map);
		} catch (Exception e) {
			LOG.info("Cannot populate AuthorDTO with " + map);
		}
		return out;
	}

}
