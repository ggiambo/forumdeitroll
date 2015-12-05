package com.forumdeitroll.test.persistence;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.forumdeitroll.persistence.AdDTO;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.test.BaseTest;

public class AdminTest extends BaseTest {

	@Test
	public void test_moveThreadTree() {
		MessageDTO message = messagesDAO.getMessage(8);
		Assert.assertNull(message.getForum());

		int msgInSourceForum = messagesDAO.getMessages("", null, 99, 0, null).getMaxNrOfMessages();
		int msgInDestinationForum = messagesDAO.getMessages("Procura Svizzera", null, 99, 0, null).getMaxNrOfMessages();

        adminDAO.moveThreadTree(message, "Procura Svizzera");
		MessageDTO movedMessage = messagesDAO.getMessage(8);

		Assert.assertEquals("Procura Svizzera", movedMessage.getForum());
		Assert.assertEquals(message.getId(), movedMessage.getId());
		Assert.assertEquals(message.getLastId(), movedMessage.getLastId());
		Assert.assertEquals(message.getLastId(), movedMessage.getLastId());
		Assert.assertEquals(message.getSubject(), movedMessage.getSubject());
		Assert.assertEquals(message.getText(), movedMessage.getText());
		Assert.assertEquals(message.getId(), movedMessage.getThreadId());
		Assert.assertEquals(message.getId(), movedMessage.getParentId());

		int msgInSourceForumAfter = messagesDAO.getMessages("", null, 99, 0, null).getMaxNrOfMessages();
		Assert.assertEquals(msgInSourceForum - 2, msgInSourceForumAfter);

		int msgInDestinationForumAfter = messagesDAO.getMessages("Procura Svizzera", null, 99, 0, null).getMaxNrOfMessages();
		Assert.assertEquals(msgInDestinationForum + 2, msgInDestinationForumAfter);
	}

	@Test
	public void test_restoreOrHideMessage() {
		MessageDTO message = messagesDAO.getMessage(4);
		Assert.assertTrue(message.isVisible());

        adminDAO.restoreOrHideMessage(message.getId(), 0);
		message = messagesDAO.getMessage(message.getId());
		Assert.assertFalse(message.isVisible());

        adminDAO.restoreOrHideMessage(message.getId(), 1);
		message = messagesDAO.getMessage(message.getId());
		Assert.assertTrue(message.isVisible());

	}

	@Test
	public void test_blockTorExitNodes() {
		Assert.assertFalse(adminDAO.blockTorExitNodes());
	}

	@Test
	public void test_getTitles() {
		List<String> titles = adminDAO.getTitles();
		Assert.assertEquals(3, titles.size());
		Assert.assertEquals("Forum di test", titles.get(0));
		Assert.assertEquals("Forum che funziona !", titles.get(1));
		Assert.assertEquals("IMMENSO GIAMBO !!!1!", titles.get(2));
	}

	@Test
	public void test_setTitles() {
		List<String> titles = adminDAO.getTitles();
		titles.remove(1);
		titles.add("Prot Quack Quack");

        adminDAO.setTitles(titles);
		List<String> newTitles = adminDAO.getTitles();
		Assert.assertArrayEquals(newTitles.toArray(), titles.toArray());
	}

	@Test
	public void test_getAllAds() {
		List<AdDTO> allAds = adminDAO.getAllAds();
		Assert.assertEquals(9, allAds.size());
	}

	@Test
	public void test_setAllAds() {

		List<AdDTO> ads = new ArrayList<AdDTO>();

		for (int i = 0; i < 10; i++) {
			AdDTO ad = new AdDTO();
			ad.setContent("Content_" + i);
			ad.setTitle("Title_" + i);
			ad.setVisurl("Visurl_" + i);
			ads.add(ad);
		}
        adminDAO.setAllAds(ads);

		List<AdDTO> allAds = adminDAO.getAllAds();
		Assert.assertEquals(10, allAds.size());

	}

}
