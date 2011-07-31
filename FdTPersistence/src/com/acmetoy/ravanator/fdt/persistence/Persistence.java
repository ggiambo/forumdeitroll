package com.acmetoy.ravanator.fdt.persistence;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public abstract class Persistence {
	
	private DB db;
	
	protected Persistence() throws UnknownHostException, MongoException {
		db = new Mongo("127.0.0.1", 27017).getDB("fdtDb");
		// check structure
		DBCollection msgs = db.getCollection("messages");
		msgs.ensureIndex("id");
		DBCollection auth = db.getCollection("authors");
		auth.ensureIndex("nick");
	}
	
	protected DB getDb() {
		return db;
	}

}
