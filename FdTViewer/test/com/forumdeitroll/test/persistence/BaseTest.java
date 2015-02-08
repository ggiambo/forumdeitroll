package com.forumdeitroll.test.persistence;

import com.forumdeitroll.persistence.DAOFactory;
import com.forumdeitroll.persistence.dao.*;
import org.h2.jdbcx.JdbcConnectionPool;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseTest {

	private static JdbcConnectionPool pool;

    static AdminDAO adminDAO;
    static AuthorsDAO authorsDAO;
    static BookmarksDAO bookmarksDAO;
    static DigestDAO digestDAO;
    static MessagesDAO messagesDAO;
    static MiscDAO miscDAO;
    static PollsDAO pollsDAO;
    static PrivateMsgDAO pvtDAO;
    static QuotesDAO quotesDAO;
    static ThreadsDAO threadsDAO;

	@BeforeClass
	public static void init() throws Exception {
		// setup datasource
		Class.forName("org.h2.Driver");
		pool = JdbcConnectionPool.create("jdbc:h2:mem:fdtsucker;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1", "fdtsucker",
				"fdtsucker");

        DSLContext jooq = DSL.using(pool, SQLDialect.H2);

        authorsDAO = new AuthorsDAO(jooq);
        threadsDAO = new ThreadsDAO(jooq);
        messagesDAO = new MessagesDAO(jooq);
        pollsDAO = new PollsDAO(jooq);
        quotesDAO = new QuotesDAO(jooq);
        bookmarksDAO = new BookmarksDAO(jooq);
        adminDAO = new AdminDAO(jooq);
        miscDAO = new MiscDAO(jooq);
        pvtDAO = new PrivateMsgDAO(jooq);
        digestDAO = new DigestDAO(jooq);
    }

	@Before
	public void setupDatabase() throws Exception {
		// reset database
		loadData("schema_h2.sql");
		// load testdata
		loadData("com/forumdeitroll/test/persistence/testDatabase.sql");
	}

	private void loadData(String fileName) throws Exception {
		InputStream sqlFile = null;
		Reader isr = null;
		Connection conn = null;

		try {
			sqlFile = this.getClass().getClassLoader().getResourceAsStream(fileName);
			isr = new InputStreamReader(sqlFile);
			conn = pool.getConnection();
			ScriptRunner scriptRunner = new ScriptRunner(conn, true, true);
			scriptRunner.setLogWriter(null);
			scriptRunner.runScript(isr);
		} finally {
			if (isr != null) {
				try {
					isr.close();
				} catch (Exception e) {
					// ignore
				}
			}
			if (sqlFile != null) {
				try {
					sqlFile.close();
				} catch (Exception e) {
					// ignore
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	/**
	 * DatabaseString format: yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 * @throws ParseException
	 */
	public Date getDateFromDatabaseString(String databaseString) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.parse(databaseString);
	}

}
