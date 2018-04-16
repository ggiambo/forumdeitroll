package com.forumdeitroll.persistence;

import com.forumdeitroll.FdTConfig;
import com.forumdeitroll.persistence.dao.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.util.Properties;

public class DAOFactory {

	public static final String FORUM_PROC = "Proc di Catania";
	public static final String FORUM_ASHES = "Cenere";

	private static final Logger LOG = Logger.getLogger(DAOFactory.class);

	static DAOFactory instance;

	private AuthorsDAO authorsDAO;
	private ThreadsDAO threadsDAO;
	private MessagesDAO messagesDAO;
	private QuotesDAO quotesDAO;
	private BookmarksDAO bookmarksDAO;
	private AdminDAO adminDAO;
	private MiscDAO miscDAO;
	private PrivateMsgDAO privateMsgDAO;
	private LoginsDAO loginsDAO;

    BasicDataSource dataSource;

	DAOFactory() throws ClassNotFoundException {
	}

	static synchronized DAOFactory getInstance() {
		if (instance == null) {
			String persistenceName = FdTConfig.getProperty("persistence.name");
			try {
				instance = new DAOFactory();
				instance.init(FdTConfig.getDatabaseConfig(persistenceName));
			} catch (Exception e) {
				LOG.fatal("Cannot instantiate Persistence '" + persistenceName + "'", e);
				return null;
			}
		}
		return instance;
	}

	void init(Properties databaseConfig) throws ClassNotFoundException {
		String driver = databaseConfig.getProperty("driverclass");
		Class.forName(driver);

		String username = databaseConfig.getProperty("username");
		String password = databaseConfig.getProperty("password");
		String url = databaseConfig.getProperty("url");

		dataSource = new BasicDataSource();
		dataSource.setMaxTotal(15);
		dataSource.setMaxIdle(10);
		dataSource.setMinIdle(3);
		dataSource.setMaxWaitMillis(30000);
		dataSource.setTestOnBorrow(true);
		dataSource.setTestWhileIdle(true);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setValidationQueryTimeout(30);

		DSLContext jooq = DSL.using(dataSource, SQLDialect.MYSQL);

		authorsDAO = new AuthorsDAO(jooq);
		threadsDAO = new ThreadsDAO(jooq);
		messagesDAO = new MessagesDAO(jooq);
		quotesDAO = new QuotesDAO(jooq);
		bookmarksDAO = new BookmarksDAO(jooq);
		adminDAO = new AdminDAO(jooq);
		miscDAO = new MiscDAO(jooq);
		privateMsgDAO = new PrivateMsgDAO(jooq);
		loginsDAO = new LoginsDAO(jooq);
	}

	public static final AuthorsDAO getAuthorsDAO() {
		return getInstance().authorsDAO;
	}

	public static final ThreadsDAO getThreadsDAO() {
		return getInstance().threadsDAO;
	}

	public static final MessagesDAO getMessagesDAO() {
		return getInstance().messagesDAO;
	}

	public static final QuotesDAO getQuotesDAO() {
		return getInstance().quotesDAO;
	}

	public static final BookmarksDAO getBookmarksDAO() {
		return getInstance().bookmarksDAO;
	}

	public static final AdminDAO getAdminDAO() {
		return getInstance().adminDAO;
	}

	public static final MiscDAO getMiscDAO() {
		return getInstance().miscDAO;
	}

	public static final PrivateMsgDAO getPrivateMsgDAO() {
		return getInstance().privateMsgDAO;
	}

	public static final LoginsDAO getLoginsDAO() {
		return getInstance().loginsDAO;
	}

}
