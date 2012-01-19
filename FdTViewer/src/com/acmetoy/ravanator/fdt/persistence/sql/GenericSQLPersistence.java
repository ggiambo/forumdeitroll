package com.acmetoy.ravanator.fdt.persistence.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.IPersistence;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.acmetoy.ravanator.fdt.persistence.PrivateMsgDTO;
import com.acmetoy.ravanator.fdt.persistence.QuoteDTO;
import com.acmetoy.ravanator.fdt.persistence.ThreadDTO;

public abstract class GenericSQLPersistence implements IPersistence {

	private PoolingDataSource dataSource;

	private static final Logger LOG = Logger.getLogger(GenericSQLPersistence.class);

	void setupDataSource(String connectURI, String user, String password) {

		GenericObjectPool.Config config = new GenericObjectPool.Config();
		config.maxActive = 15;
		config.maxIdle = 10;
		config.minIdle = 3;
		config.maxWait = 100;

		ObjectPool connectionPool = new GenericObjectPool(null, config);

		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, user, password);
		new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);

		dataSource = new PoolingDataSource(connectionPool);
	}

	protected final synchronized Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public MessageDTO insertMessage(MessageDTO message) {
		if (message.getParentId() != -1) {
			if (message.getId() == -1) {
				return getMessage(insertReplyMessage(message));
			} else {
				return getMessage(insertEditMessage(message));
			}
		} else {
			return getMessage(insertNewMessage(message));
		}
	}

	private long insertEditMessage(MessageDTO message) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("UPDATE messages set text = ? where id = ?");
			int i = 1;
			ps.setString(i++, message.getText());
			ps.setLong(i++, message.getId());
			ps.execute();
			return message.getId();
		} catch (SQLException e) {
			LOG.error("Cannot insert message " + message.toString(), e);
		} finally {
			close(rs, ps, conn);
		}
		return -1;
	}

	private long insertReplyMessage(MessageDTO message) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("INSERT INTO messages (parentId, threadId, text, subject, author, forum, date) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			int i = 1;
			ps.setLong(i++, message.getParentId());
			ps.setLong(i++, message.getThreadId());
			ps.setString(i++, message.getText());
			ps.setString(i++, message.getSubject());
			ps.setString(i++, message.getAuthor().getNick());
			ps.setString(i++, message.getForum());
			ps.setTimestamp(i++, new java.sql.Timestamp(message.getDate().getTime()));
			ps.execute();
			// get generated id
			rs = ps.getGeneratedKeys();
			rs.next();
			return rs.getLong(1);
		} catch (SQLException e) {
			LOG.error("Cannot insert message " + message.toString(), e);
		} finally {
			close(rs, ps, conn);
		}
		return -1;
	}

	private long insertNewMessage(MessageDTO message) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("INSERT INTO messages (parentId, threadId, text, subject, author, forum, date) " +
					"VALUES (-1, -1, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			int i = 1;
			ps.setString(i++, message.getText());
			ps.setString(i++, message.getSubject());
			ps.setString(i++, message.getAuthor().getNick());
			ps.setString(i++, message.getForum());
			ps.setTimestamp(i++, new java.sql.Timestamp(message.getDate().getTime()));
			ps.execute();
			// get generated id
			rs = ps.getGeneratedKeys();
			rs.next();
			long id = rs.getLong(1);
			//  new message, update threadId and parentId
			ps = conn.prepareStatement("UPDATE messages SET parentId=?, threadId=? WHERE id=?");
			i = 1;
			ps.setLong(i++, id);
			ps.setLong(i++, id);
			ps.setLong(i++, id);
			ps.execute();
			return id;
		} catch (SQLException e) {
			LOG.error("Cannot insert message " + message.toString(), e);
		} finally {
			close(rs, ps, conn);
		}
		return -1;
	}

	@Override
	public MessageDTO getMessage(long id) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM messages WHERE id = ?");
			ps.setLong(1, id);
			List<MessageDTO> res = getMessages(ps.executeQuery());
			if (res.size() == 1) {
				return res.get(0);
			}
		} catch (SQLException e) {
			LOG.error("Cannot get message with id " + id, e);
		} finally {
			close(rs, ps, conn);
		}
		return new MessageDTO();
	}

	@Override
	public List<String> getForums() {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> result = new ArrayList<String>();
		try {
			ps = conn.prepareStatement("SELECT DISTINCT forum FROM messages WHERE forum IS NOT NULL ORDER BY forum ASC");
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getString(1));
			}
		} catch (SQLException e) {
			LOG.error("Cannot get Forums", e);
		} finally {
			close(rs, ps, conn);
		}
		return result;
	}

	@Override
	public AuthorDTO getAuthor(String nick) {
		AuthorDTO dto = new AuthorDTO();
		if (StringUtils.isEmpty(nick)) {
			return dto;
		}
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM authors WHERE UPPER(NICK) = ?");
			ps.setString(1, nick.toUpperCase());
			rs = ps.executeQuery();
			while (rs.next()) {
				dto.setNick(rs.getString("nick"));
				dto.setRanking(rs.getInt("ranking"));
				dto.setAvatar(rs.getBytes("avatar"));
				dto.setMessages(rs.getInt("messages"));
				dto.setOldPassword(rs.getString("password"));
				dto.setSalt(rs.getString("salt"));
				dto.setHash(rs.getString("hash"));
			}
		} catch (SQLException e) {
			LOG.error("Cannot get Author " + nick, e);
		} finally {
			close(rs, ps, conn);
		}
		return dto;
	}

	@Override
	public AuthorDTO registerUser(String nick, String password) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// check se esiste gia'. Blah banf transazioni chissenefrega <-- (complimenti a chi ha scritto questo - sarrusofono)
			if (getAuthor(nick).isValid()) {
				return new AuthorDTO();
			}

			final AuthorDTO a = new AuthorDTO();
			a.changePassword(password);

			// inserisci
			ps = conn.prepareStatement("INSERT INTO authors (nick, password, ranking, messages, salt, hash) VALUES (?, ?, ?, ?, ?, ?)");
			int i = 1;
			ps.setString(i++, nick);
			ps.setString(i++, ""); // <- campo "password", ospitava la vecchia "hash", non lo settiamo piu`
			ps.setInt(i++, 0);
			ps.setInt(i++, 0);
			ps.setString(i++, a.getSalt());
			ps.setString(i++, a.getHash());
			ps.execute();
			return getAuthor(nick);
		} catch (SQLException e) {
			LOG.error("Cannot get Author " + nick, e);
			return new AuthorDTO();
		} finally {
			close(rs, ps, conn);
		}
	}

	@Override
	public List<MessageDTO> getMessagesByThread(long threadId) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM messages WHERE threadId = ? ORDER BY id ASC");
			ps.setLong(1, threadId);
			return getMessages(ps.executeQuery());
		} catch (SQLException e) {
			LOG.error("Cannot get messages with threadId " + threadId, e);
		} finally {
			close(rs, ps, conn);
		}
		return new ArrayList<MessageDTO>();
	}

	@Override
	public void updateAuthor(AuthorDTO author) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("UPDATE authors SET messages = ?, avatar = ? where nick = ?");
			ps.setInt(1, author.getMessages());
			ps.setBytes(2, author.getAvatar());
			ps.setString(3, author.getNick());
			ps.executeUpdate();
		} catch (SQLException e) {
			LOG.error("Cannot update author " + author, e);
		} finally {
			close(rs, ps, conn);
		}
	}

	@Override
	public boolean updateAuthorPassword(AuthorDTO author, String newPassword) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("UPDATE authors SET password = ?, salt = ?, hash = ? WHERE nick = ?");
			int i = 1;
			author.changePassword(newPassword);
			ps.setString(i++, "");
			ps.setString(i++, author.getSalt());
			ps.setString(i++, author.getHash());
			ps.setString(i++, author.getNick());
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			LOG.error("Cannot update author '" + author.getNick() + "'", e);
		} finally {
			close(rs, ps, conn);
		}
		return false;
	}

	@Override
	public List<QuoteDTO> getQuotes(AuthorDTO author) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<QuoteDTO> out = new ArrayList<QuoteDTO>();
		try {
			ps = conn.prepareStatement("SELECT * FROM quotes WHERE nick = ? ORDER BY id ASC");
			ps.setString(1, author.getNick());
			rs = ps.executeQuery();
			while (rs.next()) {
				QuoteDTO dto = new QuoteDTO();
				dto.setId(rs.getLong("id"));
				dto.setContent(rs.getString("content"));
				dto.setNick(rs.getString("nick"));
				out.add(dto);
			}
		} catch (SQLException e) {
			LOG.error("Cannot get quotes for author '" + author + "'", e);
		} finally {
			close(rs, ps, conn);
		}
		return out;
	}

	@Override
	public void insertUpdateQuote(QuoteDTO quote) {
		if (quote.getId() > 0) {
			updateQuote(quote);
		} else {
			insertQuote(quote);
		}
	}

	@Override
	public void removeQuote(QuoteDTO quote) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("DELETE FROM quotes WHERE id = ? and nick = ?");
			int i = 1;
			ps.setLong(i++, quote.getId());
			ps.setString(i++, quote.getNick());
			ps.execute();
		} catch (SQLException e) {
			LOG.error("Cannot insert message " + quote.toString(), e);
		} finally {
			close(rs, ps, conn);
		}
	}

	private long insertQuote(QuoteDTO quote) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("INSERT INTO quotes (nick, content) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			int i = 1;
			ps.setString(i++, quote.getNick());
			ps.setString(i++, quote.getContent());
			ps.execute();
			// get generated id
			rs = ps.getGeneratedKeys();
			rs.next();
			return rs.getLong(1);
		} catch (SQLException e) {
			LOG.error("Cannot insert message " + quote.toString(), e);
		} finally {
			close(rs, ps, conn);
		}
		return -1;
	}

	private long updateQuote(QuoteDTO quote) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("UPDATE quotes SET nick = ?, content = ? WHERE id = ? AND nick = ?");
			int i = 1;
			ps.setString(i++, quote.getNick());
			ps.setString(i++, quote.getContent());
			ps.setLong(i++, quote.getId());
			ps.setString(i++, quote.getNick());
			ps.execute();
			return quote.getId();
		} catch (SQLException e) {
			LOG.error("Cannot insert message " + quote.toString(), e);
		} finally {
			close(rs, ps, conn);
		}
		return -1;
	}

	protected List<MessageDTO> getMessages(ResultSet rs) throws SQLException {
		List<MessageDTO> messages = new ArrayList<MessageDTO>();
		while (rs.next()) {
			MessageDTO message = new MessageDTO();
			message.setId(rs.getLong("id"));
			message.setParentId(rs.getLong("parentId"));
			message.setThreadId(rs.getLong("threadId"));
			message.setText(rs.getString("text"));
			message.setSubject(rs.getString("subject"));
			message.setAuthor(getAuthor(rs.getString("author")));
			message.setForum(rs.getString("forum"));
			message.setDate(rs.getTimestamp("date"));
			messages.add(message);
		}
		return messages;
	}

	protected List<ThreadDTO> getThreads(ResultSet rs) throws SQLException {
		List<ThreadDTO> messages = new ArrayList<ThreadDTO>();
		while (rs.next()) {
			ThreadDTO message = new ThreadDTO();
			message.setId(rs.getLong("id"));
			message.setSubject(rs.getString("subject"));
			message.setAuthor(getAuthor(rs.getString("author")));
			message.setForum(rs.getString("forum"));
			message.setDate(rs.getTimestamp("date"));
			message.setNumberOfMessages(getNumberOfMessages(message.getId()));
			messages.add(message);
		}
		return messages;
	}

	protected int getNumberOfMessages(long threadId) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT count(id) FROM messages WHERE threadId = ?");
			ps.setLong(1, threadId);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			LOG.error("Cannot count messages", e);
		} finally {
			close(rs, ps, conn);
		}
		return 0;
	}

	protected final void close(ResultSet rs, Statement stmt, Connection conn) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception ex) {
			// ignore
		}
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception ex) {
			// ignore
		}
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception ex) {
			// ignore
		}
	}


	@Override
	public void sendAPvtForGreatGoods(AuthorDTO author, PrivateMsgDTO privateMsg, String[] recipients) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("INSERT INTO pvt_content" +
										"(sender, content, senddate, subject, replyTo) " +
										"VALUES  (?,?,sysdate(),?,?)", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, author.getNick());
			ps.setString(2, privateMsg.getText());
			ps.setString(3, privateMsg.getSubject());
			ps.setLong(4, privateMsg.getReplyTo());
			ps.execute();
			
			rs = ps.getGeneratedKeys();
			rs.next();
			long pvt_id = rs.getLong(1);
			close(rs, ps, null);
			
			for (String recipient: recipients) {
				if (recipient.equals("")) continue;
				ps = conn.prepareStatement("INSERT INTO pvt_recipient" +
											"(pvt_id, recipient) "+
											"VALUES (?,?)");
				
				ps.setLong(1, pvt_id);
				ps.setString(2, recipient);
				ps.execute();
			}
			
		} catch (SQLException e) {
			LOG.error("Cannot insert pvt content or pvt recipient", e);
		} finally {
			close(rs, ps, conn);
		}
	}
	
	@Override
	public boolean checkForNewPvts(AuthorDTO recipient) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT count(*) FROM pvt_recipient WHERE recipient = ? AND `read` = 0");
			ps.setString(1, recipient.getNick());
			rs = ps.executeQuery();
			rs.next();
			return rs.getInt(1) > 0;
		} catch (SQLException e) {
			LOG.error("Cannot check for new pvts for user "+recipient.getNick(), e);
		} finally {
			close(rs, ps, conn);
		}
		return false;
	}
	
	@Override
	public void notifyRead(AuthorDTO recipient, PrivateMsgDTO privateMsg) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("UPDATE pvt_recipient SET `read` = 1 WHERE recipient = ? AND pvt_id = ?");
			ps.setString(1, recipient.getNick());
			ps.setLong(2, privateMsg.getId());
			int result;
			if ((result = ps.executeUpdate()) != 1) {
				throw new SQLException("Le scimmie presto! ha aggiornato "+result+" records!");
			}
		} catch (SQLException e) {
			LOG.error("Cannot notify "+recipient.getNick()+" id "+privateMsg.getId(), e);
		} finally {
			close(rs, ps, conn);
		}
	}
	
	@Override
	public void deletePvt(long pvt_id, AuthorDTO user) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("UPDATE pvt_recipient SET deleted = 1 WHERE pvt_id = ? AND recipient = ?");
			ps.setLong(1, pvt_id);
			ps.setString(2, user.getNick());
			ps.execute();
			close(null,ps,null);
			ps = conn.prepareStatement("UPDATE pvt_content SET deleted = 1 WHERE id = ? AND sender = ?");
			ps.setLong(1, pvt_id);
			ps.setString(2, user.getNick());
			ps.execute();
			close(null, ps, null);
			//cleanup - eventualmente opzionale oppure lanciata da timertask
			ps = conn.prepareStatement("DELETE FROM pvt_recipient WHERE pvt_id IN (SELECT id FROM pvt_content WHERE deleted = 1) AND deleted = 1");
			ps.execute();
			close(null, ps, null);
			ps = conn.prepareStatement("DELETE FROM pvt_content WHERE id NOT IN (SELECT id FROM pvt_recipient WHERE deleted = 0) AND deleted = 1");
			ps.execute();
			close(null, ps, null);
		} catch (SQLException e) {
			LOG.error("Cannot delete "+pvt_id+" for user "+user.getNick(), e);
		} finally {
			close(rs, ps, conn);
		}
	}
	
	
}
