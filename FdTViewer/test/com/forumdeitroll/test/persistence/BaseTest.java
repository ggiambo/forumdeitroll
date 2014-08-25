package com.forumdeitroll.test.persistence;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.BeforeClass;

import com.forumdeitroll.persistence.IPersistence;
import com.forumdeitroll.persistence.sql.H2Persistence;

public class BaseTest {

	private static final BasicDataSource dataSource = new BasicDataSource();

	protected static IPersistence persistence;

	@BeforeClass
	public static void init() throws Exception {
		// setup datasource
		Class.forName("org.h2.Driver");
		dataSource.setMaxActive(30);
		dataSource.setMaxIdle(10);
		dataSource.setMinIdle(5);
		dataSource.setMaxWait(1000);
		dataSource.setTestOnBorrow(true);
		dataSource.setTestWhileIdle(true);
		dataSource.setTestOnReturn(true);
		dataSource.setUrl("jdbc:h2:mem:fdtsucker;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1");
		dataSource.setUsername("fdtsucker");
		dataSource.setPassword("fdtsucker");
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setValidationQueryTimeout(30);

		dataSource.setMinEvictableIdleTimeMillis(1800000);
		dataSource.setTimeBetweenEvictionRunsMillis(1800000);
		dataSource.setNumTestsPerEvictionRun(3);

		// setup persistence
		persistence = new H2Persistence() {
			private static final long serialVersionUID = 1L;

			@Override
			protected synchronized Connection getConnection() {
				try {
					return dataSource.getConnection();
				} catch (SQLException e) {
					e.printStackTrace();
					return null;
				}
			}
		};
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
