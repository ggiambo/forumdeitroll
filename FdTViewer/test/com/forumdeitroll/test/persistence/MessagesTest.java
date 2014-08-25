package com.forumdeitroll.test.persistence;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.MessagesDTO;

public class MessagesTest extends BaseTest {

	@Test
	public void test_insertMessage() {

		Date now = new Date();
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("Sfigato");

		MessageDTO newMsg = new MessageDTO();
		newMsg.setAuthor(author);
		newMsg.setDate(now);
		newMsg.setForum("");
		newMsg.setSubject("Test new message");
		newMsg.setText("Simple text");
		int nrOfMessages = persistence.getMessages("", author.getNick(), 99, 0, null).getMaxNrOfMessages();
		MessageDTO res = persistence.insertMessage(newMsg);
		assertNotNull(res.getAuthor());
		assertEquals(author.getNick(), res.getAuthor().getNick());
		assertEquals(now, res.getDate());
		assertEquals("", res.getForum());
		assertEquals(newMsg.getSubject(), res.getSubject());
		assertEquals(newMsg.getText(), res.getText());
		assertEquals(10, res.getId());
		assertEquals(0, res.getRank());
		assertEquals(10, res.getThreadId());
		assertEquals(res.getId(), res.getThreadId());
		assertEquals(newMsg.getRank(), res.getRank());
		assertNull(res.getTags());
		assertTrue(res.isVisible());
		int nrOfMessagesAfter = persistence.getMessages("", author.getNick(), 99, 0, null).getMaxNrOfMessages();
		assertEquals(nrOfMessages + 1, nrOfMessagesAfter);

		now = new Date();
		MessageDTO editMsg = res;
		editMsg.setDate(now);
		editMsg.setSubject("Test new message: Edited");
		editMsg.setText("Simple text: Edit");
		editMsg.setThreadId(res.getThreadId());
		nrOfMessages = persistence.getMessages("", author.getNick(), 99, 0, null).getMaxNrOfMessages();
		res = persistence.insertMessage(editMsg);
		assertNotNull(res.getAuthor());
		assertEquals(author.getNick(), res.getAuthor().getNick());
		assertEquals(now, editMsg.getDate()); // la data non cambia !
		assertEquals("", res.getForum());
		assertEquals(editMsg.getSubject(), res.getSubject());
		assertEquals(editMsg.getText(), res.getText());
		assertEquals(10, res.getId());
		assertEquals(0, res.getRank());
		assertEquals(10, res.getThreadId());
		assertEquals(res.getId(), res.getThreadId());
		assertEquals(editMsg.getRank(), res.getRank());
		assertNull(res.getTags());
		assertTrue(res.isVisible());
		nrOfMessagesAfter = persistence.getMessages("", author.getNick(), 99, 0, null).getMaxNrOfMessages();
		assertEquals(nrOfMessages, nrOfMessagesAfter);

		now = new Date();
		MessageDTO replyMsg = new MessageDTO();
		replyMsg.setAuthor(new AuthorDTO(null));
		replyMsg.setDate(now);
		replyMsg.setForum("");
		replyMsg.setParentId(res.getId());
		replyMsg.setSubject("Re: Test new message");
		replyMsg.setText("Simple text: A reply");
		replyMsg.setThreadId(res.getThreadId());
		nrOfMessages = persistence.getMessages("", author.getNick(), 99, 0, null).getMaxNrOfMessages();
		res = persistence.insertMessage(replyMsg);
		assertNotNull(res.getAuthor());
		assertNull(res.getAuthor().getNick());
		assertEquals(now, res.getDate());
		assertEquals("", res.getForum());
		assertEquals(replyMsg.getSubject(), res.getSubject());
		assertEquals(replyMsg.getText(), res.getText());
		assertEquals(11, res.getId());
		assertEquals(0, res.getRank());
		assertEquals(10, res.getThreadId());
		assertEquals(res.getParentId(), res.getThreadId());
		assertEquals(replyMsg.getRank(), res.getRank());
		assertNull(res.getTags());
		assertTrue(res.isVisible());
		nrOfMessagesAfter = persistence.getMessages("", author.getNick(), 99, 0, null).getMaxNrOfMessages();
		assertEquals(nrOfMessages + 1, nrOfMessagesAfter);

	}

