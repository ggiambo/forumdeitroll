
package com.forumdeitroll.test;


import org.junit.Assert;
import org.junit.Test;

import com.forumdeitroll.persistence.ThreadsDTO;

import java.util.ArrayList;
import java.util.List;

public class ThreadsTest extends BaseTest {
	
	@Test
	public void test_getThreads() {
		
		ThreadsDTO res;
		
		res = persistence.getThreads("", 99, 0, null);
		Assert.assertEquals(2, res.getMessages().size());


		res = persistence.getThreads("Forum iniziale", 99, 0, null);
		Assert.assertEquals(1, res.getMessages().size());

		res = persistence.getThreads(null, 99, 0, null);
		Assert.assertEquals(4, res.getMessages().size());

		res = persistence.getThreads(null, 2, 0, null);
		Assert.assertEquals(2, res.getMessages().size());

		List<String> filtered = new ArrayList<String>();
		filtered.add("Forum iniziale");
		res = persistence.getThreads(null, 99, 0, filtered);
		Assert.assertEquals(3, res.getMessages().size());

	}

	@Test
	public void test_getThreadsByLastPost() {

		ThreadsDTO res;

		res = persistence.getThreadsByLastPost("", 99, 0, null);
		Assert.assertEquals(2, res.getMessages().size());


		res = persistence.getThreadsByLastPost("Forum iniziale", 99, 0, null);
		Assert.assertEquals(1, res.getMessages().size());

		res = persistence.getThreadsByLastPost(null, 99, 0, null);
		Assert.assertEquals(4, res.getMessages().size());

		res = persistence.getThreadsByLastPost(null, 2, 0, null);
		Assert.assertEquals(2, res.getMessages().size());

		List<String> filtered = new ArrayList<String>();
		filtered.add("Forum iniziale");
		res = persistence.getThreadsByLastPost(null, 99, 0, filtered);
		Assert.assertEquals(3, res.getMessages().size());
	}

	@Test
	public void test_getAuthorThreadsByLastPost() {

		ThreadsDTO res;

		res = persistence.getAuthorThreadsByLastPost("", 99, 0, null);
		Assert.assertEquals(0, res.getMessages().size());


		res = persistence.getAuthorThreadsByLastPost(null, 99, 0, null);
		Assert.assertEquals(0, res.getMessages().size());

		res = persistence.getAuthorThreadsByLastPost("Sfigato", 99, 0, null);
		Assert.assertEquals(1, res.getMessages().size());

		res = persistence.getAuthorThreadsByLastPost("Admin", 99, 0, null);
		Assert.assertEquals(0, res.getMessages().size());

		res = persistence.getAuthorThreadsByLastPost("admin", 99, 0, null);
		Assert.assertEquals(2, res.getMessages().size());

		List<String> filtered = new ArrayList<String>();
		filtered.add("Forum iniziale");
		res = persistence.getAuthorThreadsByLastPost("Sfigato", 99, 0, filtered);
		Assert.assertEquals(1, res.getMessages().size());

	}

}
