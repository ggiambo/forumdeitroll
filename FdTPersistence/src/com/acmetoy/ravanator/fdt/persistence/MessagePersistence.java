package com.acmetoy.ravanator.fdt.persistence;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

public class MessagePersistence extends Persistence {
	
	private static final Logger LOG = Logger.getLogger(Persistence.class);
	
	private static MessagePersistence instance;

	private MessagePersistence() throws UnknownHostException, MongoException {
		super();
	}
	
	public static MessagePersistence getInstance() throws Exception {
		if (instance != null) {
			return instance;
		}
		synchronized (MessagePersistence.class) {
			try {
				instance = new MessagePersistence();
			} catch (UnknownHostException e) {
				throw new Exception(e);
			} catch (MongoException e) {
				throw new Exception(e);
			}
		}
		return instance;
	}
		
	public final void insertMessage(long id, long parentId, long threadId, String text, String subject, String author, Date date) {
		BasicDBObject msg = new BasicDBObject();
		msg.put("id", id);
		msg.put("text", text);
		msg.put("date", date);
		msg.put("subject", subject);
		msg.put("threadId", threadId);
		if (parentId != -1) {
			msg.put("parentId", parentId);
		}
		if (author != null) {
			msg.put("author", author);
		}
		getDb().getCollection("messages").save(msg);
		LOG.info("Persisted message '" + id + "'");
	}
	
	public final boolean hasMessage(long id) {
		return getDb().getCollection("messages").find(new BasicDBObject("id", id)).count() > 0;
	}
	
	public DBObject getMesage(long id) {
		DBObject msg = new BasicDBObject();
		msg.put("id", id);
		return getDb().getCollection("messages").findOne(msg);
	}
	
	public List<DBObject> getMessagesByDate(int limit) {
		
		List<DBObject> ret = new ArrayList<DBObject>(limit);
//		DBCursor cur = getDb().getCollection("messages").find(new BasicDBObject()).sort(new BasicDBObject("date", -1)).limit(limit);
		DBCursor cur = getDb().getCollection("messages").find(new BasicDBObject()).sort(new BasicDBObject("date", -1));
		while (cur.hasNext()) {
			ret.add(cur.next());
		}
		
		return ret;
	}
	
}
