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
import com.acmetoy.ravanator.fdt.persistence.ThreadDTO;

public class MySQLPersistence extends GenericSQLPersistence {

	private static final Logger LOG = Logger.getLogger(MySQLPersistence.class);

	public void init(Properties databaseConfig) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		String host = databaseConfig.getProperty("host");
		String port = databaseConfig.getProperty("port");
		String username = databaseConfig.getProperty("username");
		String password = databaseConfig.getProperty("password");
		String dbname = databaseConfig.getProperty("dbname");
		String url = "jdbc:mysql://" + host + ":" + port + "/" + dbname;
		super.setupDataSource(url, username, password);
	}

	@Override
	public List<MessageDTO> getMessagesByDate(int limit, int page) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM messages ORDER BY id DESC LIMIT ?, ?");
			ps.setInt(1, limit*page);
			ps.setInt(2, limit);
			return getMessages(ps.executeQuery());
		} catch (SQLException e) {
			LOG.error("Cannot get messages with limit" + limit + " and page " + page, e);
		} finally {
			close(rs, ps, conn);
		}
		return new ArrayList<MessageDTO>();
	}

	@Override
	public List<MessageDTO> getMessagesByAuthor(String author, int limit, int page) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM messages where author = ? ORDER BY id DESC LIMIT ?, ?");
			ps.setString(1, author);
			ps.setInt(2, limit*page);
			ps.setInt(3, limit);
			return getMessages(ps.executeQuery());
		} catch (SQLException e) {
			LOG.error("Cannot get messages with limit" + limit + " and page " + page, e);
		} finally {
			close(rs, ps, conn);
		}
		return new ArrayList<MessageDTO>();
	}

	@Override
	public List<MessageDTO> getMessagesByForum(String forum, int limit, int page) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM messages where forum = ? ORDER BY id DESC LIMIT ?, ?");
			ps.setString(1, forum);
			ps.setInt(2, limit*page);
			ps.setInt(3, limit);
			return getMessages(ps.executeQuery());
		} catch (SQLException e) {
			LOG.error("Cannot get messages with limit" + limit + " and page " + page, e);
		} finally {
			close(rs, ps, conn);
		}
		return new ArrayList<MessageDTO>();
	}

	@Override
	public List<ThreadDTO> getThreads(int limit, int page) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<ThreadDTO> result = new ArrayList<ThreadDTO>();
		try {
			ps = conn.prepareStatement("SELECT * FROM messages WHERE id = threadid ORDER BY id DESC LIMIT ?, ?");
			ps.setInt(1, limit*page);
			ps.setInt(2, limit);
			return getThreads(ps.executeQuery());
		} catch (SQLException e) {
			LOG.error("Cannot get threads", e);
		} finally {
			close(rs, ps, conn);
		}
		return result;
	}

	@Override
	public List<MessageDTO> searchMessages(String search, int limit, int page) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<MessageDTO> result = new ArrayList<MessageDTO>();
		try {
			ps = conn.prepareStatement("SELECT * FROM messages WHERE MATCH (text) AGAINST (?) LIMIT ?, ?");
			ps.setString(1, search);
			ps.setInt(2, limit*page);
			ps.setInt(3, limit);
			return getMessages(ps.executeQuery());
		} catch (SQLException e) {
			LOG.error("Cannot get messages", e);
		} finally {
			close(rs, ps, conn);
		}
		return result;
	}

}
