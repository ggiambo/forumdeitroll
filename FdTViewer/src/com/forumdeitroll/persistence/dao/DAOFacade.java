package com.forumdeitroll.persistence.dao;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.IPersistence;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DAOFacade implements IPersistence {

	private static final Logger LOG = Logger.getLogger(DAOFacade.class);

	private AuthorsDAO authorsDAO;
	private PreferencesDAO preferencesDAO;

	public void init(Properties databaseConfig) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		String host = databaseConfig.getProperty("host");
		String port = databaseConfig.getProperty("port");
		String username = databaseConfig.getProperty("username");
		String password = databaseConfig.getProperty("password");
		String dbname = databaseConfig.getProperty("dbname");
		String url = "jdbc:mysql://" + host + ":" + port + "/" + dbname + "?useUnicode=yes&characterEncoding=UTF-8";
		DSLContext jooq = setupDataSource(url, username, password);

		authorsDAO = new AuthorsDAO(jooq);
		preferencesDAO = new PreferencesDAO(jooq);

	}

	private DSLContext setupDataSource(String connectURI, String user, String password) {
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

		return DSL.using(dataSource, SQLDialect.MYSQL);
	}

	@Override
	public AuthorDTO getAuthor(String nick) {
		AuthorDTO user = authorsDAO.getAuthor(nick);
		user.setPreferences(getPreferences(user));
		return user;
	}

	@Override
	public List<AuthorDTO> getAuthors(boolean onlyActive) {
		return authorsDAO.getAuthors(onlyActive);
	}

	@Override
	public AuthorDTO registerUser(String nick, String password) {
		return authorsDAO.registerUser(nick, password);
	}

	@Override
	public void updateAuthor(AuthorDTO user) {
		authorsDAO.updateAuthor(user);
	}

	@Override
	public boolean updateAuthorPassword(AuthorDTO author, String newPassword) {
		return authorsDAO.updateAuthorPassword(author, newPassword);
	}

	@Override
	public Map<String, String> getPreferences(AuthorDTO user) {
		return preferencesDAO.getPreferences(user);
	}
}

