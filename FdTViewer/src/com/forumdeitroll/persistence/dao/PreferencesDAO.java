package com.forumdeitroll.persistence.dao;


import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.jooq.tables.records.PreferencesRecord;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.forumdeitroll.persistence.jooq.Tables.PREFERENCES;

public class PreferencesDAO extends GenericDAO<PreferencesRecord, Map<String, String>> {

	public PreferencesDAO(DSLContext jooq) {
		super(jooq);
	}

	public Map<String, String> getPreferences(AuthorDTO user) {

		if (user == null || !user.isValid()) {
			return new ConcurrentHashMap<String, String>();
		}

		Result<PreferencesRecord> records = jooq.selectFrom(PREFERENCES)
				.where(PREFERENCES.NICK.equal(user.getNick()))
				.fetch();

		if (records == null) {
			return new ConcurrentHashMap<String, String>();
		}

		Map<String, String> res = new ConcurrentHashMap<String, String>();
		for (PreferencesRecord record : records) {
			res.putAll(recordToDto(record));
		}
		return res;
	}

	@Override
	protected Map<String, String> recordToDto(PreferencesRecord record) {

		if (record == null) {
			return new HashMap<String, String>();
		}

		Map<String, String> res = new HashMap<String, String>();
		res.put(record.getKey(), record.getValue());

		return res;
	}

	@Override
	protected PreferencesRecord dtoToRecord(Map<String, String> dto) {
		return null; // TODO
	}
}
