package com.forumdeitroll.test.persistence;

import com.forumdeitroll.persistence.IPersistence;
import com.forumdeitroll.persistence.dao.DAOFacade;
import org.apache.commons.dbcp.BasicDataSource;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseTest {

	private static BasicDataSource dataSource;

	protected static IPersistence persistence;

	@BeforeClass
	public static void init() throws Exception {
		// setup datasource
		Class.forName("org.h2.Driver");
		dataSource = new BasicDataSource();
		dataSource.setMaxActive(30);
		dataSource.setMaxIdle(10);
		dataSource.setMinIdle(5);
		dataSource.setMaxWait(1000);
		dataSource.setTestOnBorrow(true);
		dataSource.setTestWhileIdle(true);
		dataSource.setTestOnReturn(true);
		dataSource.setUrl("jdbc:h2:mem:fdtsucker;DATABASE_TO_UPPER=false");
		dataSource.setUsername("fdtsucker");
		dataSource.setPassword("fdtsucker");
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setValidationQueryTimeout(30);

		dataSource.setMinEvictableIdleTimeMillis(1800000);
		dataSource.setTimeBetweenEvictionRunsMillis(1800000);
		dataSource.setNumTestsPerEvictionRun(3);

		// setup persistence
		DAOFacade pers = new DAOFacade();
		pers.init(DSL.using(dataSource, SQLDialect.H2));
		persistence = pers;
	}

	@Before
	public void setupDatabase() throws Exception {
		// reset database
		loadData("schema_h2.sql");
		// load testdata
		loadData("com/forumdeitroll/test/persistence/testDatabase.sql");
	}

	@Test
	public void testSetupDatabase() {
		// just create the database and populate it
	}

	private void loadData(String fileName) throws Exception {
		InputStream sqlFile = null;
		Reader isr = null;
		Connection conn = null;

		try {
			sqlFile = this.getClass().getClassLoader().getResourceAsStream(fileName);
			isr = new InputStreamReader(sqlFile);
			conn = dataSource.getConnection();
			new ScriptRunner(conn, true, true).runScript(isr);
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
