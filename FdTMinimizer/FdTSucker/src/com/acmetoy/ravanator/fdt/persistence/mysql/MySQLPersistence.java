package com.acmetoy.ravanator.fdt.persistence.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.acmetoy.ravanator.fdt.persistence.Persistence;

public class MySQLPersistence implements Persistence {

	private static final Logger LOG = Logger.getLogger(MySQLPersistence.class);
	
	private Connection conn;

	public MySQLPersistence(Properties databaseConfig) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		String host = databaseConfig.getProperty("host");
		String port = databaseConfig.getProperty("port");
		String username = databaseConfig.getProperty("username");
		String password = databaseConfig.getProperty("password");
		String dbname = databaseConfig.getProperty("dbname");
		String url = "jdbc:mysql://" + host + ":" + port + "/" + dbname;
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection(url, username, password);
	}

	@Override
	public AuthorDTO getAuthor(String nick) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			AuthorDTO dto = new AuthorDTO();
			ps = conn.prepareStatement("SELECT * FROM authors WHERE NICK = ?");
			ps.setString(1, nick);
			rs = ps.executeQuery();
			while (rs.next()) {
				dto.setNick(rs.getString("nick"));
				dto.setRanking(rs.getInt("ranking"));
				dto.setAvatar(rs.getBytes("avatar"));
				dto.setMessages(rs.getInt("messages"));
				return dto;
			}
		} catch (SQLException e) {
			LOG.error("Cannot get Author " + nick, e);
		} finally {
			closeResources(rs, ps);
		}
		return null;
	}

	@Override
	public long getLastMessageId() {
		long lastMessageId = -1;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT id FROM messages ORDER BY id DESC LIMIT 1");
			rs = ps.executeQuery();
			while (rs.next()) {
				lastMessageId = rs.getLong(1);
			}
		} catch (SQLException e) {
			LOG.error("Cannot get last message id", e);
		} finally {
			closeResources(rs, ps);
		}
		return lastMessageId;
	}

	@Override
	public MessageDTO getMessage(long id) {
		MessageDTO message = new MessageDTO();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM messages WHERE id = ?");
			ps.setLong(1, id);
			rs = ps.executeQuery();
			while (rs.next()) {
				message.setId(rs.getLong("id")); 
				message.setParentId(rs.getLong("parentId")); 
				message.setThreadId(rs.getLong("threadId")); 
				message.setText(rs.getString("text"));
				message.setSubject(rs.getString("subject")); 
				message.setAuthor(rs.getString("author"));
				message.setForum(rs.getString("forum"));
				message.setDate(rs.getDate("date"));
			}
		} catch (SQLException e) {
			LOG.error("Cannot insert message " + message.toString(), e);
		} finally {
			closeResources(rs, ps);
		}
		return message;
	}

	@Override
	public List<MessageDTO> getMessagesByDate(int limit) {
		List<MessageDTO> messages = new ArrayList<MessageDTO>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM messages ORDER BY date DESC LIMIT ?");
			ps.setInt(1, limit);
			rs = ps.executeQuery();
			while (rs.next()) {
				MessageDTO message = new MessageDTO();
				message.setId(rs.getLong("id")); 
				message.setParentId(rs.getLong("parentId")); 
				message.setThreadId(rs.getLong("threadId")); 
				message.setText(rs.getString("text"));
				message.setSubject(rs.getString("subject")); 
				message.setAuthor(rs.getString("author"));
				message.setForum(rs.getString("forum"));
				message.setDate(rs.getDate("date"));
				messages.add(message);
			}
		} catch (SQLException e) {
			LOG.error("Cannot get messages with limit" + limit, e);
		} finally {
			closeResources(rs, ps);
		}
		return messages;
	}

	@Override
	public List<MessageDTO> getMessagesByDate(int limit, int page) {
		List<MessageDTO> messages = new ArrayList<MessageDTO>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM messages ORDER BY date DESC LIMIT ?, ?");
			ps.setInt(1, limit*page);
			ps.setInt(2, limit*(page + 1));
			rs = ps.executeQuery();
			while (rs.next()) {
				MessageDTO message = new MessageDTO();
				message.setId(rs.getLong("id")); 
				message.setParentId(rs.getLong("parentId")); 
				message.setThreadId(rs.getLong("threadId")); 
				message.setText(rs.getString("text"));
				message.setSubject(rs.getString("subject")); 
				message.setAuthor(rs.getString("author"));
				message.setForum(rs.getString("forum"));
				message.setDate(rs.getDate("date"));
				messages.add(message);
			}
		} catch (SQLException e) {
			LOG.error("Cannot get messages with limit" + limit + " and page " + page, e);
		} finally {
			closeResources(rs, ps);
		}
		return messages;
	}

	@Override
	public List<MessageDTO> getMessagesByThread(long threadId) {
		List<MessageDTO> messages = new ArrayList<MessageDTO>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM messages WHERE threadId = ? ORDER BY id ASC");
			ps.setLong(1, threadId);
			rs = ps.executeQuery();
			while (rs.next()) {
				MessageDTO message = new MessageDTO();
				message.setId(rs.getLong("id")); 
				message.setParentId(rs.getLong("parentId")); 
				message.setThreadId(rs.getLong("threadId")); 
				message.setText(rs.getString("text"));
				message.setSubject(rs.getString("subject")); 
				message.setAuthor(rs.getString("author"));
				message.setForum(rs.getString("forum"));
				message.setDate(rs.getDate("date"));
				messages.add(message);
			}
		} catch (SQLException e) {
			LOG.error("Cannot get messages with threadId " + threadId, e);
		} finally {
			closeResources(rs, ps);
		}
		return messages;
	}

	@Override
	public boolean hasAuthor(String nick) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT nick FROM authors where nick = ?");
			ps.setString(1, nick);
			rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			LOG.error("Cannot get author with nick " + nick, e);
		} finally {
			closeResources(rs, ps);
		}
		return false;
	}

	@Override
	public boolean hasMessage(long id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT id FROM messages where id = ?");
			ps.setLong(1, id);
			rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			LOG.error("Cannot get author message with id " + id, e);
		} finally {
			closeResources(rs, ps);
		}
		return false;
	}

	@Override
	public void insertAuthor(AuthorDTO author) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("INSERT INTO authors (nick, ranking, messages, avatar) VALUES (?, ?, ?, ?)");
			ps.setString(1, author.getNick());
			ps.setInt(2, author.getRanking());
			ps.setInt(3, author.getMessages());
			ps.setBytes(4, author.getAvatar());
			ps.execute();
		} catch (SQLException e) {
			LOG.error("Cannot insert author " + author.toString(), e);
		} finally {
			closeResources(rs, ps);
		}
	}
	
	@Override
	public void updateAuthor(AuthorDTO author) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("UPDATE authors SET ranking = ?, messages = ?, avatar = ? WHERE nick = ?");
			ps.setInt(1, author.getRanking());
			ps.setInt(2, author.getMessages());
			ps.setBytes(3, author.getAvatar());
			ps.setString(4, author.getNick());
			ps.execute();
		} catch (SQLException e) {
			LOG.error("Cannot update author" + author.toString(), e);
		} finally {
			closeResources(rs, ps);
		}
	}

	@Override
	public void insertMessage(MessageDTO message) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("INSERT INTO messages (id, parentId, threadId, text, subject, author, forum, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			int i = 1;
			ps.setLong(i++, message.getId());
			ps.setLong(i++, message.getParentId());
			ps.setLong(i++, message.getThreadId());
			ps.setString(i++, message.getText());
			ps.setString(i++, message.getSubject());
			ps.setString(i++, message.getAuthor());
			ps.setString(i++, message.getForum());
			ps.setTimestamp(i++, new java.sql.Timestamp(message.getDate().getTime()));
			ps.execute();
		} catch (SQLException e) {
			LOG.error("Cannot insert message " + message.toString(), e);
		} finally {
			closeResources(rs, ps);
		}
	}
	
	private void closeResources(ResultSet rs, PreparedStatement ps) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// ignore
			}
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				// ignore
			}
		}
	}

}
