package com.forumdeitroll.persistence.dao;

import com.forumdeitroll.PasswordUtils;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.jooq.tables.records.AuthorsRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.forumdeitroll.persistence.jooq.Tables.AUTHORS;

public class AuthorsDAO extends GenericDAO<AuthorsRecord, AuthorDTO> {

	public AuthorsDAO(DSLContext jooq) {
		super(jooq);
	}

	public AuthorDTO getAuthor(String nick) {

		if (StringUtils.isEmpty(nick)) {
			return new AuthorDTO(null);
		}

		AuthorsRecord record = jooq.selectFrom(AUTHORS)
				.where(AUTHORS.NICK.upper().equal(nick.toUpperCase())).fetchAny();

		if (record == null) {
			return new AuthorDTO(null);
		}

		return recordToDto(record);
	}

	public List<AuthorDTO> getAuthors(boolean onlyActive) {

		Result<AuthorsRecord> records;
		if (onlyActive) {
			records = jooq.selectFrom(AUTHORS)
					.where(AUTHORS.HASH.isNotNull())
					.fetch();
		} else {
			records = jooq.selectFrom(AUTHORS)
					.fetch();
		}

		List<AuthorDTO> res = new ArrayList<AuthorDTO>(records.size());
		for (AuthorsRecord record : records) {
			res.add(recordToDto(record));
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
			.set(AUTHORS.NICK, authorDTO.getNick())
			.set(AUTHORS.PASSWORD, "") // <- campo "password", ospitava la vecchia "hash", non lo settiamo piu`
			.set(AUTHORS.MESSAGES, 0)
			.set(AUTHORS.SALT, authorDTO.getSalt())
			.set(AUTHORS.HASH, authorDTO.getHash())
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

	@Override
	protected AuthorDTO recordToDto(AuthorsRecord record) {
		AuthorDTO authorDTO = new AuthorDTO(null);
		authorDTO.setAvatar(record.getAvatar());
		authorDTO.setNick(record.getNick());
		authorDTO.setHash(record.getHash());
		authorDTO.setMessages(record.getMessages());
		authorDTO.setOldPassword(record.getPassword());
		authorDTO.setSalt(record.getSalt());
		authorDTO.setSignatureImage(record.getSignatureImage());
		return authorDTO;
	}

	@Override
	protected AuthorsRecord dtoToRecord(AuthorDTO dto) {
		return null; // TODO
	}
}
