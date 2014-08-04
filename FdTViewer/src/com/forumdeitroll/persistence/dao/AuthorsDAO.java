package com.forumdeitroll.persistence.dao;

import com.forumdeitroll.PasswordUtils;
import com.forumdeitroll.persistence.AuthorDTO;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.List;

import static com.forumdeitroll.persistence.jooq.Tables.AUTHORS;

public class AuthorsDAO extends BaseDAO {

	public AuthorsDAO(DSLContext jooq) {
		super(jooq);
	}

	public List<AuthorDTO> getAuthors(boolean onlyActive) {

		List<String> nicks;
		if (onlyActive) {
			nicks = jooq.select(AUTHORS.HASH, AUTHORS.NICK)
					.where(AUTHORS.HASH.isNotNull())
					.fetch(AUTHORS.NICK);
		} else {
			nicks = jooq.select(AUTHORS.NICK)
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

}
