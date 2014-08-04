
package com.forumdeitroll.test;

import com.forumdeitroll.persistence.ThreadsDTO;
import com.forumdeitroll.persistence.dao.DAOFacade;
import com.forumdeitroll.persistence.dao.ThreadsDAO;

public class ThreadsDAOTest extends BaseTest {

	public static void main(String[] args) throws Exception {
		ThreadsDAOTest test = new ThreadsDAOTest();
		test.doTests();
	}

	private void doTests() {
		try {
			ThreadsDAO threadsDAO = new ThreadsDAO(jooq);
			ThreadsDTO threadsByLastPost = threadsDAO.getThreadsByLastPost("", 5, 1, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ThreadsDAOTest() throws Exception {
		super();
	}
}
