package com.forumdeitroll.test.persistence;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.servlets.User;

public class AuthorTest extends BaseTest {

	@Test
	public void test_getAuthor() {
		AuthorDTO author = persistence.getAuthor("admin");
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

		author = persistence.getAuthor("Admin");
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

		author = persistence.getAuthor("Inesistente");
		assertNotNull(author);
		assertEquals(null, author.getNick());
		assertNull(author.getAvatar());
		assertEquals(-1, author.getMessages());
		assertEquals(0, author.getPreferences().size());
	}

	@Test
	public void test_getAuthors() {
		List<AuthorDTO> authors = persistence.getAuthors(true);
		assertNotNull(authors);
		assertEquals(2, authors.size());

		// sort by nick
		Collections.sort(authors, new Comparator<AuthorDTO>() {
			@Override
			public int compare(AuthorDTO a1, AuthorDTO a2) {
				return a1.getNick().compareTo(a2.getNick());
			}
		});

		AuthorDTO author = authors.get(0);
		assertNotNull(author);
		assertEquals("Sfigato", author.getNick());
		assertNull(author.getAvatar());
		assertEquals(1, author.getMessages());
		assertEquals(0, author.getPreferences().size());

		author = authors.get(1);
		assertNotNull(author);
		assertEquals("admin", author.getNick());
		assertNull(author.getAvatar());
		assertEquals(2, author.getMessages());
		assertEquals(0, author.getPreferences().size());
	}

	@Test
	public void test_updateAuthor() {
		AuthorDTO author = persistence.getAuthor("admin");
		author.setMessages(42);
		author.setAvatar(new byte[] { 11, 12, 13, 14, 15 });
		author.setSignatureImage(new byte[] { 16, 17, 18, 19, 20 });
		persistence.updateAuthor(author);

		author = persistence.getAuthor("admin");
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
		AuthorDTO author = persistence.getAuthor("admin");
		String oldHash = author.getHash();
		assertTrue(persistence.updateAuthorPassword(author, "prooot"));

		AuthorDTO updatedAuthor = persistence.getAuthor("admin");
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
		AuthorDTO author = persistence.registerUser("admin", "troll");
		assertNotNull(author);
		assertEquals(null, author.getNick());
		assertNull(author.getAvatar());
		assertEquals(-1, author.getMessages());
		assertEquals(0, author.getPreferences().size());

		author = persistence.registerUser("Newtroll", "troll");
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
		Map<String, String> preferences = persistence.getPreferences(author);
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
		persistence.setPreference(author, User.PREF_HIDE_FAKE_ADS, "yesss!!");

		author = persistence.getAuthor("admin");
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
		List<String> nicks = persistence.searchAuthor("sfig");
		assertNotNull(nicks);
		assertEquals(0, nicks.size());

		nicks = persistence.searchAuthor("Sfig");
		assertNotNull(nicks);
		assertEquals(1, nicks.size());
		assertEquals("Sfigato", nicks.get(0));
	}

	@Test
	public void test_setHiddenForums() {
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("admin");
		author.setMessages(99); // isValid()
		persistence.setHiddenForums(author, Arrays.asList("Perbacco", "Accidenti", "Merdaaahhh!!"));
		List<String> hiddenForums = persistence.getHiddenForums(author);
		assertNotNull(hiddenForums);
		assertEquals(3, hiddenForums.size());
		assertTrue(hiddenForums.contains("Accidenti"));
		assertTrue(hiddenForums.contains("Perbacco"));
		assertTrue(hiddenForums.contains("Merdaaahhh!!"));

		author.setNick("Sfigato");
		author.setMessages(99); // isValid()
		persistence.setHiddenForums(author, Arrays.asList("Numeri del Lotto"));
		hiddenForums = persistence.getHiddenForums(author);
		assertNotNull(hiddenForums);
		assertEquals(1, hiddenForums.size());
		assertTrue(hiddenForums.contains("Numeri del Lotto"));

		author.setNick("Inesistente");
		author.setMessages(99); // isValid()
		persistence.setHiddenForums(author, Arrays.asList("Numeri del Lotto"));
		hiddenForums = persistence.getHiddenForums(author);
		assertNotNull(hiddenForums);
		assertEquals(1, hiddenForums.size());
		assertTrue(hiddenForums.contains("Numeri del Lotto"));
	}

	@Test
	public void test_getHiddenForums() {
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("admin");
		author.setMessages(99); // isValid()
		List<String> hiddenForums = persistence.getHiddenForums(author);
		assertNotNull(hiddenForums);
		assertEquals(1, hiddenForums.size());
		assertEquals("Procura Svizzera", hiddenForums.get(0));

		author.setNick("Admin");
		author.setMessages(99); // isValid()
		hiddenForums = persistence.getHiddenForums(author);
		assertNotNull(hiddenForums);
		assertEquals(0, hiddenForums.size());

		author.setNick("Inesistente");
		author.setMessages(99); // isValid()
		hiddenForums = persistence.getHiddenForums(author);
		assertNotNull(hiddenForums);
		assertEquals(0, hiddenForums.size());
	}

	@Test
	public void test_setSysinfoValue() {
		persistence.setSysinfoValue("newKey", "Prot Quack Burp");
		String value = persistence.getSysinfoValue("newKey");
		assertNotNull(value);
		assertEquals("Prot Quack Burp", value);
	}

	@Test
	public void test_getSysinfoValue() {
		String value = persistence.getSysinfoValue("newKey");
		assertNull(value);

		value = persistence.getSysinfoValue("title.2");
		assertNotNull(value);
		assertEquals("IMMENSO GIAMBO !!!1!", value);
	}

}
