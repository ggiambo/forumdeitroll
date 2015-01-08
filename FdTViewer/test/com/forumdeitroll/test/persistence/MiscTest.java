package com.forumdeitroll.test.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.MessagesDTO;
import com.forumdeitroll.persistence.NotificationDTO;
import com.forumdeitroll.persistence.TagDTO;

public class MiscTest extends BaseTest {

	@Test
	public void test_getForums() {
		List<String> forums = persistence.getForums();
		assertNotNull(forums);
		assertEquals(2, forums.size());

		String forum = forums.get(0);
		assertEquals("Forum iniziale", forum);

		forum = forums.get(1);
		assertEquals("Procura Svizzera", forum);
	}

	@Test
	public void test_getNotifications() {
		List<NotificationDTO> notifications = persistence.getNotifications("admin", "Sfigato");
		assertNotNull(notifications);
		assertEquals(2, notifications.size());

		NotificationDTO notification = notifications.get(0);
		assertEquals("admin", notification.getFromNick());
		assertEquals(1, notification.getId());
		assertEquals(1, notification.getMsgId());
		assertEquals("Sfigato", notification.getToNick());

		notification = notifications.get(1);
		assertEquals("admin", notification.getFromNick());
		assertEquals(3, notification.getId());
		assertEquals(9, notification.getMsgId());
		assertEquals("Sfigato", notification.getToNick());

		notifications = persistence.getNotifications("Sfigato", "admin");
		assertNotNull(notifications);
		assertEquals(1, notifications.size());

		notification = notifications.get(0);
		assertEquals("Sfigato", notification.getFromNick());
		assertEquals(2, notification.getId());
		assertEquals(7, notification.getMsgId());
		assertEquals("admin", notification.getToNick());

		notifications = persistence.getNotifications("Sfigato", "Admin");
		assertNotNull(notifications);
		assertEquals(0, notifications.size());

	}

	@Test
	public void test_removeNotification() {
		persistence.removeNotification("admin", "Sfigato", 1);

		List<NotificationDTO> notifications = persistence.getNotifications("admin", "Sfigato");
		assertNotNull(notifications);
		assertEquals(1, notifications.size());

		NotificationDTO notification = notifications.get(0);
		assertEquals("admin", notification.getFromNick());
		assertEquals(3, notification.getId());
		assertEquals(9, notification.getMsgId());
		assertEquals("Sfigato", notification.getToNick());
	}

	@Test
	public void test_createNotification() {
		persistence.createNotification("Sfigato", "admin", 4);

		List<NotificationDTO> notifications = persistence.getNotifications("Sfigato", "admin");
		assertNotNull(notifications);
		assertEquals(2, notifications.size());

		NotificationDTO notification = notifications.get(0);
		assertEquals("Sfigato", notification.getFromNick());
		assertEquals(2, notification.getId());
		assertEquals(7, notification.getMsgId());
		assertEquals("admin", notification.getToNick());

		notification = notifications.get(1);
		assertEquals("Sfigato", notification.getFromNick());
		assertEquals(4, notification.getId());
		assertEquals(4, notification.getMsgId());
		assertEquals("admin", notification.getToNick());
	}

	@Test
	public void test_getLastId() {
		assertEquals(9, persistence.getLastId());
	}

