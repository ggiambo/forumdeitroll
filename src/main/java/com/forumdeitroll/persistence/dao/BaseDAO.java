package com.forumdeitroll.persistence.dao;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.jooq.tables.records.AuthorsRecord;
import com.forumdeitroll.persistence.jooq.tables.records.PreferencesRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.forumdeitroll.persistence.jooq.Tables.*;

public abstract class BaseDAO {

	private static final Logger LOG = Logger.getLogger(BaseDAO.class);

	protected DSLContext jooq;

	public BaseDAO(DSLContext jooq) {
		this.jooq = jooq;
	}

	public AuthorDTO getAuthor(String nick) {

		if (StringUtils.isEmpty(nick)) {
			return new AuthorDTO(null);
		}

		AuthorsRecord record = jooq.selectFrom(AUTHORS)
				.where(AUTHORS.NICK.upper().equal(nick.toUpperCase()))
				.fetchAny();

		if (record == null) {
			return new AuthorDTO(null);
		}

		AuthorDTO authorDTO = new AuthorDTO(null);
		authorDTO.setAvatar(record.getAvatar());
		authorDTO.setNick(record.getNick());
		authorDTO.setHash(record.getHash());
		authorDTO.setMessages(record.getMessages());
		authorDTO.setOldPassword(record.getPassword());
		authorDTO.setSalt(record.getSalt());
		authorDTO.setSignatureImage(record.getSignatureImage());
		authorDTO.setPreferences(getPreferences(authorDTO));
		authorDTO.enabled(record.getEnabled() == 1);

		authorDTO.setPreferences(getPreferences(authorDTO));
		return authorDTO;
	}

	public Map<String, String> getPreferences(AuthorDTO user) {

		if (user == null || !user.isValid()) {
			return new ConcurrentHashMap<>();
		}

		Result<PreferencesRecord> records = jooq.selectFrom(PREFERENCES)
				.where(PREFERENCES.NICK.equal(user.getNick()))
				.fetch();

		if (records.isEmpty()) {
			return new ConcurrentHashMap<>();
		}

		Map<String, String> res = new HashMap<>();
		for (PreferencesRecord record : records) {
			res.put(record.getKey(), record.getValue());
		}

		return new ConcurrentHashMap<>(res);
	}

	void increaseNumberOfMessagesFor(String forumName, int increaseValue) {
		increaseNumber("messages.forum." + forumName, increaseValue);
	}

	void increaseNumberOfThreadsFor(String forumName, int increaseValue) {
		increaseNumber("threads.forum." + forumName, increaseValue);
	}

	void increaseTotalNumberOfMessagess() {
		increaseNumber("messages.total", 1);
	}

	void increaseTotalNumberOfThreads() {
		increaseNumber("threads.total", 1);
	}

	private void increaseNumber(String key, int increaseValue) {
		String val = getSysinfoValue(key);
		if (StringUtils.isEmpty(val)) {
			LOG.error("Nessuna entry in SYSINFO con la key '" + key + "' !");
			return;
		}
		int nr = Integer.parseInt(val);
		setSysinfoValue(key, Integer.toString(nr + increaseValue));
	}

	public String getSysinfoValue(String key) {
		Record1<String> record = jooq.select(SYSINFO.VALUE)
				.from(SYSINFO)
				.where(SYSINFO.KEY.equal(key))
				.fetchOne();

		if (record == null) {
			return null;
		}

		return record.getValue(SYSINFO.VALUE);
	}

	public void setSysinfoValue(String key, String value) {
		if (getSysinfoValue(key) == null) {
			// insert
			jooq.insertInto(SYSINFO)
			.set(SYSINFO.KEY, key)
			.set(SYSINFO.VALUE, value)
			.execute();
		} else {
			// update
			jooq.update(SYSINFO)
			.set(SYSINFO.VALUE, value)
			.where(SYSINFO.KEY.equal(key))
			.execute();
		}
	}

}
