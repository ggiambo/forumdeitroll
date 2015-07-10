package com.forumdeitroll.test.persistence;

import com.forumdeitroll.persistence.ThreadDTO;
import com.forumdeitroll.persistence.ThreadsDTO;
import com.forumdeitroll.test.BaseTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ThreadsTest extends BaseTest {

	@Test
	public void test_getThreads() throws Exception {

		ThreadsDTO res;

		res = threadsDAO.getThreads("", 99, 0, null);
		assertEquals(2, res.getMessages().size());
		assertEquals(2, res.getMaxNrOfMessages());
		ThreadDTO thread = res.getMessages().get(0);
		assertEquals("Sfigato", thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2014-08-16 14:54:11"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(7, thread.getId());
		assertEquals(-1, thread.getLastId());
		assertEquals(3, thread.getNumberOfMessages());
		assertEquals(1, thread.getRank());
		assertEquals("Ieri", thread.getSubject());
		assertEquals(true, thread.isVisible());
		thread = res.getMessages().get(1);
		assertEquals(null, thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-14 12:23:13"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(6, thread.getId());
		assertEquals(-1, thread.getLastId());
		assertEquals(1, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Nel Forum Principale", thread.getSubject());
		assertEquals(true, thread.isVisible());

		res = threadsDAO.getThreads("Forum iniziale", 99, 0, null);
		assertEquals(1, res.getMessages().size());
		assertEquals(1, res.getMaxNrOfMessages());
		thread = res.getMessages().get(0);
		assertEquals("admin", thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-13 14:30:15"), thread.getDate());
		assertEquals("Forum iniziale", thread.getForum());
		assertEquals(1, thread.getId());
		assertEquals(-1, thread.getLastId());
		assertEquals(3, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("benvenuto nel fdt !", thread.getSubject());
		assertEquals(true, thread.isVisible());

		res = threadsDAO.getThreads(null, 99, 0, null);
		assertEquals(4, res.getMessages().size());
		assertEquals(4, res.getMaxNrOfMessages());
		thread = res.getMessages().get(0);
		assertEquals("Sfigato", thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2014-08-16 14:54:11"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(7, thread.getId());
		assertEquals(-1, thread.getLastId());
		assertEquals(3, thread.getNumberOfMessages());
		assertEquals(1, thread.getRank());
		assertEquals("Ieri", thread.getSubject());
		assertEquals(true, thread.isVisible());
		thread = res.getMessages().get(1);
		assertEquals(null, thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-14 12:23:13"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(6, thread.getId());
		assertEquals(-1, thread.getLastId());
		assertEquals(1, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Nel Forum Principale", thread.getSubject());
		assertEquals(true, thread.isVisible());
		thread = res.getMessages().get(2);
		assertEquals(null, thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-13 18:03:12"), thread.getDate());
		assertEquals("Procura Svizzera", thread.getForum());
		assertEquals(4, thread.getId());
		assertEquals(-1, thread.getLastId());
		assertEquals(2, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Primo !", thread.getSubject());
		assertEquals(true, thread.isVisible());
		thread = res.getMessages().get(3);
		assertEquals("admin", thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-13 14:30:15"), thread.getDate());
		assertEquals("Forum iniziale", thread.getForum());
		assertEquals(1, thread.getId());
		assertEquals(-1, thread.getLastId());
		assertEquals(3, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("benvenuto nel fdt !", thread.getSubject());
		assertEquals(true, thread.isVisible());

		res = threadsDAO.getThreads(null, 2, 0, null);
		assertEquals(2, res.getMessages().size());
		assertEquals(4, res.getMaxNrOfMessages());
		thread = res.getMessages().get(0);
		assertEquals("Sfigato", thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2014-08-16 14:54:11"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(7, thread.getId());
		assertEquals(-1, thread.getLastId());
		assertEquals(3, thread.getNumberOfMessages());
		assertEquals(1, thread.getRank());
		assertEquals("Ieri", thread.getSubject());
		assertEquals(true, thread.isVisible());
		thread = res.getMessages().get(1);
		assertEquals(null, thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-14 12:23:13"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(6, thread.getId());
		assertEquals(-1, thread.getLastId());
		assertEquals(1, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Nel Forum Principale", thread.getSubject());
		assertEquals(true, thread.isVisible());

		List<String> filtered = new ArrayList<String>();
		filtered.add("Forum iniziale");
		res = threadsDAO.getThreads(null, 99, 0, filtered);
		assertEquals(3, res.getMessages().size());
		assertEquals(1, res.getMaxNrOfMessages());
		thread = res.getMessages().get(0);
		assertEquals("Sfigato", thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2014-08-16 14:54:11"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(7, thread.getId());
		assertEquals(-1, thread.getLastId());
		assertEquals(3, thread.getNumberOfMessages());
		assertEquals(1, thread.getRank());
		assertEquals("Ieri", thread.getSubject());
		assertEquals(true, thread.isVisible());
	}

	@Test
	public void test_getThreadsByLastPost() throws Exception {

		ThreadsDTO res;

		res = threadsDAO.getThreadsByLastPost("", 99, 0, null);
		assertEquals(2, res.getMessages().size());
		ThreadDTO thread = res.getMessages().get(0);
		assertEquals("admin", thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2014-08-16 14:55:14"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(7, thread.getId());
		assertEquals(9, thread.getLastId());
		assertEquals(3, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Re: Ieri", thread.getSubject());
		assertEquals(true, thread.isVisible());
		thread = res.getMessages().get(1);
		assertEquals(null, thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-14 12:23:13"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(6, thread.getId());
		assertEquals(6, thread.getLastId());
		assertEquals(1, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Nel Forum Principale", thread.getSubject());
		assertEquals(true, thread.isVisible());

		res = threadsDAO.getThreadsByLastPost("Forum iniziale", 99, 0, null);
		assertEquals(1, res.getMessages().size());
		thread = res.getMessages().get(0);
		assertEquals("admin", thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-13 14:55:45"), thread.getDate());
		assertEquals("Forum iniziale", thread.getForum());
		assertEquals(1, thread.getId());
		assertEquals(3, thread.getLastId());
		assertEquals(3, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("benvenuto nel fdt !", thread.getSubject());
		assertEquals(true, thread.isVisible());

		res = threadsDAO.getThreadsByLastPost(null, 99, 0, null);
		assertEquals(4, res.getMessages().size());
		thread = res.getMessages().get(0);
		assertEquals("admin", thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2014-08-16 14:55:14"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(7, thread.getId());
		assertEquals(9, thread.getLastId());
		assertEquals(3, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Re: Ieri", thread.getSubject());
		assertEquals(true, thread.isVisible());
		thread = res.getMessages().get(1);
		assertEquals(null, thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-14 12:23:13"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(6, thread.getId());
		assertEquals(6, thread.getLastId());
		assertEquals(1, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Nel Forum Principale", thread.getSubject());
		assertEquals(true, thread.isVisible());
		thread = res.getMessages().get(2);
		assertEquals(null, thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-14 09:15:54"), thread.getDate());
		assertEquals("Procura Svizzera", thread.getForum());
		assertEquals(4, thread.getId());
		assertEquals(5, thread.getLastId());
		assertEquals(2, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Secondo !", thread.getSubject());
		assertEquals(true, thread.isVisible());
		thread = res.getMessages().get(3);
		assertEquals("admin", thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-13 14:55:45"), thread.getDate());
		assertEquals("Forum iniziale", thread.getForum());
		assertEquals(1, thread.getId());
		assertEquals(3, thread.getLastId());
		assertEquals(3, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("benvenuto nel fdt !", thread.getSubject());
		assertEquals(true, thread.isVisible());

		res = threadsDAO.getThreadsByLastPost(null, 2, 0, null);
		assertEquals(2, res.getMessages().size());
		thread = res.getMessages().get(0);
		assertEquals("admin", thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2014-08-16 14:55:14"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(7, thread.getId());
		assertEquals(9, thread.getLastId());
		assertEquals(3, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Re: Ieri", thread.getSubject());
		assertEquals(true, thread.isVisible());
		thread = res.getMessages().get(1);
		assertEquals(null, thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-14 12:23:13"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(6, thread.getId());
		assertEquals(6, thread.getLastId());
		assertEquals(1, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Nel Forum Principale", thread.getSubject());
		assertEquals(true, thread.isVisible());

		List<String> filtered = new ArrayList<String>();
		filtered.add("Forum iniziale");
		res = threadsDAO.getThreadsByLastPost(null, 99, 0, filtered);
		assertEquals(3, res.getMessages().size());
		thread = res.getMessages().get(0);
		assertEquals("admin", thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2014-08-16 14:55:14"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(7, thread.getId());
		assertEquals(9, thread.getLastId());
		assertEquals(3, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Re: Ieri", thread.getSubject());
		assertEquals(true, thread.isVisible());
		thread = res.getMessages().get(1);
		assertEquals(null, thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-14 12:23:13"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(6, thread.getId());
		assertEquals(6, thread.getLastId());
		assertEquals(1, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Nel Forum Principale", thread.getSubject());
		assertEquals(true, thread.isVisible());
		thread = res.getMessages().get(2);
		assertEquals(null, thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-14 09:15:54"), thread.getDate());
		assertEquals("Procura Svizzera", thread.getForum());
		assertEquals(4, thread.getId());
		assertEquals(5, thread.getLastId());
		assertEquals(2, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Secondo !", thread.getSubject());
		assertEquals(true, thread.isVisible());
	}

	@Test
	public void test_getAuthorThreadsByLastPost() throws Exception {

		ThreadsDTO res;

		res = threadsDAO.getAuthorThreadsByLastPost("", 99, 0, null);
		assertEquals(0, res.getMessages().size());

		res = threadsDAO.getAuthorThreadsByLastPost(null, 99, 0, null);
		assertEquals(0, res.getMessages().size());

		res = threadsDAO.getAuthorThreadsByLastPost("Sfigato", 99, 0, null);
		assertEquals(1, res.getMessages().size());
		ThreadDTO thread = res.getMessages().get(0);
		assertEquals("admin", thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2014-08-16 14:55:14"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(7, thread.getId());
		assertEquals(9, thread.getLastId());
		assertEquals(3, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Re: Ieri", thread.getSubject());
		assertEquals(true, thread.isVisible());

		res = threadsDAO.getAuthorThreadsByLastPost("Admin", 99, 0, null);
		assertEquals(0, res.getMessages().size());

		res = threadsDAO.getAuthorThreadsByLastPost("admin", 99, 0, null);
		assertEquals(2, res.getMessages().size());
		thread = res.getMessages().get(0);
		assertEquals("admin", thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2014-08-16 14:55:14"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(7, thread.getId());
		assertEquals(9, thread.getLastId());
		assertEquals(3, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Re: Ieri", thread.getSubject());
		assertEquals(true, thread.isVisible());
		thread = res.getMessages().get(1);
		assertEquals("admin", thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2010-05-13 14:55:45"), thread.getDate());
		assertEquals("Forum iniziale", thread.getForum());
		assertEquals(1, thread.getId());
		assertEquals(3, thread.getLastId());
		assertEquals(3, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("benvenuto nel fdt !", thread.getSubject());
		assertEquals(true, thread.isVisible());

		List<String> filtered = new ArrayList<String>();
		filtered.add("Forum iniziale");
		res = threadsDAO.getAuthorThreadsByLastPost("Sfigato", 99, 0, filtered);
		assertEquals(1, res.getMessages().size());
		thread = res.getMessages().get(0);
		assertEquals("admin", thread.getAuthor().getNick());
		assertEquals(getDateFromDatabaseString("2014-08-16 14:55:14"), thread.getDate());
		assertEquals(null, thread.getForum());
		assertEquals(7, thread.getId());
		assertEquals(9, thread.getLastId());
		assertEquals(3, thread.getNumberOfMessages());
		assertEquals(0, thread.getRank());
		assertEquals("Re: Ieri", thread.getSubject());
		assertEquals(true, thread.isVisible());

	}

}
