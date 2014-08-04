package com.forumdeitroll.test;

import com.forumdeitroll.persistence.dao.AuthorsDAO;
import com.forumdeitroll.persistence.dao.ThreadsDAO;
import org.apache.commons.dbcp.BasicDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.util.Properties;

public class BaseTest {

	DSLContext jooq;

	public BaseTest() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		String username = "fdtsucker";
		String password = "fdtsucker";
		String url = "jdbc:mysql://localhost:3306/fdtsucker?useUnicode=yes&characterEncoding=UTF-8";
		setupDataSource(url, "fdtsucker", "fdtsucker");
	}

	private void setupDataSource(String connectURI, String user, String password) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setMaxActive(15);
		dataSource.setMaxIdle(10);
		dataSource.setMinIdle(3);
		dataSource.setMaxWait(100);
		dataSource.setTestOnBorrow(true);
		dataSource.setTestWhileIdle(true);
		dataSource.setUrl(connectURI);
		dataSource.setUsername(user);
		dataSource.setPassword(password);
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setValidationQueryTimeout(30);

		jooq = DSL.using(dataSource, SQLDialect.MYSQL);
	}

}
