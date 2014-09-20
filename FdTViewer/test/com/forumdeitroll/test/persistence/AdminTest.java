package com.forumdeitroll.test.persistence;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.forumdeitroll.persistence.AdDTO;
import com.forumdeitroll.persistence.MessageDTO;

public class AdminTest extends BaseTest {

	@Test
	public void test_moveThreadTree() {
		MessageDTO message = persistence.getMessage(8);
		Assert.assertNull(message.getForum());

		int msgInSourceForum = persistence.getMessages("", null, 99, 0, null).getMaxNrOfMessages();
		int msgInDestinationForum = persistence.getMessages("Procura Svizzera", null, 99, 0, null).getMaxNrOfMessages();

		persistence.moveThreadTree(message.getId(), "Procura Svizzera");
		MessageDTO movedMessage = persistence.getMessage(8);

		Assert.assertEquals("Procura Svizzera", movedMessage.getForum());
		Assert.assertEquals(message.getId(), movedMessage.getId());
		Assert.assertEquals(message.getLastId(), movedMessage.getLastId());
		Assert.assertEquals(message.getLastId(), movedMessage.getLastId());
		Assert.assertEquals(message.getSubject(), movedMessage.getSubject());
		Assert.assertEquals(message.getText(), movedMessage.getText());
		Assert.assertEquals(message.getId(), movedMessage.getThreadId());
		Assert.assertEquals(message.getId(), movedMessage.getParentId());

		int msgInSourceForumAfter = persistence.getMessages("", null, 99, 0, null).getMaxNrOfMessages();
		Assert.assertEquals(msgInSourceForum - 2, msgInSourceForumAfter);

		int msgInDestinationForumAfter = persistence.getMessages("Procura Svizzera", null, 99, 0, null).getMaxNrOfMessages();
		Assert.assertEquals(msgInDestinationForum + 2, msgInDestinationForumAfter);
	}

	@Test
	public void test_restoreOrHideMessage() {
		MessageDTO message = persistence.getMessage(4);
		Assert.assertTrue(message.isVisible());

		persistence.restoreOrHideMessage(message.getId(), 0);
		message = persistence.getMessage(message.getId());
		Assert.assertFalse(message.isVisible());

		persistence.restoreOrHideMessage(message.getId(), 1);
		message = persistence.getMessage(message.getId());
		Assert.assertTrue(message.isVisible());

	}

	@Test
	public void test_blockTorExitNodes() {
		Assert.assertFalse(persistence.blockTorExitNodes());
	}

	@Test
	public void test_getTitles() {
		List<String> titles = persistence.getTitles();
		Assert.assertEquals(3, titles.size());
		Assert.assertEquals("Forum di test", titles.get(0));
		Assert.assertEquals("Forum che funziona !", titles.get(1));
		Assert.assertEquals("IMMENSO GIAMBO !!!1!", titles.get(2));
	}

	@Test
	public void test_setTitles() {
		List<String> titles = persistence.getTitles();
		titles.remove(1);
		titles.add("Prot Quack Quack");

		persistence.setTitles(titles);
		List<String> newTitles = persistence.getTitles();
		Assert.assertArrayEquals(titles.toArray(), newTitles.toArray());
	}

	@Test
	public void test_getAllAds() {
		List<AdDTO> allAds = persistence.getAllAds();
		Assert.assertEquals(9, allAds.size());
	}

	@Test
	@Ignore("Implement me !")
	public void test_setAllAds() {
		// TODO
	}

}