	@Test
	public void test_getMessage() throws Exception {
		MessageDTO msg = persistence.getMessage(7);
		assertNull(msg.getForum());
		assertNotNull(msg.getAuthor());
		assertEquals("Sfigato", msg.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2014-08-16 14:54:11"), msg.getDate());
		assertEquals(7, msg.getId());
		assertEquals(7, msg.getParentId());
		assertEquals(1, msg.getRank());
		assertEquals("Ieri", msg.getSubject());
		assertNull(msg.getTags());
		assertEquals("Ho incontrato yoda. Che ragazzo fortunato :( ...", msg.getText());
		assertEquals(7, msg.getThreadId());
		assertTrue(msg.isVisible());

		msg = persistence.getMessage(5);
		assertEquals("Procura Svizzera", msg.getForum());
		assertNotNull(msg.getAuthor());
		assertEquals(null, msg.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-14 09:15:54"), msg.getDate());
		assertEquals(5, msg.getId());
		assertEquals(4, msg.getParentId());
		assertEquals(0, msg.getRank());
		assertEquals("Secondo !", msg.getSubject());
		assertNull(msg.getTags());
		assertEquals(":(", msg.getText());
		assertEquals(4, msg.getThreadId());
		assertTrue(msg.isVisible());
	}

	@Test
	public void test_getMessagesByThread() throws Exception {
		List<MessageDTO> msgs = persistence.getMessagesByThread(7);
		assertEquals(3, msgs.size());

		MessageDTO msg = msgs.get(0);
		assertEquals(null, msg.getForum());
		assertNotNull(msg.getAuthor());
		assertEquals("Sfigato", msg.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2014-08-16 14:54:11"), msg.getDate());
		assertEquals(7, msg.getId());
		assertEquals(7, msg.getParentId());
		assertEquals(1, msg.getRank());
		assertEquals("Ieri", msg.getSubject());
		assertNull(msg.getTags());
		assertEquals("Ho incontrato yoda. Che ragazzo fortunato :( ...", msg.getText());
		assertEquals(7, msg.getThreadId());
		assertTrue(msg.isVisible());

		msg = msgs.get(1);
		assertEquals(null, msg.getForum());
		assertNotNull(msg.getAuthor());
		assertEquals(null, msg.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2014-08-16 14:54:46"), msg.getDate());
		assertEquals(8, msg.getId());
		assertEquals(7, msg.getParentId());
		assertEquals(0, msg.getRank());
		assertEquals("Re: Ieri", msg.getSubject());
		assertNull(msg.getTags());
		assertEquals("Scritto da: Sfigato<BR>&gt; Ho incontrato yoda. Che ragazzo fortunato :( ...<BR><BR>Mi trovi un lavoro ?<BR><BR>- idyoda -", msg.getText());
		assertEquals(7, msg.getThreadId());
		assertTrue(msg.isVisible());

		msg = msgs.get(2);
		assertEquals(null, msg.getForum());
		assertNotNull(msg.getAuthor());
		assertEquals("admin", msg.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2014-08-16 14:55:14"), msg.getDate());
		assertEquals(9, msg.getId());
		assertEquals(8, msg.getParentId());
		assertEquals(0, msg.getRank());
		assertEquals("Re: Ieri", msg.getSubject());
		assertNull(msg.getTags());
		assertEquals("Scritto da: <BR>&gt; Scritto da: Sfigato<BR>&gt; &gt; Ho incontrato yoda. Che ragazzo fortunato :( ...<BR>&gt; <BR>&gt; Mi trovi un lavoro ?<BR>&gt; <BR>&gt; - idyoda -<BR><BR>(rotfl)(rotfl)", msg.getText());
		assertEquals(7, msg.getThreadId());
		assertTrue(msg.isVisible());

	}

	@Test
	public void test_searchMessages() {
		// Search implementato con il motorino scassato di Sarru
	}

	@Test
	public void test_getMessageTitle() {
		String title = persistence.getMessageTitle(8);
		assertEquals("Re: Ieri", title);
	}

	@Test
	public void test_getMessages() throws Exception {
		MessagesDTO msgs = persistence.getMessages("Procura Svizzera", null, 99, 0, null);
		assertNotNull(msgs);
		assertNotNull(msgs.getMessages());
		assertEquals(2, msgs.getMessages().size());

		MessageDTO msg = msgs.getMessages().get(0);
		assertEquals("Procura Svizzera", msg.getForum());
		assertNotNull(msg.getAuthor());
		assertEquals(null, msg.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-14 09:15:54"), msg.getDate());
		assertEquals(5, msg.getId());
		assertEquals(4, msg.getParentId());
		assertEquals(0, msg.getRank());
		assertEquals("Secondo !", msg.getSubject());
		assertNull(msg.getTags());
		assertEquals(":(", msg.getText());
		assertEquals(4, msg.getThreadId());
		assertTrue(msg.isVisible());

		msg = msgs.getMessages().get(1);
		assertEquals("Procura Svizzera", msg.getForum());
		assertNotNull(msg.getAuthor());
		assertEquals(null, msg.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-13 18:03:12"), msg.getDate());
		assertEquals(4, msg.getId());
		assertEquals(4, msg.getParentId());
		assertEquals(0, msg.getRank());
		assertEquals("Primo !", msg.getSubject());
		assertNull(msg.getTags());
		assertEquals("Blah Banf montanari orologio a cucu", msg.getText());
		assertEquals(4, msg.getThreadId());
		assertTrue(msg.isVisible());
	}

	@Test
	public void test_getMessagesByTag() throws Exception {
		MessagesDTO msgs = persistence.getMessagesByTag(99, 0, 1, null);
		assertNotNull(msgs);
		assertNotNull(msgs.getMessages());
		assertEquals(2, msgs.getMessages().size());

		MessageDTO msg = msgs.getMessages().get(0);
		assertEquals("Procura Svizzera", msg.getForum());
		assertNotNull(msg.getAuthor());
		assertEquals(null, msg.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-13 18:03:12"), msg.getDate());
		assertEquals(4, msg.getId());
		assertEquals(4, msg.getParentId());
		assertEquals(0, msg.getRank());
		assertEquals("Primo !", msg.getSubject());
		assertNull(msg.getTags());
		assertEquals("Blah Banf montanari orologio a cucu", msg.getText());
		assertEquals(4, msg.getThreadId());
		assertTrue(msg.isVisible());

		msg = msgs.getMessages().get(1);
		assertEquals("Forum iniziale", msg.getForum());
		assertNotNull(msg.getAuthor());
		assertEquals("admin", msg.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-13 14:30:15"), msg.getDate());
		assertEquals(1, msg.getId());
		assertEquals(1, msg.getParentId());
		assertEquals(0, msg.getRank());
		assertEquals("benvenuto nel fdt !", msg.getSubject());
		assertNull(msg.getTags());
		assertEquals("qui comando io $cool", msg.getText());
		assertEquals(1, msg.getThreadId());
		assertTrue(msg.isVisible());

	}
}
