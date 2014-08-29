package com.forumdeitroll.test.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.PrivateMsgDTO;
import com.forumdeitroll.persistence.PrivateMsgDTO.ToNickDetailsDTO;

public class PrivateMsgTest extends BaseTest {

	@Test
	public void test_getSentPvts() throws Exception {
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("admin");

		List<PrivateMsgDTO> messages = persistence.getSentPvts(author, 99, 0);
		assertNotNull(messages);
		assertEquals(1, messages.size());

		PrivateMsgDTO pvt = messages.get(0);
		assertEquals(getDateFromDatabaseString("2014-08-29 13:02:43"), pvt.getDate());
		assertEquals(null, pvt.getFromNick());
		assertEquals(1, pvt.getId());
		assertEquals(0, pvt.getReplyTo());
		assertEquals("Benvenuto fortunello !", pvt.getSubject());
		assertEquals(null, pvt.getText());
		List<ToNickDetailsDTO> toNicks = pvt.getToNick();
		assertNotNull(toNicks);
		assertEquals(1, toNicks.size());
		ToNickDetailsDTO toNick = toNicks.get(0);
		assertEquals("Sfigato", toNick.getNick());
		assertTrue(toNick.isRead());
	}

	@Test
	public void test_getInbox() throws Exception {
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("admin");

		List<PrivateMsgDTO> messages = persistence.getInbox(author, 99, 0);

		assertNotNull(messages);
		assertEquals(2, messages.size());

		PrivateMsgDTO pvt = messages.get(0);
		assertEquals(getDateFromDatabaseString("2014-08-29 13:11:24"), pvt.getDate());
		assertEquals("Sfigato", pvt.getFromNick());
		assertEquals(3, pvt.getId());
		assertEquals(0, pvt.getReplyTo());
		assertEquals("Re: Benvenuto fortunello !", pvt.getSubject());
		assertEquals(null, pvt.getText());
		List<ToNickDetailsDTO> toNicks = pvt.getToNick();
		assertNotNull(toNicks);
		assertEquals(1, toNicks.size());
		ToNickDetailsDTO toNick = toNicks.get(0);
		assertEquals("admin", toNick.getNick());
		assertFalse(toNick.isRead());

		pvt = messages.get(1);
		assertEquals(getDateFromDatabaseString("2014-08-29 13:10:50"), pvt.getDate());
		assertEquals("Sfigato", pvt.getFromNick());
		assertEquals(2, pvt.getId());
		assertEquals(0, pvt.getReplyTo());
		assertEquals("Re: Benvenuto fortunello !", pvt.getSubject());
		assertEquals(null, pvt.getText());
		toNicks = pvt.getToNick();
		assertNotNull(toNicks);
		assertEquals(1, toNicks.size());
		toNick = toNicks.get(0);
		assertEquals("admin", toNick.getNick());
		assertFalse(toNick.isRead());
	}

	@Test
	public void test_getInboxPages() {
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("admin");
		int inboxPages = persistence.getInboxPages(author);
		assertEquals(0, inboxPages);
	}

	@Test
	public void test_getOutboxPages() {
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("admin");
		int inboxPages = persistence.getOutboxPages(author);
		assertEquals(0, inboxPages);
	}

	@Test
	public void test_sendAPvtForGreatGoods() {
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("admin");

		String[] recipients = new String[] {
				"admin",
				"Sfigato"
		};

		PrivateMsgDTO privateMsg = new PrivateMsgDTO();
		privateMsg.setText("Nuovo messaggio");
		privateMsg.setSubject("Per tutti gli utenti coccolosi (love)");

		persistence.sendAPvtForGreatGoods(author, privateMsg, recipients);

		List<PrivateMsgDTO> messages = persistence.getInbox(author, 99, 0);
		assertEquals(3, messages.size());
		long newPvtId = messages.get(0).getId();
		PrivateMsgDTO newPvt = persistence.getPvtDetails(newPvtId, author);
		assertEquals(privateMsg.getText(), newPvt.getText());
		assertEquals(privateMsg.getSubject(), newPvt.getSubject());

		author = new AuthorDTO(null);
		author.setNick("Sfigato");
		messages = persistence.getInbox(author, 99, 0);
		assertEquals(2, messages.size());
		newPvtId = messages.get(0).getId();
		newPvt = persistence.getPvtDetails(newPvtId, author);
		assertEquals(privateMsg.getText(), newPvt.getText());
		assertEquals(privateMsg.getSubject(), newPvt.getSubject());
	}

