package com.acmetoy.ravanator.fdt.persistence;

import java.net.UnknownHostException;
import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

public class StatusPersistence extends Persistence {
	
	private static StatusPersistence instance;

	private StatusPersistence() throws UnknownHostException, MongoException {
		super();
	}
	
	public static StatusPersistence getInstance() throws Exception {
		if (instance != null) {
			return instance;
		}
		synchronized (MessagePersistence.class) {
			try {
				instance = new StatusPersistence();
			} catch (UnknownHostException e) {
				throw new Exception(e);
			} catch (MongoException e) {
				throw new Exception(e);
			}
		}
		return instance;
	}

	public Date getLastScan() {
		DBObject date = getDb().getCollection("status").findOne();
		if (date != null) {
			return (Date)date.get("date");
		}
		return null;
	}

	public void updateScanDate() {
		Date date = getLastScan();
		if (date == null) {
			getDb().getCollection("status").save(new BasicDBObject("date", new Date()));
		} else {
			getDb().getCollection("status").update(new BasicDBObject("date",date), new BasicDBObject("date", new Date()));
		}
	}
}
