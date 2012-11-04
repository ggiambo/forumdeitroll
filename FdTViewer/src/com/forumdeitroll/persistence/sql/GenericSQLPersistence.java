package com.forumdeitroll.persistence.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.forumdeitroll.FdTException;
import com.forumdeitroll.PagerTag;
import com.forumdeitroll.PasswordUtils;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.IPersistence;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.MessagesDTO;
import com.forumdeitroll.persistence.NotificationDTO;
import com.forumdeitroll.persistence.PollDTO;
import com.forumdeitroll.persistence.PollQuestion;
import com.forumdeitroll.persistence.PollsDTO;
import com.forumdeitroll.persistence.PrivateMsgDTO;
import com.forumdeitroll.persistence.QuoteDTO;
import com.forumdeitroll.persistence.SearchMessagesSort;
import com.forumdeitroll.persistence.ThreadDTO;
import com.forumdeitroll.persistence.ThreadsDTO;

public abstract class GenericSQLPersistence implements IPersistence {

	private static final long serialVersionUID = 1L;

	private BasicDataSource dataSource;

	private static final Logger LOG = Logger.getLogger(GenericSQLPersistence.class);
	
	void setupDataSource(String connectURI, String user, String password) {
		dataSource = new BasicDataSource();
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
	public MessagesDTO getMessagesByAuthor(String author, String forum, int limit, int page) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			StringBuilder query = new StringBuilder("SELECT * FROM messages WHERE author = ?");
			int i = 1;
			if ("".equals(forum)) {
				query.append(" AND forum IS NULL");
			} else if (forum != null) {
				query.append(" AND forum = ?");
			}
			query.append(" ORDER BY id DESC LIMIT ? OFFSET ?");
			ps = conn.prepareStatement(query.toString());
			ps.setString(i++, author);
			if (StringUtils.isNotEmpty(forum)) {
				ps.setString(i++, forum);
			}
			ps.setInt(i++, limit);
			ps.setInt(i++, limit*page);
			return new MessagesDTO(getMessages(ps.executeQuery(), false), countMessagesByAuthor(author, forum, conn));
		} catch (SQLException e) {
			LOG.error("Cannot get messages with limit" + limit + " and page " + page, e);
		} finally {
			close(rs, ps, conn);
		}
		return new MessagesDTO();
	}

	@Override
	public MessagesDTO getMessages(String forum, int limit, int page, boolean hideProcCatania) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			StringBuilder query = new StringBuilder("SELECT * FROM messages");
			int i = 1;
			if ("".equals(forum)) {
				query.append(" WHERE forum IS NULL");
			} else if (forum == null) {
				if (hideProcCatania) {
					query.append(" WHERE (forum IS NULL OR forum != '").append(FORUM_PROC).append("') ");
				}
			} else {
				query.append(" WHERE forum = ?");
			}
			query.append(" ORDER BY id DESC LIMIT ? OFFSET ?");
			ps = conn.prepareStatement(query.toString());
			if (StringUtils.isNotEmpty(forum)) {
				ps.setString(i++, forum);
			}
			ps.setInt(i++, limit);
			ps.setInt(i++, limit*page);

			int messagesCount = countMessages(forum, conn);
			if (hideProcCatania && forum == null) {
				messagesCount -= countMessages(FORUM_PROC, conn);
			}

			return new MessagesDTO(getMessages(ps.executeQuery(), false), messagesCount);
		} catch (SQLException e) {
			LOG.error("Cannot get messages with limit" + limit + " and page " + page, e);
		} finally {
			close(rs, ps, conn);
		}
		return new MessagesDTO();
	}

	@Override
	public ThreadsDTO getThreads(String forum, int limit, int page, boolean hideProcCatania) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			StringBuilder query = new StringBuilder("SELECT * FROM messages WHERE id = threadId");
			int i = 1;
			if ("".equals(forum)) {
				query.append(" AND forum IS NULL");
			} else if (forum == null) {
				if (hideProcCatania) {
					query.append(" AND (forum IS NULL OR forum != '").append(FORUM_PROC).append("') ");
				}
			} else {
				query.append(" AND forum = ?");
			}
			query.append(" ORDER BY id DESC LIMIT ? OFFSET ?");
			ps = conn.prepareStatement(query.toString());
			if (StringUtils.isNotEmpty(forum)) {
				ps.setString(i++, forum);
			}
			ps.setInt(i++, limit);
			ps.setInt(i++, limit*page);
			
			int threadsCount = countThreads(forum, conn);
			if (hideProcCatania && forum == null) {
				threadsCount -= countThreads(FORUM_PROC, conn);
			}
			
			return new ThreadsDTO(getThreads(ps.executeQuery()), threadsCount);
		} catch (SQLException e) {
			LOG.error("Cannot get threads", e);
		} finally {
			close(rs, ps, conn);
		}
		return new ThreadsDTO();
	}
	
	@Override
	public ThreadsDTO getThreadsByLastPost(String forum, int limit, int page, boolean hideProcCatania) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<ThreadDTO> result = new ArrayList<ThreadDTO>();
		try {
			conn = getConnection();
			StringBuilder query = new StringBuilder();
			query.append("SELECT MAX(`id`) AS mid FROM messages JOIN (");
			query.append("    SELECT DISTINCT `threadId` FROM messages" );
			int i = 1;
			if ("".equals(forum)) {
				query.append(" WHERE forum IS NULL");
			} else if (forum == null) {
				if (hideProcCatania) {
					query.append(" WHERE (forum IS NULL OR forum != '").append(FORUM_PROC).append("')");
				}
			} else {
				query.append(" WHERE forum = ?");
			}
			query.append("    ORDER BY `id` DESC ");
			query.append("    LIMIT ? OFFSET ? ");
			query.append(") AS threadIds ");
			query.append(" ON (threadIds.threadId = messages.threadId) ");
			query.append(" GROUP BY messages.threadId ");
			query.append(" ORDER BY mid DESC");
			ps = conn.prepareStatement(query.toString());
			if (StringUtils.isNotEmpty(forum)) {
				ps.setString(i++, forum);
			}
			ps.setInt(i++, limit);
			ps.setInt(i++, limit*page);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(getMessage(rs.getLong(1)));
			}
			return new ThreadsDTO(result, countThreads(forum, conn));
		} catch (SQLException e) {
			LOG.error("Cannot get threads by last post", e);
		} finally {
			close(rs, ps, conn);
		}
		return new ThreadsDTO();
	}

	@Override
	public List<ThreadDTO> getAuthorThreadsByLastPost(String author, int limit, int page, boolean hideProcCatania) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		final List<ThreadDTO> result = new ArrayList<ThreadDTO>();
		try {
			conn = getConnection();
			StringBuilder query = new StringBuilder();
			query.append("SELECT DISTINCT threadId ")
				.append("FROM messages ")
				.append("WHERE author = ? ");
			if (hideProcCatania) {
				query.append("AND (forum IS NULL OR forum != '").append(FORUM_PROC).append("') ");
			}
			query.append("ORDER BY `id` DESC ")
				.append("LIMIT ? OFFSET ?");
			
			
			ps = conn.prepareStatement(query.toString());
			int i = 1;
			ps.setString(i++, author);
			ps.setInt(i++, limit);
			ps.setInt(i++, limit*page);

			rs = ps.executeQuery();

			while (rs.next()) {
				result.add(getMessage(rs.getLong(1)));
			}
		} catch (SQLException e) {
			LOG.error("Cannot get author threads by last post", e);
		} finally {
			close(rs, ps, conn);
		}
		return result;
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
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("UPDATE messages set text = ?, subject = ? where id = ?");
			int i = 1;
			ps.setString(i++, message.getText());
			ps.setString(i++, message.getSubject());
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
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("INSERT INTO messages (parentId, threadId, text, subject, author, forum, date) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			int i = 1;
			ps.setLong(i++, message.getParentId());
			ps.setLong(i++, message.getThreadId());
			ps.setString(i++, message.getText());
			ps.setString(i++, message.getSubject());
			ps.setString(i++, message.getAuthor().getNick());
			ps.setString(i++, message.getForum());
			ps.setTimestamp(i++, new Timestamp(message.getDate().getTime()));
			ps.execute();
			// update count
			increaseNumberOfMessages(message.getForum(), false);
			// get generated id
			rs = ps.getGeneratedKeys();
			rs.next();
			conn.commit();
			return rs.getLong(1);
		} catch (SQLException e) {
			LOG.error("Cannot insert message " + message.toString(), e);
		} finally {
			close(rs, ps, conn);
		}
		return -1;
	}

	private long insertNewMessage(MessageDTO message) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("INSERT INTO messages (parentId, threadId, text, subject, author, forum, date) " +
					"VALUES (-1, -1, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			int i = 1;
			ps.setString(i++, message.getText());
			ps.setString(i++, message.getSubject());
			ps.setString(i++, message.getAuthor().getNick());
			ps.setString(i++, message.getForum());
			ps.setTimestamp(i++, new Timestamp(message.getDate().getTime()));
			ps.execute();
			// update count
			increaseNumberOfMessages(message.getForum(), true);
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
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM messages WHERE id = ?");
			ps.setLong(1, id);
			List<MessageDTO> res = getMessages(ps.executeQuery(), false);
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
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> result = new ArrayList<String>();
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT forum FROM messages WHERE forum IS NOT NULL GROUP BY forum ORDER BY COUNT(id) DESC, forum ASC");
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
		AuthorDTO dto = new AuthorDTO(null);
		if (StringUtils.isEmpty(nick)) {
			return dto;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM authors WHERE UPPER(NICK) = ?");
			ps.setString(1, nick.toUpperCase());
			rs = ps.executeQuery();
			while (rs.next()) {
				dto.setNick(rs.getString("nick"));
				dto.setAvatar(rs.getBytes("avatar"));
				dto.setMessages(rs.getInt("messages"));
				dto.setOldPassword(rs.getString("password"));
				dto.setSalt(rs.getString("salt"));
				dto.setHash(rs.getString("hash"));
			}
			dto.setPreferences(getPreferences(dto));
		} catch (SQLException e) {
			LOG.error("Cannot get Author " + nick, e);
		} finally {
			close(rs, ps, conn);
		}
		return dto;
	}

	@Override
	public List<AuthorDTO> getAuthors(boolean onlyActive) {
		List<AuthorDTO> res = new ArrayList<AuthorDTO>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder query = new StringBuilder("SELECT * FROM authors");
		try {
			conn = getConnection();
			if (onlyActive) {
				query.append(" WHERE hash IS NOT NULL");
			}
			ps = conn.prepareStatement(query.toString());
			rs = ps.executeQuery();
			AuthorDTO dto;
			while (rs.next()) {
				dto = new AuthorDTO(null);
				dto.setNick(rs.getString("nick"));
				dto.setAvatar(rs.getBytes("avatar"));
				dto.setMessages(rs.getInt("messages"));
				dto.setOldPassword(rs.getString("password"));
				dto.setSalt(rs.getString("salt"));
				dto.setHash(rs.getString("hash"));
				res.add(dto);
			}
		} catch (SQLException e) {
			LOG.error("Cannot get Authors", e);
		} finally {
			close(rs, ps, conn);
		}
		return res;
	}

	@Override
	public AuthorDTO registerUser(String nick, String password) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			// check se esiste gia'. Blah banf transazioni chissenefrega <-- (complimenti a chi ha scritto questo - sarrusofono)
			if (getAuthor(nick).isValid()) {
				return new AuthorDTO(null);
			}

			final AuthorDTO a = new AuthorDTO(null);
			PasswordUtils.changePassword(a, password);

			// inserisci
			ps = conn.prepareStatement("INSERT INTO authors (nick, password, messages, salt, hash) VALUES (?, ?, ?, ?, ?)");
			int i = 1;
			ps.setString(i++, nick);
			ps.setString(i++, ""); // <- campo "password", ospitava la vecchia "hash", non lo settiamo piu`
			ps.setInt(i++, 0);
			ps.setString(i++, a.getSalt());
			ps.setString(i++, a.getHash());
			ps.execute();
			return getAuthor(nick);
		} catch (SQLException e) {
			LOG.error("Cannot get Author " + nick, e);
			return new AuthorDTO(null);
		} finally {
			close(rs, ps, conn);
		}
	}

	@Override
	public List<MessageDTO> getMessagesByThread(long threadId) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM messages WHERE threadId = ? ORDER BY id ASC");
			ps.setLong(1, threadId);
			return getMessages(ps.executeQuery(), false);
		} catch (SQLException e) {
			LOG.error("Cannot get messages with threadId " + threadId, e);
		} finally {
			close(rs, ps, conn);
		}
		return new ArrayList<MessageDTO>();
	}

	@Override
	public void updateAuthor(AuthorDTO author) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
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
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("UPDATE authors SET password = ?, salt = ?, hash = ? WHERE nick = ?");
			int i = 1;
			PasswordUtils.changePassword(author, newPassword);
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
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<QuoteDTO> out = new ArrayList<QuoteDTO>();
		try {
			conn = getConnection();
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

	public List<QuoteDTO> getAllQuotes() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		final List<QuoteDTO> out = new ArrayList<QuoteDTO>();

		try {
			conn = getConnection();
			ps = conn.prepareStatement("select quotes.id as id, authors.nick as nick, quotes.content as content from quotes, authors where quotes.nick = authors.nick and authors.messages > 0 and authors.hash <> ?;");
			ps.setString(1, AuthorDTO.BANNED_TAG);
			rs = ps.executeQuery();
			while (rs.next()) {
				final QuoteDTO dto = new QuoteDTO();
				dto.setId(rs.getLong("id"));
				dto.setContent(rs.getString("content"));
				dto.setNick(rs.getString("nick"));
				out.add(dto);
			}
		} catch (SQLException e) {
			LOG.error("Cannot get all quotes", e);
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
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
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

	@Override
	public PrivateMsgDTO getPvtDetails(long pvt_id, AuthorDTO user) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(
				"SELECT content, replyTo, subject, senddate, recipient, sender " +
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
					//msg.setFromNick(user.getNick());
					msg.setFromNick(rs.getString("sender"));
					msg.setDate(rs.getTimestamp("senddate"));
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

	@Override
	public List<PrivateMsgDTO> getInbox(AuthorDTO user, int limit, int pageNr) {
		Connection conn = null;
		PreparedStatement ps = null, ps2 = null;
		ResultSet rs = null, rs2 = null;
		List<PrivateMsgDTO> result = new LinkedList<PrivateMsgDTO>();
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT pvt_id, `read`, senddate FROM pvt_recipient, pvt_content WHERE pvt_id = id AND recipient = ? AND pvt_recipient.deleted = 0 ORDER BY senddate desc LIMIT ? OFFSET ?");
			ps.setString(1, user.getNick());
			ps.setInt(2, limit);
			ps.setInt(3, limit*pageNr);
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
				msg.setDate(rs2.getTimestamp("senddate"));
				msg.setSubject(rs2.getString("subject"));
				msg.setFromNick(rs2.getString("sender"));
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
	public boolean sendAPvtForGreatGoods(AuthorDTO author, PrivateMsgDTO privateMsg, String[] recipients) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		long pvt_id = -1;
		try {
			conn = getConnection();

			//verifica esistenza dei destinatari
			for (String recipient: recipients) {
				if (recipient.equals("")) continue;
				ps = conn.prepareStatement("SELECT nick FROM authors WHERE nick = ? AND hash IS NOT NULL");
				ps.setString(1, recipient);
				rs = ps.executeQuery();
				if (!rs.next()) throw new FdTException("Il destinatario "+StringEscapeUtils.escapeHtml4(recipient)+" non esiste.");
			}


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
			pvt_id = rs.getLong(1);
		} catch (SQLException e) {
			LOG.error("Cannot insert into pvt_content", e);
			return false;
		} finally {
			close(rs, ps, null);
		}
		try {
			for (String recipient: recipients) {
				if (recipient.equals("")) continue;
				ps = conn.prepareStatement("INSERT INTO pvt_recipient" +
											"(pvt_id, recipient) "+
											"VALUES (?,(SELECT nick FROM authors WHERE nick = ?))"); // mi assicuro che il nick esista

				ps.setLong(1, pvt_id);
				ps.setString(2, recipient);
				try {
					ps.execute();
				} catch (SQLException e) {
					LOG.error("Probabilmente il recipient "+recipient+" non esiste.");
					throw e;
				}
			}
		} catch (SQLException e) {
			LOG.error("Cannot insert into pvt_recipient", e);
			// prova ad annullare l'insert del messaggio ed eventuali collegati
			for (int i=0;i<recipients.length;++i) {
				AuthorDTO rec = new AuthorDTO(null);
				rec.setNick(recipients[i]);
				deletePvt(pvt_id, rec);
			}
			deletePvt(pvt_id, author);
			return false;
		} finally {
			close(rs, ps, conn);
		}
		return true;
	}

	@Override
	public boolean checkForNewPvts(AuthorDTO recipient) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
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
	
	private void notifyPvt(AuthorDTO recipient, PrivateMsgDTO privateMsg, boolean read) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("UPDATE pvt_recipient SET `read` = ? WHERE recipient = ? AND pvt_id = ?");
			ps.setBoolean(1, read);
			ps.setString(2, recipient.getNick());
			ps.setLong(3, privateMsg.getId());
			int result;
			if ((result = ps.executeUpdate()) > 1) { // 0 == messaggio gia' letto
				throw new SQLException("Le scimmie presto! "+recipient.getNick()+"ha aggiornato "+result+" records!");
			}
		} catch (SQLException e) {
			LOG.error("Cannot notify "+recipient.getNick()+" id "+privateMsg.getId(), e);
		} finally {
			close(rs, ps, conn);
		}
	}
	
	@Override
	public void notifyUnread(AuthorDTO recipient, PrivateMsgDTO privateMsg) {
		notifyPvt(recipient, privateMsg, false);
	}

	@Override
	public void notifyRead(AuthorDTO recipient, PrivateMsgDTO privateMsg) {
		notifyPvt(recipient, privateMsg, true);
	}

	@Override
	public void deletePvt(long pvt_id, AuthorDTO user) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
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

	@Override
	public List<PrivateMsgDTO> getSentPvts(AuthorDTO user, int limit, int pageNr) {
		Connection conn = null;
		PreparedStatement ps = null, ps2 = null;
		ResultSet rs = null, rs2 = null;
		List<PrivateMsgDTO> result = new LinkedList<PrivateMsgDTO>();
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT id, subject, senddate FROM pvt_content WHERE sender = ? AND deleted = 0 ORDER BY senddate desc LIMIT ? OFFSET ?");
			ps.setString(1, user.getNick());
			ps.setInt(2, limit);
			ps.setInt(3, limit*pageNr);
			rs = ps.executeQuery();
			while (rs.next()) {
				PrivateMsgDTO msg = new PrivateMsgDTO();
				msg.setRead(true); // se l'ho mandato io...
				msg.setId(rs.getLong("id"));
				msg.setSubject(rs.getString("subject"));
				msg.setDate(rs.getTimestamp("senddate"));
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
	public int getInboxPages(AuthorDTO author) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT COUNT(*) FROM pvt_recipient WHERE recipient = ? AND deleted = 0");
			ps.setString(1, author.getNick());
			rs = ps.executeQuery();
			rs.next();
			int nElem = rs.getInt(1);
			return PagerTag.pagify(nElem, 10);
		} catch (SQLException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			close(rs, ps, conn);
		}
		return 1;
	}

	@Override
	public int getOutboxPages(AuthorDTO author) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT COUNT(*) FROM pvt_content WHERE sender = ? AND deleted = 0");
			ps.setString(1, author.getNick());
			rs = ps.executeQuery();
			rs.next();
			int nElem = rs.getInt(1);
			return PagerTag.pagify(nElem, 10);
		} catch (SQLException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			close(rs, ps, conn);
		}
		return 1;
	}

	@Override
	public Map<String, String> getPreferences(AuthorDTO user) {
		final Map<String, String> r = new ConcurrentHashMap<String, String>();
		if (user == null || !user.isValid()) {
			return r;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT `key`, value FROM preferences WHERE nick = ?");
			ps.setString(1, user.getNick());
			rs = ps.executeQuery();
			while (rs.next()) {
				r.put(rs.getString("key"), rs.getString("value"));
			}
		} catch (SQLException e) {
			LOG.error("Cannot read properties for user " + user.getNick(), e);
		} finally {
			close(rs, ps, conn);
		}
		return r;
	}

	protected abstract List<MessageDTO> searchMessagesEx(String search, SearchMessagesSort sort, int pageSize, int pageNr);

	public List<MessageDTO> searchMessages(String search, SearchMessagesSort sort, int pageSize, int pageNr) {
		if (StringUtils.isEmpty(search)) {
			final List<MessageDTO> r = Collections.emptyList();
			return r;
		} else {
			return searchMessagesEx(search, sort, pageSize, pageNr);
		}
	}

	public List<String> searchAuthor(String searchString) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> res = new ArrayList<String>();
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT nick FROM authors WHERE nick LIKE ? AND hash IS NOT NULL ORDER BY nick ASC");
			ps.setString(1, searchString + "%");
			rs = ps.executeQuery();
			while (rs.next()) {
				res.add(rs.getString(1));
			}
		} catch (SQLException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			close(rs, ps, conn);
		}
		return res;
	}

	@Override
	public Map<String, String> setPreference(AuthorDTO user, String key, String value) {
		if (!getPreferences(user).keySet().contains(key)) {
			insertPreference(user, key, value);
		} else {
			updatePreference(user, key, value);
		}
		return getPreferences(user);
	}
	
	@Override
	public long createPoll(PollDTO pollDTO) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Timestamp now = new Timestamp(System.currentTimeMillis());
			conn = getConnection();
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("INSERT INTO poll (title, author, text, creationDate, updateDate) VALUES (?, ?, ?, ?, ?)", 
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, pollDTO.getTitle());
			ps.setString(2, pollDTO.getAuthor());
			ps.setString(3, pollDTO.getText());
			ps.setTimestamp(4, now);
			ps.setTimestamp(5, now);
			ps.execute();
			// get generated id
			rs = ps.getGeneratedKeys();
			rs.next();
			long pollId = rs.getLong(1);
			// insert poll questions
			for (PollQuestion question : pollDTO.getPollQuestions()) {
				ps = conn.prepareStatement("INSERT INTO poll_question (pollId, sequence, text, votes) VALUES (?, ?, ?, ?)");
				ps.setLong(1, pollId);
				ps.setInt(2, question.getSequence());
				ps.setString(3, question.getText());
				ps.setInt(4, 0);
				ps.execute();
			}
			conn.commit();
			return pollId;
		} catch (SQLException e) {
			LOG.error("Cannot create poll " + pollDTO, e);
		} finally {
			close(null, ps, conn);
		}
		return -1;
	}
	
	@Override
	public boolean updatePollQuestion(PollQuestion pollQuestion, AuthorDTO user) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// check se ha gia' votato
			conn = getConnection();
			ps = conn.prepareStatement("SELECT COUNT(*) AS cnt FROM poll_user WHERE pollId = ? AND nick = ?");
			ps.setLong(1, pollQuestion.getPollId());
			ps.setString(2, user.getNick());
			rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt("cnt") != 0) {
					return false;
				}
			}
			// update
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE poll_question SET votes = votes + 1 WHERE pollId = ? and sequence = ?");
			ps.setLong(1, pollQuestion.getPollId());
			ps.setInt(2, pollQuestion.getSequence());
			ps.execute();
			// update update date :)
			ps = conn.prepareStatement("UPDATE poll SET updateDate = ? WHERE id = ?");
			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setLong(2, pollQuestion.getPollId());
			ps.execute();
			// 1 troll, i vote
			ps = conn.prepareStatement("INSERT INTO poll_user (nick, pollId) VALUES (?, ?)");
			ps.setString(1, user.getNick());
			ps.setLong(2, pollQuestion.getPollId());
			ps.execute();
			conn.commit();
		} catch (SQLException e) {
			LOG.error("Cannot update pollQuestion " + pollQuestion, e);
		} finally {
			close(null, ps, conn);
		}
		return true;
	}
	
	@Override
	public PollsDTO getPollsByDate(int limit, int page) {
		return  getPollsBy("creationDate", limit, page);
	}
	
	@Override
	public PollsDTO getPollsByLastVote(int limit, int page) {
		return  getPollsBy("updateDate", limit, page);
	}
	
	public PollsDTO getPollsBy(String by, int limit, int page) {
		List<PollDTO> res = new ArrayList<PollDTO>();
		int nrOfPolls = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM poll ORDER BY " + by + " DESC LIMIT ? OFFSET ?");
			ps.setInt(1, limit);
			ps.setInt(2, limit*page);
			rs = ps.executeQuery();
			while (rs.next()) {
				res.add(getPoll(conn, rs));
			}
			rs = conn.prepareStatement("SELECT COUNT(*) AS cnt FROM poll").executeQuery();
			if (rs.next()) {
				nrOfPolls = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			LOG.error("Cannot get polls with limit" + limit + " and page " + page, e);
		} finally {
			close(rs, ps, conn);
		}
		return new PollsDTO(res, nrOfPolls);
	}
	
	@Override
	public PollDTO getPoll(long pollId) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM poll WHERE id = ?");
			ps.setLong(1, pollId);
			rs = ps.executeQuery();
			rs.next();
			return getPoll(conn, rs);
		} catch (SQLException e) {
			LOG.error("Cannot get polls with id" + pollId, e);
		} finally {
			close(rs, ps, conn);
		}
		return null;
	}
	
	@Override
	public List<NotificationDTO> getNotifications(String fromNick, String toNick) {
		List<NotificationDTO> res = new ArrayList<NotificationDTO>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			StringBuilder query = new StringBuilder("SELECT * FROM notification WHERE 1=1");
			if (StringUtils.isNotEmpty(fromNick)) {
				query.append(" AND fromNick = ?");
			}
			if (StringUtils.isNotEmpty(toNick)) {
				query.append(" AND toNick = ?");
			}
			query.append(" ORDER BY id ASC");
			ps = conn.prepareStatement(query.toString());
			int i = 1;
			if (StringUtils.isNotEmpty(fromNick)) {
				ps.setString(i++, fromNick);
			}
			if (StringUtils.isNotEmpty(toNick)) {
				ps.setString(i++, toNick);
			}
			rs = ps.executeQuery();
			NotificationDTO notification;
			while (rs.next()) {
				notification = new NotificationDTO();
				notification.setId(rs.getLong("id"));
				notification.setFromNick(rs.getString("fromNick"));
				notification.setToNick(rs.getString("toNick"));
				notification.setMsgId(rs.getLong("msgId"));
				res.add(notification);
			}
		} catch (SQLException e) {
			LOG.error("Cannot get notifications for fromNick " + fromNick + " and toNick " + toNick, e);
		} finally {
			close(rs, ps, conn);
		}
		return res;
	}
	
	@Override
	public void removeNotification(String fromNick, String toNick, long id) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			StringBuilder query = new StringBuilder("DELETE FROM notification WHERE fromNick = ?");
			if (StringUtils.isNotEmpty(toNick)) {
				query.append(" AND toNick = ?");
			}
			query.append(" AND id = ?");
			ps = conn.prepareStatement(query.toString());
			int i = 1;
			ps.setString(i++, fromNick);
			if (StringUtils.isNotEmpty(toNick)) {
				ps.setString(i++, toNick);
			}
			ps.setLong(i++, id);
			ps.execute();
		} catch (SQLException e) {
			LOG.error("Cannot remove notifications from " + fromNick + " and id " + id, e);
		} finally {
			close(rs, ps, conn);
		}
	}
	
	@Override
	public void createNotification(String fromNick, String toNick, long msgId) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("INSERT INTO notification (fromNick, toNick, msgId) VALUES (?, ?, ?)");
			int i = 1;
			ps.setString(i++, fromNick);
			ps.setString(i++, toNick);
			ps.setLong(i++, msgId);
			ps.execute();
		} catch (SQLException e) {
			LOG.error("Cannot get notifications for fromNick " + fromNick + " and toNick " + toNick, e);
		} finally {
			close(rs, ps, conn);
		}
	}
	
	private List<String> getPollVoterNicks(Connection conn, long pollId) {
		List<String> ret = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT nick FROM poll_user WHERE pollId = ?");
			ps.setLong(1, pollId);
			rs = ps.executeQuery();
			while (rs.next()) {
				ret.add(rs.getString("nick"));
			}
		} catch (SQLException e) {
			LOG.error("Cannot get polls with id" + pollId, e);
		} finally {
			close(rs, ps, null);
		}
		return ret;
	}

	private void insertPreference(AuthorDTO user, String key, String value) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("INSERT INTO preferences (nick, `key`, value) VALUES (?, ?, ?)");
			ps.setString(1, user.getNick());
			ps.setString(2, key);
			ps.setString(3, value);
			ps.execute();
		} catch (SQLException e) {
			LOG.error("Cannot set preference key=" + key + " value=" + value + " for user " + user.getNick(), e);
		} finally {
			close(null, ps, conn);
		}
	}

	private void updatePreference(AuthorDTO user, String key, String value) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("UPDATE preferences SET value = ? WHERE nick = ? and `key` = ?");
			ps.setString(1, value);
			ps.setString(2, user.getNick());
			ps.setString(3, key);
			ps.execute();
		} catch (SQLException e) {
			LOG.error("Cannot update preference key=" + key + " value=" + value + " for user " + user.getNick(), e);
		} finally {
			close(null, ps, conn);
		}
	}

	private long insertQuote(QuoteDTO quote) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
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
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
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

	protected List<MessageDTO> getMessages(ResultSet rs, final boolean search) throws SQLException {
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
			message.setIsVisible(rs.getBoolean("visible"));

			if (search) {
				message.setSearchRelevance(rs.getDouble("relevance"));
				message.setSearchCount(rs.getInt("count"));
			}

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
			message.setIsVisible(rs.getBoolean("visible"));
			messages.add(message);
		}
		return messages;
	}

	private int countMessages(String forum, Connection conn) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (forum == null) {
				ps = conn.prepareStatement("SELECT value FROM sysinfo WHERE `key` = 'messages.total'");
			} else {
				ps = conn.prepareStatement("SELECT value FROM sysinfo WHERE `key` = ?");
				ps.setString(1, "messages.forum." + forum);
			}
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("value");
			}
		} catch (SQLException e) {
			LOG.error("Cannot count messages", e);
		} finally {
			close(rs, ps, null);
		}
		return -1;
	}

	private int countThreads(String forum, Connection conn) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (forum == null) {
				ps = conn.prepareStatement("SELECT value FROM sysinfo WHERE `key` = 'threads.total'");
			} else {
				ps = conn.prepareStatement("SELECT value FROM sysinfo WHERE `key` = ?");
				ps.setString(1, "threads.forum." + forum);
			}
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("value");
			}
		} catch (SQLException e) {
			LOG.error("Cannot count threads", e);
		} finally {
			close(rs, ps, null);
		}
		return -1;
	}

	private int countMessagesByAuthor(String author, String forum, Connection conn) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringBuilder query = new StringBuilder("SELECT count(id) AS nr FROM messages WHERE author = ?");
			int i = 1;
			if ("".equals(forum)) {
				query.append(" AND forum IS NULL");
			} else if (forum != null) {
				query.append(" AND forum = ?");
			}
			ps = conn.prepareStatement(query.toString());
			ps.setString(i++, author);
			if (StringUtils.isNotEmpty(forum)) {
				ps.setString(i++, forum);
			}
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("nr");
			}
		} catch (SQLException e) {
			LOG.error("Cannot count messages", e);
		} finally {
			close(rs, ps, null);
		}
		return -1;
	}

	protected int getNumberOfMessages(long threadId) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
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

	@Override
	public void moveThreadTree(long rootMessageId, final String destForum) {
		// forum originale
		MessageDTO msg = getMessage(rootMessageId);
		String forum = msg.getForum();
		forum = forum == null ? "" : forum;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			sql.append("SELECT id FROM messages WHERE parentId = ?");
			ps = conn.prepareStatement(sql.toString());
			Stack<Long> parents = new Stack<Long>();
			ArrayList<Long> messages = new ArrayList<Long>();
			parents.push(rootMessageId);
			Long currentId = rootMessageId;
			while (!parents.isEmpty()) {
				currentId = parents.pop();
				messages.add(currentId);
				LOG.debug(sql.toString()+currentId);
				ps.setLong(1, currentId);
				rs = ps.executeQuery();
				while (rs.next()) {
					if (rs.getLong(1) != currentId.longValue()) {
						parents.push(rs.getLong(1));
					}
				}
				rs.close();
			}
			// setta a tutti i messaggi il threadId = rootMessageId
			ps.close();
			sql.setLength(0);
			sql.append("UPDATE messages SET threadId = ? , forum = '").append(destForum).append("' WHERE id IN (");
			// sono long, non temo injection io
			for (Long id : messages) {
				sql.append(id).append(',');
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(')');
			LOG.debug(sql);
			ps = conn.prepareStatement(sql.toString());
			ps.setLong(1, rootMessageId);
			int res = ps.executeUpdate();
			if (res != messages.size()) throw new SQLException("AGGIORNATI "+res+" recordz! ids: "+messages);
			ps.close();
			sql.setLength(0);
			sql.append("UPDATE messages SET parentId = id WHERE id = ").append(messages.get(0).toString());
			LOG.debug(sql);
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			// update numero di messaggi
			ps = conn.prepareStatement("UPDATE sysinfo SET value = value + ? WHERE `key` = ?");
			ps.setInt(1, res);
			ps.setString(2, "messages.forum." + destForum);
			ps.execute();
			ps.setInt(1, 1);
			ps.setString(2, "threads.forum." + destForum);
			ps.execute();
			ps.setInt(1, -1 * res);
			ps.setString(2, "messages.forum." + forum);
			ps.execute();
			if (msg.getId() == msg.getThreadId()) {
				// spostato tutto il thread
				ps.setInt(1, -1);
				ps.setString(2, "threads.forum." + forum);
				ps.execute();
			}
			conn.commit();
		} catch (SQLException e) {
			LOG.error("Pedonize failed!", e);
		} finally {
			close(rs, ps, conn);
		}
	}

	@Override
	public void restoreOrHideMessage(long msgId, boolean visible) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("UPDATE messages SET visible = ? WHERE id = ?");
			ps.setBoolean(1, visible);
			ps.setLong(2, msgId);
			ps.executeUpdate();
		} catch (SQLException e) {
			LOG.error("Cannot hide/restore message " + msgId, e);
		} finally {
			close(rs, ps, conn);
		}
	}

	@Override
	public String getSysinfoValue(String key) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// increase for this forum
			conn = getConnection();
			ps = conn.prepareStatement("SELECT value FROM sysinfo WHERE `key` = ?");
			ps.setString(1, key);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("value");
			}
		} catch (SQLException e) {
			LOG.error("Cannot get sysinfo value for " + key, e);
		} finally {
			close(rs, ps, conn);
		}
		return null;
	}

	@Override
	public void setSysinfoValue(String key, String value) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = null;
		try {
			if (getSysinfoValue(key) == null) {
				// insert
				query = "INSERT INTO sysinfo (`key`, value) VALUES (?, ?)";
				conn = getConnection();
				ps = conn.prepareStatement(query);
				ps.setString(1, key);
				ps.setString(2, value);
			} else {
				// update
				query = "UPDATE sysinfo SET value=? WHERE `key`=?";
				conn = getConnection();
				ps = conn.prepareStatement(query);
				ps.setString(1, value);
				ps.setString(2, key);
			}
			ps.execute();
		} catch (SQLException e) {
			LOG.error("Cannot set sysinfo value for " + key, e);
		} finally {
			close(rs, ps, conn);
		}
	}

	private void increaseNumberOfMessages(String forum, boolean isNewThread) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		forum = forum == null ? "" : forum;
		try {
			// increase for this forum
			conn = getConnection();
			ps = conn.prepareStatement("UPDATE sysinfo SET value = value + 1 WHERE `key` = ?");
			ps.setString(1, "messages.forum." + forum);
			ps.execute();
			// increase total
			ps.setString(1, "messages.total");
			ps.execute();
			// increase threads
			if (isNewThread) {
				// increase for this forum
				ps.setString(1, "threads.forum." + forum);
				ps.execute();
				// increase total
				ps.setString(1, "threads.total");
				ps.execute();
			}
		} catch (SQLException e) {
			LOG.error("Cannot increase number of messages of forum " + forum, e);
		} finally {
			close(rs, ps, conn);
		}
	}

	private PollDTO getPoll(Connection conn, ResultSet rs) throws SQLException {
		PollDTO pollDTO = new PollDTO();
		pollDTO.setId(rs.getLong("id"));
		pollDTO.setTitle(rs.getString("title"));
		pollDTO.setAuthor(rs.getString("author"));
		pollDTO.setText(rs.getString("text"));
		pollDTO.setCreationDate(rs.getTimestamp("creationDate"));
		pollDTO.setUpdateDate(rs.getTimestamp("updateDate"));
		pollDTO.setPollQuestions(getPollQuestion(conn, pollDTO.getId()));
		pollDTO.setVoterNicks(getPollVoterNicks(conn, pollDTO.getId()));
		return pollDTO;
	}

	private List<PollQuestion> getPollQuestion(Connection conn, long pollId) throws SQLException {
		List<PollQuestion> res = new ArrayList<PollQuestion>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM poll_question WHERE pollId = ? ORDER BY sequence ASC");
			ps.setLong(1, pollId);
			rs = ps.executeQuery();
			PollQuestion question;
			while (rs.next()) {
				question = new PollQuestion();
				question.setPollId(pollId);
				question.setSequence(rs.getInt("sequence"));
				question.setText(rs.getString("text"));
				question.setVotes(rs.getInt("votes"));
				res.add(question);
			}
		} finally {
			close(rs, ps, null);
		}
		return res;
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
			if (conn != null && !conn.getAutoCommit()) {
				conn.rollback();
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

	public boolean blockTorExitNodes() {
		return "checked".equals(getSysinfoValue("blockTorExitNodes"));
	}
	
	public long getLastId() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT MAX(id) FROM messages");
			rs = ps.executeQuery();
			if (rs.next()) return rs.getLong(1);
			else throw new SQLException("No rows in messages?????");
		} catch (SQLException e) {
			LOG.error("Cannot retrieve max(id) from messages table", e);
			return 0;
		} finally {
			close(rs, ps, conn);
		}
	}
}