	@Test
	public void test_notifyRead() throws Exception {
		PrivateMsgDTO pvt = new PrivateMsgDTO();
		pvt.setId(1);
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("Sfigato");

		persistence.notifyRead(author, pvt);

		List<PrivateMsgDTO> messages = persistence.getInbox(author, 99, 0);
		assertNotNull(messages);
		assertEquals(1, messages.size());

		pvt = messages.get(0);
		assertEquals(getDateFromDatabaseString("2014-08-29 13:02:43"), pvt.getDate());
		assertEquals("admin", pvt.getFromNick());
		assertEquals(1, pvt.getId());
		assertEquals(0, pvt.getReplyTo());
		assertEquals("Benvenuto fortunello !", pvt.getSubject());
		assertEquals(null, pvt.getText());
		List<ToNickDetailsDTO> toNicks = pvt.getToNick();
		assertNotNull(toNicks);
		assertEquals(1, toNicks.size());
		ToNickDetailsDTO toNick = toNicks.get(0);
		assertEquals("Sfigato", toNick.getNick());
		assertTrue(toNick.isRead());

	}

	@Test
	public void test_notifyUnread() throws Exception {
		PrivateMsgDTO pvt = new PrivateMsgDTO();
		pvt.setId(1);
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("Sfigato");

		persistence.notifyUnread(author, pvt);

		List<PrivateMsgDTO> messages = persistence.getInbox(author, 99, 0);
		assertNotNull(messages);
		assertEquals(1, messages.size());

		pvt = messages.get(0);
		assertEquals(getDateFromDatabaseString("2014-08-29 13:02:43"), pvt.getDate());
		assertEquals("admin", pvt.getFromNick());
		assertEquals(1, pvt.getId());
		assertEquals(0, pvt.getReplyTo());
		assertEquals("Benvenuto fortunello !", pvt.getSubject());
		assertEquals(null, pvt.getText());
		List<ToNickDetailsDTO> toNicks = pvt.getToNick();
		assertNotNull(toNicks);
		assertEquals(1, toNicks.size());
		ToNickDetailsDTO toNick = toNicks.get(0);
		assertEquals("Sfigato", toNick.getNick());
		assertFalse(toNick.isRead());
	}

	@Test
	public void test_checkForNewPvts() {
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("Sfigato");

		boolean checkForNewPvts = persistence.checkForNewPvts(author);
		assertFalse(checkForNewPvts);

		author = new AuthorDTO(null);
		author.setNick("admin");

		checkForNewPvts = persistence.checkForNewPvts(author);
		assertTrue(checkForNewPvts);
	}

	@Test
	public void test_deletePvt() throws Exception {
		AuthorDTO authorAdmin = new AuthorDTO(null);
		authorAdmin.setNick("admin");
		AuthorDTO authorSfigato = new AuthorDTO(null);
		authorSfigato.setNick("Sfigato");

		List<PrivateMsgDTO> messages = persistence.getInbox(authorAdmin, 99, 0);
		assertEquals(2, messages.size());

		persistence.deletePvt(2, authorSfigato); // not allowed
		messages = persistence.getInbox(authorAdmin, 99, 0);
		assertEquals(2, messages.size());

		persistence.deletePvt(2, authorAdmin);
		messages = persistence.getInbox(authorAdmin, 99, 0);
		assertEquals(1, messages.size());
	}

	@Test
	public void test_getPvtDetails() throws Exception {
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("Sfigato");

		PrivateMsgDTO pvt = persistence.getPvtDetails(2, author);
		assertNotNull(pvt);
		assertEquals(getDateFromDatabaseString("2014-08-29 13:10:50"), pvt.getDate());
		assertEquals("Sfigato", pvt.getFromNick());
		assertEquals(2, pvt.getId());
		assertEquals(0, pvt.getReplyTo());
		assertEquals("Re: Benvenuto fortunello !", pvt.getSubject());
		assertEquals("&gt; Fai il bravo ;) ...<br><br>OK :$", pvt.getText());
		assertNotNull(pvt.getToNick());
		List<ToNickDetailsDTO> toNicks = pvt.getToNick();
		assertNotNull(toNicks);
		assertEquals(1, toNicks.size());
		ToNickDetailsDTO toNick = toNicks.get(0);
		assertEquals("admin", toNick.getNick());
		assertFalse(toNick.isRead());
	}

}
