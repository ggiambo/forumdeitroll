package com.forumdeitroll.test.persistence;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.servlets.User;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class AuthorTest extends BaseTest {

	@Test
	public void test_getAuthor() {
		AuthorDTO author = authorsDAO.getAuthor("admin");
		assertNotNull(author);
		assertEquals("admin", author.getNick());
		assertNull(author.getAvatar());
		assertEquals(2, author.getMessages());
		assertNull(author.getSignatureImage());
		Map<String, String> preferences = author.getPreferences();
		assertNull(preferences.get(User.PREF_AUTO_REFRESH));
		assertNull(preferences.get(User.PREF_BLOCK_HEADER));
		assertNull(preferences.get(User.PREF_COLLAPSE_QUOTES));
		assertNull(preferences.get(User.PREF_COMPACT_SIGNATURE));
		assertNull(preferences.get(User.PREF_EMBEDDYT));
		assertEquals("Procura Svizzera", preferences.get("hideForum.Procura Svizzera"));
		assertEquals("checked", preferences.get(User.PREF_HIDE_BANNERONE));
		assertNull(preferences.get(User.PREF_HIDE_FAKE_ADS));
		assertNull(preferences.get(User.PREF_HIDE_SIGNATURE));
		assertNull(preferences.get(User.PREF_LARGE_STYLE));
		assertNull(preferences.get(User.PREF_MSG_MAX_HEIGHT));
		assertNull(preferences.get(User.PREF_SHOWANONIMG));
		assertEquals("Classico", preferences.get(User.PREF_THEME));

		author = authorsDAO.getAuthor("Admin");
		assertNotNull(author);
		assertEquals("admin", author.getNick());
		assertNull(author.getAvatar());
		assertEquals(2, author.getMessages());
		assertNull(author.getSignatureImage());
		preferences = author.getPreferences();
		assertNull(preferences.get(User.PREF_AUTO_REFRESH));
		assertNull(preferences.get(User.PREF_BLOCK_HEADER));
		assertNull(preferences.get(User.PREF_COLLAPSE_QUOTES));
		assertNull(preferences.get(User.PREF_COMPACT_SIGNATURE));
		assertNull(preferences.get(User.PREF_EMBEDDYT));
		assertEquals("Procura Svizzera", preferences.get("hideForum.Procura Svizzera"));
		assertEquals("checked", preferences.get(User.PREF_HIDE_BANNERONE));
		assertNull(preferences.get(User.PREF_HIDE_FAKE_ADS));
		assertNull(preferences.get(User.PREF_HIDE_SIGNATURE));
		assertNull(preferences.get(User.PREF_LARGE_STYLE));
		assertNull(preferences.get(User.PREF_MSG_MAX_HEIGHT));
		assertNull(preferences.get(User.PREF_SHOWANONIMG));
		assertEquals("Classico", preferences.get(User.PREF_THEME));

		author = authorsDAO.getAuthor("Inesistente");
		assertNotNull(author);
		assertEquals(null, author.getNick());
		assertNull(author.getAvatar());
		assertEquals(-1, author.getMessages());
		assertEquals(0, author.getPreferences().size());
	}

	@Test
	public void test_getAuthors() {
		List<AuthorDTO> authors = authorsDAO.getAuthors(true);
		assertNotNull(authors);
		assertEquals(2, authors.size());

		// sort by nick
		Collections.sort(authors, new Comparator<AuthorDTO>() {
			public int compare(AuthorDTO a1, AuthorDTO a2) {
				return a1.getNick().compareTo(a2.getNick());
			}
		});

		AuthorDTO author = authors.get(0);
		assertNotNull(author);
		assertEquals("Sfigato", author.getNick());
		assertNull(author.getAvatar());
		assertEquals(1, author.getMessages());
		assertEquals(2, author.getPreferences().size());

		author = authors.get(1);
		assertNotNull(author);
		assertEquals("admin", author.getNick());
		assertNull(author.getAvatar());
		assertEquals(2, author.getMessages());
		assertEquals(5, author.getPreferences().size());
	}

	@Test
	public void test_updateAuthor() {
		AuthorDTO author = authorsDAO.getAuthor("admin");
		author.setMessages(42);
		author.setAvatar(new byte[] { 11, 12, 13, 14, 15 });
		author.setSignatureImage(new byte[] { 16, 17, 18, 19, 20 });
		authorsDAO.updateAuthor(author);

		author = authorsDAO.getAuthor("admin");
		assertEquals(new String(new byte[] { 11, 12, 13, 14, 15 }), new String(author.getAvatar()));
		assertEquals(42, author.getMessages());
		assertEquals("admin", author.getNick());
		assertEquals(new String(new byte[] { 16, 17, 18, 19, 20 }), new String(author.getSignatureImage()));
		Map<String, String> preferences = author.getPreferences();
		assertNull(preferences.get(User.PREF_AUTO_REFRESH));
		assertNull(preferences.get(User.PREF_BLOCK_HEADER));
		assertNull(preferences.get(User.PREF_COLLAPSE_QUOTES));
		assertNull(preferences.get(User.PREF_COMPACT_SIGNATURE));
		assertNull(preferences.get(User.PREF_EMBEDDYT));
		assertEquals("Procura Svizzera", preferences.get("hideForum.Procura Svizzera"));
		assertEquals("checked", preferences.get(User.PREF_HIDE_BANNERONE));
		assertNull(preferences.get(User.PREF_HIDE_FAKE_ADS));
		assertNull(preferences.get(User.PREF_HIDE_SIGNATURE));
		assertNull(preferences.get(User.PREF_LARGE_STYLE));
		assertNull(preferences.get(User.PREF_MSG_MAX_HEIGHT));
		assertNull(preferences.get(User.PREF_SHOWANONIMG));
		assertEquals("Classico", preferences.get(User.PREF_THEME));
	}

	@Test
	public void test_updateAuthorPassword() {
		AuthorDTO author = authorsDAO.getAuthor("admin");
		String oldHash = author.getHash();
		assertTrue(authorsDAO.updateAuthorPassword(author, "prooot"));

		AuthorDTO updatedAuthor = authorsDAO.getAuthor("admin");
		assertNotNull(updatedAuthor);
		assertThat(oldHash, not(updatedAuthor.getHash()));
		assertEquals(author.getHash(), updatedAuthor.getHash());
		assertEquals(author.getSalt(), updatedAuthor.getSalt());
		assertEquals("admin", updatedAuthor.getNick());
		assertNull(updatedAuthor.getAvatar());
		assertEquals(2, updatedAuthor.getMessages());
		assertNull(updatedAuthor.getSignatureImage());
		Map<String, String> preferences = updatedAuthor.getPreferences();
		assertNull(preferences.get(User.PREF_AUTO_REFRESH));
		assertNull(preferences.get(User.PREF_BLOCK_HEADER));
		assertNull(preferences.get(User.PREF_COLLAPSE_QUOTES));
		assertNull(preferences.get(User.PREF_COMPACT_SIGNATURE));
		assertNull(preferences.get(User.PREF_EMBEDDYT));
		assertEquals("Procura Svizzera", preferences.get("hideForum.Procura Svizzera"));
		assertEquals("checked", preferences.get(User.PREF_HIDE_BANNERONE));
		assertNull(preferences.get(User.PREF_HIDE_FAKE_ADS));
		assertNull(preferences.get(User.PREF_HIDE_SIGNATURE));
		assertNull(preferences.get(User.PREF_LARGE_STYLE));
		assertNull(preferences.get(User.PREF_MSG_MAX_HEIGHT));
		assertNull(preferences.get(User.PREF_SHOWANONIMG));
		assertEquals("Classico", preferences.get(User.PREF_THEME));
	}

	@Test
	public void test_registerUser() {
		AuthorDTO author = authorsDAO.registerUser("admin", "troll");
		assertNotNull(author);
		assertEquals(null, author.getNick());
		assertNull(author.getAvatar());
		assertEquals(-1, author.getMessages());
		assertEquals(0, author.getPreferences().size());

		author = authorsDAO.registerUser("Newtroll", "troll");
		assertNotNull(author);
		assertEquals("Newtroll", author.getNick());
		assertNull(author.getAvatar());
		assertEquals(0, author.getMessages());
		assertNull(author.getSignatureImage());
		assertEquals(0, author.getPreferences().size());
	}

	@Test
	public void test_getPreferences() {
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("admin");
		author.setMessages(99); // isValid()
		Map<String, String> preferences = authorsDAO.getPreferences(author);
		assertNull(preferences.get(User.PREF_AUTO_REFRESH));
		assertNull(preferences.get(User.PREF_BLOCK_HEADER));
		assertNull(preferences.get(User.PREF_COLLAPSE_QUOTES));
		assertNull(preferences.get(User.PREF_COMPACT_SIGNATURE));
		assertNull(preferences.get(User.PREF_EMBEDDYT));
		assertEquals("Procura Svizzera", preferences.get("hideForum.Procura Svizzera"));
		assertEquals("checked", preferences.get(User.PREF_HIDE_BANNERONE));
		assertNull(preferences.get(User.PREF_HIDE_FAKE_ADS));
		assertNull(preferences.get(User.PREF_HIDE_SIGNATURE));
		assertNull(preferences.get(User.PREF_LARGE_STYLE));
		assertNull(preferences.get(User.PREF_MSG_MAX_HEIGHT));
		assertNull(preferences.get(User.PREF_SHOWANONIMG));
		assertEquals("Classico", preferences.get(User.PREF_THEME));
	}

	@Test
	public void test_setPreference() {
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("admin");
		authorsDAO.setPreference(author, User.PREF_HIDE_FAKE_ADS, "yesss!!");

		author = authorsDAO.getAuthor("admin");
		assertNotNull(author);
		assertEquals("admin", author.getNick());
		assertNull(author.getAvatar());
		assertEquals(2, author.getMessages());
		assertNull(author.getSignatureImage());
		Map<String, String> preferences = author.getPreferences();
		assertNull(preferences.get(User.PREF_AUTO_REFRESH));
		assertNull(preferences.get(User.PREF_BLOCK_HEADER));
		assertNull(preferences.get(User.PREF_COLLAPSE_QUOTES));
		assertNull(preferences.get(User.PREF_COMPACT_SIGNATURE));
		assertNull(preferences.get(User.PREF_EMBEDDYT));
		assertEquals("Procura Svizzera", preferences.get("hideForum.Procura Svizzera"));
		assertEquals("checked", preferences.get(User.PREF_HIDE_BANNERONE));
		assertEquals("yesss!!", preferences.get(User.PREF_HIDE_FAKE_ADS));
		assertNull(preferences.get(User.PREF_HIDE_SIGNATURE));
		assertNull(preferences.get(User.PREF_LARGE_STYLE));
		assertNull(preferences.get(User.PREF_MSG_MAX_HEIGHT));
		assertNull(preferences.get(User.PREF_SHOWANONIMG));
		assertEquals("Classico", preferences.get(User.PREF_THEME));
	}

	@Test
	public void test_searchAuthor() {
		List<String> nicks = authorsDAO.searchAuthor("sfig");
		assertNotNull(nicks);
		assertEquals(0, nicks.size());

		nicks = authorsDAO.searchAuthor("Sfig");
		assertNotNull(nicks);
		assertEquals(1, nicks.size());
		assertEquals("Sfigato", nicks.get(0));
	}

	@Test
	public void test_setSysinfoValue() {
		authorsDAO.setSysinfoValue("newKey", "Prot Quack Burp");
		String value = authorsDAO.getSysinfoValue("newKey");
		assertNotNull(value);
		assertEquals("Prot Quack Burp", value);
	}

	@Test
	public void test_getSysinfoValue() {
		String value = authorsDAO.getSysinfoValue("newKey");
		assertNull(value);

		value = authorsDAO.getSysinfoValue("title.2");
		assertNotNull(value);
		assertEquals("IMMENSO GIAMBO !!!1!", value);
	}

	@Test
	public void test_getActiveAuthors() {
		List<AuthorDTO> authors = authorsDAO.getActiveAuthors();
		assertNotNull(authors);
		assertEquals(2, authors.size());
	}

}
