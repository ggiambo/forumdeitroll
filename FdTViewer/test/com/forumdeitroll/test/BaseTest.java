package com.forumdeitroll.test;

import com.forumdeitroll.persistence.IPersistence;
import com.forumdeitroll.persistence.sql.H2Persistence;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseTest {

	private static BasicDataSource dataSource;

	protected static IPersistence persistence;

	@BeforeClass
	public static void init() throws Exception {
		// setup datasource
		Class.forName("org.h2.Driver");
		dataSource = new BasicDataSource();
		dataSource.setMaxActive(15);
		dataSource.setMaxIdle(10);
		dataSource.setMinIdle(3);
		dataSource.setMaxWait(100);
		dataSource.setTestOnBorrow(true);
		dataSource.setTestWhileIdle(true);
		dataSource.setUrl("jdbc:h2:mem:testDatabase");
		dataSource.setUsername("fdtsucker");
		dataSource.setPassword("fdtsucker");
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setValidationQueryTimeout(30);

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
		loadData("com/forumdeitroll/test/testDatabase.sql");
	}

	public final void loadData(String fileName) throws Exception {
		InputStream sqlFile = this.getClass().getClassLoader().getResourceAsStream(fileName);
		Reader isr = new InputStreamReader(sqlFile);
		new ScriptRunner(dataSource.getConnection(), true, true).runScript(isr);
		isr.close();
		sqlFile.close();
	}


}
