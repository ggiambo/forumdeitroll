package com.forumdeitroll.persistence.dao;

import static com.forumdeitroll.persistence.jooq.tables.Logins.LOGINS;

import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.forumdeitroll.RandomPool;

public class LoginsDAO extends BaseDAO {

	public LoginsDAO(DSLContext jooq) {
		super(jooq);
	}

	public String createLogin(String nick) {
		// cleanup login vecchie oltre il minuto
		jooq.deleteFrom(LOGINS)
			.where(LOGINS.TSTAMP.lt(DSL.field("date_add(current_timestamp, INTERVAL -1 MINUTE)", Timestamp.class)))
			.execute()
		;
		// chiave generata per accesso
		String key = RandomPool.getString(64);
		// inserisco il record di login
		jooq.insertInto(LOGINS)
			.set(LOGINS.NICK, nick)
			.set(LOGINS.LOGINKEY, key)
			.set(LOGINS.TSTAMP, DSL.field("current_timestamp", Timestamp.class))
			.execute()
		;
		return key;
	}

}
