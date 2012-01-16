package com.acmetoy.ravanator.fdt.persistence.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.acmetoy.ravanator.fdt.persistence.PrivateMsgDTO;
import com.acmetoy.ravanator.fdt.persistence.QuoteDTO;
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
		String url = "jdbc:mysql://" + host + ":" + port + "/" + dbname + "?useUnicode=yes&characterEncoding=UTF-8";
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
		
	@Override
	public QuoteDTO getRandomQuote() {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		QuoteDTO out = new QuoteDTO();
		try {
			ps = conn.prepareStatement("SELECT * FROM quotes ORDER BY RAND() LIMIT 1");
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
		return out;
	}
	
	@Override
	public List<PrivateMsgDTO> getInbox(AuthorDTO user, int limit, int pageNr) {
		Connection conn = getConnection();
		PreparedStatement ps = null, ps2 = null;
		ResultSet rs = null, rs2 = null;
		List<PrivateMsgDTO> result = new LinkedList<PrivateMsgDTO>();
		try {
			ps = conn.prepareStatement("SELECT pvt_id, read FROM pvt_recipient WHERE recipient = ? AND deleted = 0 LIMIT ?,?");
			ps.setString(1, user.getNick());
			ps.setInt(2, limit*pageNr);
			ps.setInt(3, limit);
			rs = ps.executeQuery();
			while (rs.next()) {
				long id = rs.getLong("pvt_id");
				PrivateMsgDTO msg = new PrivateMsgDTO();
				msg.setId(id);
				msg.setRead(rs.getBoolean("read"));
				ps2 = conn.prepareStatement("SELECT sender, subject, senddate FROM pvt_content WHERE id = ?");
				ps2.setLong(1, id);
				rs2 = ps2.executeQuery();
				rs2.next();
				msg.setDate(rs.getDate("senddate"));
				msg.setSubject(rs.getString("subject"));
				msg.setFromNick(rs.getString("sender"));
				close(rs2, ps2, null);
				ps2 = conn.prepareStatement("SELECT recipient FROM pvt_recipient WHERE pvt_id = ?");
				ps2.setLong(1, id);
				rs2 = ps2.executeQuery();
				while (rs2.next()) {
					msg.getToNick().add(rs2.getString(1));
				}
				close(rs2, ps2, null);
				result.add(msg);
			}
		} catch (SQLException e) {
			LOG.error("Cannot get getInbox for user "+user.getNick()+" limit "+limit+", pageNr "+pageNr, e);
		} finally {
			close(rs, ps, conn);
		}
		return result;
	}
	
	@Override
	public List<PrivateMsgDTO> getSentPvts(AuthorDTO user, int limit, int pageNr) {
		Connection conn = getConnection();
		PreparedStatement ps = null, ps2 = null;
		ResultSet rs = null, rs2 = null;
		List<PrivateMsgDTO> result = new LinkedList<PrivateMsgDTO>();
		try {
			ps = conn.prepareStatement("SELECT id, subject, senddate FROM pvt_content WHERE sender = ? AND deleted = 0 LIMIT ?,?");
			ps.setString(1, user.getNick());
			ps.setInt(2, limit*pageNr);
			ps.setInt(3, limit);
			rs = ps.executeQuery();
			while (rs.next()) {
				PrivateMsgDTO msg = new PrivateMsgDTO();
				msg.setId(rs.getLong("id"));
				msg.setSubject(rs.getString("subject"));
				msg.setDate(rs.getDate("senddate"));
				ps2 = conn.prepareStatement("SELECT recipient FROM pvt_recipient WHERE pvt_id = ?");
				ps2.setLong(1, msg.getId());
				rs2 = ps2.executeQuery();
				while (rs2.next()) {
					msg.getToNick().add(rs2.getString(1));
				}
				close(rs2, ps2, null);
				result.add(msg);
			}
		} catch (SQLException e) {
			LOG.error("Cannot get getSentPvts for user "+user.getNick()+" limit "+limit+", pageNr "+pageNr, e);
		} finally {
			close(rs, ps, conn);
		}
		return result;
	}
	
	@Override
	public PrivateMsgDTO getPvtDetails(long pvt_id, AuthorDTO user) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(
				"SELECT content, replyTo, subject, senddate, recipient " +
				"FROM pvt_content, pvt_recipient " +
				"WHERE id = pvt_id " +
				"AND id = ? " +
				"AND (sender = ? OR " +
					"? IN ( SELECT recipient " +
					"  FROM pvt_recipient" +
					"  WHERE pvt_id = ?" +
					")" +
				")");
			ps.setLong(1, pvt_id);
			ps.setString(2, user.getNick());
			ps.setString(3, user.getNick());
			ps.setLong(4, pvt_id);
			rs = ps.executeQuery();
			//one row per recipient
			PrivateMsgDTO msg = null;
			while (rs.next()) {
				if (msg == null) {
					msg = new PrivateMsgDTO();
					msg.setId(pvt_id);
					msg.setFromNick(user.getNick());
					msg.setDate(rs.getDate("senddate"));
					msg.setReplyTo(rs.getLong("replyTo"));
					msg.setText(rs.getString("content"));
					msg.setSubject(rs.getString("subject"));
				}
				msg.getToNick().add(rs.getString("recipient"));
			}
			return msg;
		} catch (SQLException e) {
			LOG.error("Cannot get pvt details for user "+user.getNick()+" pvt_id "+pvt_id, e);
			return null;
		} finally {
			close(rs, ps, conn);
		}
	}
}
