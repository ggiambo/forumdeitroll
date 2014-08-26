package com.forumdeitroll.test.persistence;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.h2.jdbcx.JdbcConnectionPool;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Before;
import org.junit.BeforeClass;

import com.forumdeitroll.persistence.IPersistence;
import com.forumdeitroll.persistence.dao.DAOFacade;

public class BaseTest {

	private static JdbcConnectionPool pool;

	protected static IPersistence persistence;

	@BeforeClass
	public static void init() throws Exception {
		// setup datasource
		Class.forName("org.h2.Driver");
		pool = JdbcConnectionPool.create("jdbc:h2:mem:fdtsucker;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1", "fdtsucker", "fdtsucker");

		// setup persistence
		DAOFacade pers = new DAOFacade();
		pers.init(DSL.using(pool, SQLDialect.H2));
		persistence = pers;
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