	@Test
	public void test_like() {
		MessageDTO msg = persistence.getMessage(9);
		assertEquals(0, msg.getRank());

		// like: +1
		assertEquals(1, persistence.like(9, "Sfigato", true));
		msg = persistence.getMessage(9);
		assertEquals(1, msg.getRank());
		// not allowed
		assertEquals(0, persistence.like(9, "Sfigato", true));
		msg = persistence.getMessage(9);
		assertEquals(1, msg.getRank());
		// unlike: -1
		assertEquals(-2, persistence.like(9, "Sfigato", false));
		msg = persistence.getMessage(9);
		assertEquals(-1, msg.getRank());
		// not allowed
		assertEquals(0, persistence.like(9, "Sfigato", false));
		msg = persistence.getMessage(9);
		assertEquals(-1, msg.getRank());
		// like: +1
		assertEquals(2, persistence.like(9, "Sfigato", true));
		msg = persistence.getMessage(9);
		assertEquals(1, msg.getRank());

		// like: +2
		assertEquals(1, persistence.like(9, "admin", true));
		msg = persistence.getMessage(9);
		assertEquals(2, msg.getRank());
		// not allowed
		assertEquals(0, persistence.like(9, "admin", true));
		msg = persistence.getMessage(9);
		assertEquals(2, msg.getRank());
		// unlike: 0
		assertEquals(-2, persistence.like(9, "admin", false));
		msg = persistence.getMessage(9);
		assertEquals(0, msg.getRank());
		// not allowed
		assertEquals(0, persistence.like(9, "admin", false));
		msg = persistence.getMessage(9);
		assertEquals(0, msg.getRank());
		// like: +2
		assertEquals(2, persistence.like(9, "admin", true));
		msg = persistence.getMessage(9);
		assertEquals(2, msg.getRank());

	}

	@Test
	@Ignore("Implement me !")
	public void test_getReadersDigest() {
		// TODO
	}

	@Test
	public void test_addTag() {

		TagDTO tag = new TagDTO();
		tag.setAuthor("Sfigato");
		tag.setM_id(9);
		tag.setValue("Bah banf");

		persistence.addTag(tag);
		assertEquals(tag.getT_id(), 3);

		MessagesDTO messages = new MessagesDTO();
		MessageDTO msg = persistence.getMessage(tag.getM_id());
		messages.getMessages().add(msg);
		persistence.getTags(messages);
		List<MessageDTO> messgesList = messages.getMessages();
		assertEquals(1, messgesList.size());
		ArrayList<TagDTO> tags = messgesList.get(0).getTags();
		assertEquals(2, tags.size());
		TagDTO newTag = tags.get(1);
		assertEquals(tag.getAuthor(), newTag.getAuthor());
		assertEquals(tag.getM_id(), newTag.getM_id());
		assertEquals(tag.getT_id(), newTag.getT_id());
		assertEquals(tag.getValue(), newTag.getValue());
	}

	@Test
	public void test_deleTag() {
		MessagesDTO messages = new MessagesDTO();
		MessageDTO msg = persistence.getMessage(1);
		messages.getMessages().add(msg);
		persistence.getTags(messages);

		persistence.deleTag(msg.getTags().get(0), true);

		messages = new MessagesDTO();
		msg = persistence.getMessage(1);
		messages.getMessages().add(msg);
		persistence.getTags(messages);
		assertNull(messages.getMessages().get(0).getTags());
	}

	@Test
	public void test_getTags() {
		MessagesDTO messages = new MessagesDTO();

		MessageDTO msg = persistence.getMessage(1);
		messages.getMessages().add(msg);
		msg = persistence.getMessage(7);
		messages.getMessages().add(msg);
		msg = persistence.getMessage(9);
		messages.getMessages().add(msg);

		persistence.getTags(messages);

		msg = messages.getMessages().get(0);
		List<TagDTO> tags = msg.getTags();
		assertNotNull(tags);
		assertEquals(1, tags.size());
		TagDTO tag = tags.get(0);
		assertEquals("Sfigato", tag.getAuthor());
		assertEquals(1, tag.getM_id());
		assertEquals(1, tag.getT_id());
		assertEquals("cazzata",tag.getValue());

		msg = messages.getMessages().get(1);
		tags = msg.getTags();
		assertNull(tags);

		msg = messages.getMessages().get(2);
		tags = msg.getTags();
		assertNotNull(tags);
		assertEquals(1, tags.size());
		tag = tags.get(0);
		assertEquals("admin", tag.getAuthor());
		assertEquals(9, tag.getM_id());
		assertEquals(2, tag.getT_id());
		assertEquals("idyoda",tag.getValue());
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

}
