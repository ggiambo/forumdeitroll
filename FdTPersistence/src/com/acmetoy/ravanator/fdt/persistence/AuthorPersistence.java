package com.acmetoy.ravanator.fdt.persistence;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

public class AuthorPersistence extends Persistence {

	private static final Logger LOG = Logger.getLogger(AuthorPersistence.class);

	private static AuthorPersistence instance;

	private AuthorPersistence() throws UnknownHostException, MongoException {
		super();
	}
	
	public static AuthorPersistence getInstance() throws Exception {
		if (instance != null) {
			return instance;
		}
		synchronized (MessagePersistence.class) {
			try {
				instance = new AuthorPersistence();
			} catch (UnknownHostException e) {
				throw new Exception(e);
			} catch (MongoException e) {
				throw new Exception(e);
			}
		}
		return instance;
	}
	
	public final boolean hasAuthor(String nick) {
		return getDb().getCollection("authors").find(new BasicDBObject("nick", nick)).count() > 0;
	}

	public void insertAuthor(String nick, int ranking, int messages, byte[] avatar) {
		BasicDBObject msg = new BasicDBObject();
		msg.put("nick", nick);
		msg.put("ranking", ranking);
		msg.put("messages", messages);
		msg.put("avatar", avatar);
		getDb().getCollection("authors").save(msg);
		LOG.info("Persisted author '" + nick + "'");
	}
	
	public DBObject getAuthor(String nick) {
		BasicDBObject author = new BasicDBObject();
		author.put("nick", nick);
		return getDb().getCollection("authors").findOne(author);
	}
	
}
