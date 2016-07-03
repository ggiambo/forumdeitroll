package com.forumdeitroll.persistence.dao;

import static com.forumdeitroll.persistence.jooq.Tables.AUTHORS;
import static com.forumdeitroll.persistence.jooq.Tables.PREFERENCES;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;

import com.forumdeitroll.PasswordUtils;
import com.forumdeitroll.persistence.AuthorDTO;

public class AuthorsDAO extends BaseDAO {

	public AuthorsDAO(DSLContext jooq) {
		super(jooq);
	}

	public List<AuthorDTO> getActiveAuthors() {
		Result<Record2<String, Integer>> result =
			jooq.select(AUTHORS.NICK, AUTHORS.MESSAGES)
			.from(AUTHORS)
			.where(AUTHORS.MESSAGES.greaterThan(0))
			.orderBy(AUTHORS.MESSAGES.desc())
			.fetch();
		List<AuthorDTO> authors = new ArrayList<AuthorDTO>();
		for (Record2<String, Integer> record : result) {
			AuthorDTO author = new AuthorDTO(null);
			author.setNick(record.value1());
			author.setMessages(record.value2());
			authors.add(author);
		}
		return authors;
	}

	public List<AuthorDTO> getAuthors(boolean onlyActive) {

		List<String> nicks;
		if (onlyActive) {
			nicks = jooq.select(AUTHORS.HASH, AUTHORS.NICK)
					.from(AUTHORS)
					.where(AUTHORS.HASH.isNotNull())
					.fetch(AUTHORS.NICK);
		} else {
			nicks = jooq.select(AUTHORS.NICK)
					.from(AUTHORS)
					.fetch(AUTHORS.NICK);
		}

		List<AuthorDTO> res = new ArrayList<AuthorDTO>(nicks.size());
		for (String nick : nicks) {
			res.add(getAuthor(nick));
		}

		return res;
	}

	public AuthorDTO registerUser(String nick, String password) {

		// check se esiste gia'. Blah banf transazioni chissenefrega <-- (complimenti a chi ha scritto questo - sarrusofono)
		if (getAuthor(nick).isValid()) {
			return new AuthorDTO(null);
		}

		AuthorDTO authorDTO = new AuthorDTO(null);
		PasswordUtils.changePassword(authorDTO, password);

		jooq.insertInto(AUTHORS)
				.set(AUTHORS.NICK, nick)
				.set(AUTHORS.PASSWORD, "") // <- campo "password", ospitava la vecchia "hash", non lo settiamo piu`
				.set(AUTHORS.MESSAGES, 0)
				.set(AUTHORS.SALT, authorDTO.getSalt())
				.set(AUTHORS.HASH, authorDTO.getHash())
				.set(AUTHORS.CREATIONDATE, new Date(System.currentTimeMillis()))
				.set(AUTHORS.ENABLED, (byte)0)
				.execute();

		return getAuthor(nick);

	}

	public void updateAuthor(AuthorDTO authorDTO) {

		jooq.update(AUTHORS)
				.set(AUTHORS.MESSAGES, authorDTO.getMessages())
				.set(AUTHORS.AVATAR, authorDTO.getAvatar())
				.set(AUTHORS.SIGNATURE_IMAGE, authorDTO.getSignatureImage())
				.where(AUTHORS.NICK.equal(authorDTO.getNick()))
				.execute();

	}

	public boolean updateAuthorPassword(AuthorDTO authorDTO, String newPassword) {

		PasswordUtils.changePassword(authorDTO, newPassword);

		int res = jooq.update(AUTHORS)
				.set(AUTHORS.PASSWORD, "")
				.set(AUTHORS.SALT, authorDTO.getSalt())
				.set(AUTHORS.HASH, authorDTO.getHash())
				.where(AUTHORS.NICK.equal(authorDTO.getNick()))
				.execute();

		return res == 1;
	}

	public Map<String, String> setPreference(AuthorDTO user, String key, String value) {
		if (!getPreferences(user).keySet().contains(key)) {
			jooq.insertInto(PREFERENCES)
				.set(PREFERENCES.NICK, user.getNick())
				.set(PREFERENCES.KEY, key)
				.set(PREFERENCES.VALUE, value)
				.execute();
		} else {
			jooq.update(PREFERENCES)
				.set(PREFERENCES.VALUE, value)
				.where(PREFERENCES.NICK.eq(user.getNick()))
				.and(PREFERENCES.KEY.eq(key))
				.execute();
		}
		return getPreferences(user);
	}

	public List<String> getHiddenForums(AuthorDTO loggedUser) {
		List<String> hiddenForums = new ArrayList<String>();
		Map<String, String> prefs = getPreferences(loggedUser);
		for (Map.Entry<String, String> pref : prefs.entrySet()) {
			if (pref.getKey().startsWith("hideForum.")) {
				hiddenForums.add(pref.getValue());
			}
		}
		return hiddenForums;
	}

	public List<String> searchAuthor(String searchString) {
		List<String> res = new ArrayList<String>();
		Result<Record1<String>> records = jooq.select(AUTHORS.NICK)
			.from(AUTHORS)
			.where(AUTHORS.NICK.like(searchString + "%"))
			.and(AUTHORS.HASH.isNotNull())
			.orderBy(AUTHORS.NICK.asc())
			.fetch();

		for (Record1<String> record : records) {
			res.add(record.getValue(AUTHORS.NICK));
		}
		return res;
	}

	public void setHiddenForums(AuthorDTO loggedUser, List<String> hiddenForums) {
			// remove all
		jooq.delete(PREFERENCES)
			.where(PREFERENCES.KEY.like("hideForum.%"))
			.and(PREFERENCES.NICK.eq(loggedUser.getNick()))
			.execute();
			// insert all
			for (String hiddenForum : hiddenForums) {
				jooq.insertInto(PREFERENCES)
					.set(PREFERENCES.NICK, loggedUser.getNick())
					.set(PREFERENCES.KEY, "hideForum." + hiddenForum)
					.set(PREFERENCES.VALUE, hiddenForum)
					.execute();
			}
	}

	public void enableUser(AuthorDTO loggedUser, boolean isEnabled) {
		byte enabled = isEnabled ? (byte)1 : (byte)0;
		jooq.update(AUTHORS)
				.set(AUTHORS.ENABLED, enabled)
				.where(AUTHORS.NICK.eq(loggedUser.getNick()))
				.execute();
	}

}
