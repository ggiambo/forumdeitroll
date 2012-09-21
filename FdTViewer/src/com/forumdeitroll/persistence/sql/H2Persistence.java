package com.forumdeitroll.persistence.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.QuoteDTO;
import com.forumdeitroll.persistence.SearchMessagesSort;

public class H2Persistence extends GenericSQLPersistence {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(H2Persistence.class);

	public void init(Properties databaseConfig) throws Exception {
		Class.forName("org.h2.Driver");
		String path = databaseConfig.getProperty("path");
		String username = databaseConfig.getProperty("username");
		String password = databaseConfig.getProperty("password");
		String dbname = databaseConfig.getProperty("dbname");
		String url = "jdbc:h2:file:" + path + "/" + dbname + ";AUTO_SERVER=TRUE";
		super.setupDataSource(url, username, password);
	}

	@Override
	public List<MessageDTO> searchMessagesEx(String search, SearchMessagesSort sort, int limit, int page) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<MessageDTO> result = new ArrayList<MessageDTO>();
		StringBuilder query = new StringBuilder("SELECT *, COUNT(id) AS count FROM (");
		query.append("SELECT MSG.*, FT.score AS relevance FROM FT_SEARCH_DATA(?, ?, ?) FT, MESSAGES MSG ");
		query.append("WHERE FT.table = 'MESSAGES' AND MSG.id = FT.KEYS[0] ORDER BY ").append(sort.orderBy());
		query.append(") GROUP by id");
		try {
			ps = conn.prepareStatement(query.toString());
			ps.setString(1, search);
			ps.setInt(2, limit);
			ps.setInt(3, limit*page);
			return getMessages(ps.executeQuery(), true);
		} catch (SQLException e) {
			LOG.error("Cannot get messages", e);
		} finally {
			close(rs, ps, conn);
		}
		return result;
	}
}
