package com.forumdeitroll.test.persistence;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.PrivateMsgDTO;
import com.forumdeitroll.persistence.PrivateMsgDTO.ToNickDetailsDTO;
import com.forumdeitroll.test.BaseTest;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PrivateMsgTest extends BaseTest {

	@Test
	public void test_getSentPvts() throws Exception {
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("admin");

		List<PrivateMsgDTO> messages = pvtDAO.getSentPvts(author, 99, 0);
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

		List<PrivateMsgDTO> messages = pvtDAO.getInbox(author, 99, 0);

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
		int inboxPages = pvtDAO.getInboxPages(author);
		assertEquals(0, inboxPages);
	}

	@Test
	public void test_getOutboxPages() {
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("admin");
		int inboxPages = pvtDAO.getOutboxPages(author);
		assertEquals(0, inboxPages);
	}

	@Test
	public void test_sendAPvtForGreatGoods() {
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("admin");

		String[] recipients = new String[] {
				"admin",
				"",
				"Sfigato"
		};

		PrivateMsgDTO privateMsg = new PrivateMsgDTO();
		privateMsg.setText("Nuovo messaggio");
		privateMsg.setSubject("Per tutti gli utenti coccolosi (love)");

		pvtDAO.sendAPvtForGreatGoods(author, privateMsg, recipients);

		List<PrivateMsgDTO> messages = pvtDAO.getInbox(author, 99, 0);
		assertEquals(3, messages.size());
		long newPvtId = messages.get(0).getId();
		PrivateMsgDTO newPvt = pvtDAO.getPvtDetails(newPvtId, author);
		assertEquals(privateMsg.getText(), newPvt.getText());
		assertEquals(privateMsg.getSubject(), newPvt.getSubject());

		author = new AuthorDTO(null);
		author.setNick("Sfigato");
		messages = pvtDAO.getInbox(author, 99, 0);
		assertEquals(2, messages.size());
		newPvtId = messages.get(0).getId();
		newPvt = pvtDAO.getPvtDetails(newPvtId, author);
		assertEquals(privateMsg.getText(), newPvt.getText());
		assertEquals(privateMsg.getSubject(), newPvt.getSubject());
	}

	@Test
	public void test_notifyRead() throws Exception {
		PrivateMsgDTO pvt = new PrivateMsgDTO();
		pvt.setId(2);
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("admin");

		pvtDAO.notifyRead(author, pvt);

		List<PrivateMsgDTO> messages = pvtDAO.getInbox(author, 99, 0);
		assertNotNull(messages);
		assertEquals(2, messages.size());

		pvt = messages.get(0);
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
		assertTrue(toNick.isRead());

	}

	@Test
	public void test_notifyUnread() throws Exception {
		PrivateMsgDTO pvt = new PrivateMsgDTO();
		pvt.setId(1);
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("Sfigato");

		pvtDAO.notifyUnread(author, pvt);

		List<PrivateMsgDTO> messages = pvtDAO.getInbox(author, 99, 0);
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

		boolean checkForNewPvts = pvtDAO.checkForNewPvts(author);
		assertFalse(checkForNewPvts);

		author = new AuthorDTO(null);
		author.setNick("admin");

		checkForNewPvts = pvtDAO.checkForNewPvts(author);
		assertTrue(checkForNewPvts);
	}

	@Test
	public void test_deletePvt() throws Exception {
		AuthorDTO authorAdmin = new AuthorDTO(null);
		authorAdmin.setNick("admin");
		AuthorDTO authorSfigato = new AuthorDTO(null);
		authorSfigato.setNick("Sfigato");

		List<PrivateMsgDTO> messages = pvtDAO.getInbox(authorAdmin, 99, 0);
		assertEquals(2, messages.size());

		pvtDAO.deletePvt(2, authorSfigato); // not allowed
		messages = pvtDAO.getInbox(authorAdmin, 99, 0);
		assertEquals(2, messages.size());

		pvtDAO.deletePvt(2, authorAdmin);
		messages = pvtDAO.getInbox(authorAdmin, 99, 0);
		assertEquals(1, messages.size());
	}

	@Test
	public void test_getPvtDetails() throws Exception {
		// pvt as sender
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("Sfigato");

		PrivateMsgDTO pvtSfigato = pvtDAO.getPvtDetails(2, author);
		assertNotNull(pvtSfigato);
		assertEquals(getDateFromDatabaseString("2014-08-29 13:10:50"), pvtSfigato.getDate());
		assertEquals("Sfigato", pvtSfigato.getFromNick());
		assertEquals(2, pvtSfigato.getId());
		assertEquals(0, pvtSfigato.getReplyTo());
		assertEquals("Re: Benvenuto fortunello !", pvtSfigato.getSubject());
		assertEquals("&gt; Fai il bravo ;) ...<br><br>OK :$", pvtSfigato.getText());
		assertNotNull(pvtSfigato.getToNick());
		List<ToNickDetailsDTO> toNicks = pvtSfigato.getToNick();
		assertNotNull(toNicks);
		assertEquals(1, toNicks.size());
		ToNickDetailsDTO toNick = toNicks.get(0);
		assertEquals("admin", toNick.getNick());
		assertFalse(toNick.isRead());

		// pvt as recipient
		author = new AuthorDTO(null);
		author.setNick("admin");

		PrivateMsgDTO pvtAdmin = pvtDAO.getPvtDetails(2, author);
		assertNotNull(pvtAdmin);
		assertEquals(pvtSfigato.getDate(), pvtAdmin.getDate());
		assertEquals(pvtSfigato.getFromNick(), pvtAdmin.getFromNick());
		assertEquals(pvtSfigato.getId(), pvtAdmin.getId());
		assertEquals(pvtSfigato.getReplyTo(), pvtAdmin.getReplyTo());
		assertEquals(pvtSfigato.getSubject(), pvtAdmin.getSubject());
		assertEquals(pvtSfigato.getText(), pvtAdmin.getText());
		assertNotNull(pvtAdmin.getToNick());
		toNicks = pvtAdmin.getToNick();
		assertNotNull(toNicks);
		assertEquals(1, toNicks.size());
		toNick = toNicks.get(0);
		assertEquals("admin", toNick.getNick());
		assertFalse(toNick.isRead());
	}

}
