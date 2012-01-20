package com.acmetoy.ravanator.fdt.persistence.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.acmetoy.ravanator.fdt.persistence.QuoteDTO;

public class PostgreSQLPersistence extends GenericSQLPersistence {

	private static final Logger LOG = Logger.getLogger(PostgreSQLPersistence.class);

	public void init(Properties databaseConfig) throws Exception {
		Class.forName("org.postgresql.Driver");
		String host = databaseConfig.getProperty("host");
		String username = databaseConfig.getProperty("username");
		String password = databaseConfig.getProperty("password");
		String dbname = databaseConfig.getProperty("dbname");
		String url = "jdbc:postgresql://" + host  + "/" + dbname;
		super.setupDataSource(url, username, password);
	}

	@Override
	public List<MessageDTO> searchMessages(String search, int limit, int page) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<MessageDTO> result = new ArrayList<MessageDTO>();
		try {
			ps = conn.prepareStatement("SELECT *, ts_rank_cd(text_search, query, 32) AS rank FROM messages, plainto_tsquery(?) AS query " +
					"WHERE text @@ query ORDER BY rank DESC LIMIT ? OFFSET ?");
			ps.setString(1, search);
			ps.setInt(2, limit);
			ps.setInt(3, limit*page);
			return getMessages(ps.executeQuery());
		} catch (SQLException e) {
			LOG.error("Cannot get messages", e);
		} finally {
			close(rs, ps, conn);
		}
		return result;
	}
	
	@Override
	public QuoteDTO getRandomQuote() {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		QuoteDTO out = new QuoteDTO();
		try {
			ps = conn.prepareStatement("SELECT * FROM quotes ORDER BY RANDOM() LIMIT 1");
			rs = ps.executeQuery();
			if (rs.next()) {
				out.setContent(rs.getString("content"));
				out.setNick(rs.getString("nick"));
			}
		} catch (SQLException e) {
			LOG.error("Cannot get random quote", e);
		} finally {
			close(rs, ps, conn);
		}
		return null;
	}
	
}
