package com.forumdeitroll.persistence.dao;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.forumdeitroll.persistence.AdDTO;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.BookmarkDTO;
import com.forumdeitroll.persistence.DigestArticleDTO;
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
import com.forumdeitroll.persistence.TagDTO;
import com.forumdeitroll.persistence.ThreadsDTO;

public class DAOFacade implements IPersistence {

	private static final long serialVersionUID = 1L;

	protected AuthorsDAO authorsDAO;
	protected ThreadsDAO threadsDAO;
	protected MessagesDAO messagesDAO;
	protected PollsDAO pollsDAO;

	public void init(DSLContext jooq) {
		authorsDAO = new AuthorsDAO(jooq);
		threadsDAO = new ThreadsDAO(jooq);
		messagesDAO = new MessagesDAO(jooq);
		pollsDAO = new PollsDAO(jooq);
	}

	public void init(Properties databaseConfig) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		String host = databaseConfig.getProperty("host");
		String port = databaseConfig.getProperty("port");
		String username = databaseConfig.getProperty("username");
		String password = databaseConfig.getProperty("password");
		String dbname = databaseConfig.getProperty("dbname");
		String url = "jdbc:mysql://" + host + ":" + port + "/" + dbname + "?useUnicode=yes&characterEncoding=UTF-8";
		DSLContext jooq = setupDataSource(url, username, password);
		init(jooq);

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
	public MessageDTO insertMessage(MessageDTO message) {
		return messagesDAO.insertMessage(message);
	}

	@Override
	public List<String> getForums() {
		return null;
	}

	@Override
	public ThreadsDTO getThreads(String forum, int limit, int page, List<String> hiddenForums) {
		return threadsDAO.getThreads(forum, limit, page, hiddenForums);
	}

	@Override
	public ThreadsDTO getThreadsByLastPost(String forum, int limit, int page, List<String> hiddenForums) {
		return threadsDAO.getThreadsByLastPost(forum, limit, page, hiddenForums);
	}

	@Override
	public ThreadsDTO getAuthorThreadsByLastPost(String author, int limit, int page, List<String> hiddenForums) {
		return threadsDAO.getAuthorThreadsByLastPost(author, limit, page, hiddenForums);
	}

	@Override
	public MessageDTO getMessage(long id) {
		return messagesDAO.getMessage(id);
	}

	@Override
	public MessagesDTO getMessages(String forum, String author, int limit, int page, List<String> hiddenForums) {
		return messagesDAO.getMessages(forum, author, limit, page, hiddenForums);
	}

	@Override
	public List<MessageDTO> getMessagesByThread(long threadId) {
		return messagesDAO.getMessagesByThread(threadId);
	}

	@Override
	public List<QuoteDTO> getQuotes(AuthorDTO author) {
		return null;
	}

	@Override
	public List<QuoteDTO> getAllQuotes() {
		return null;
	}

	@Override
	public void insertUpdateQuote(QuoteDTO quote) {

	}

	@Override
	public void removeQuote(QuoteDTO quote) {

	}

	@Override
	public List<PrivateMsgDTO> getSentPvts(AuthorDTO author, int limit, int pageNr) {
		return null;
	}

	@Override
	public List<PrivateMsgDTO> getInbox(AuthorDTO author, int limit, int pageNr) {
		return null;
	}

	@Override
	public int getInboxPages(AuthorDTO author) {
		return 0;
	}

	@Override
	public int getOutboxPages(AuthorDTO author) {
		return 0;
	}

	@Override
	public boolean sendAPvtForGreatGoods(AuthorDTO author, PrivateMsgDTO privateMsg, String[] recipients) {
		return false;
	}

	@Override
	public void notifyRead(AuthorDTO recipient, PrivateMsgDTO privateMsg) {

	}

	@Override
	public void notifyUnread(AuthorDTO recipient, PrivateMsgDTO privateMsg) {

	}

	@Override
	public boolean checkForNewPvts(AuthorDTO author) {
		return false;
	}

	@Override
	public void deletePvt(long pvt_id, AuthorDTO user) {

	}

	@Override
	public PrivateMsgDTO getPvtDetails(long pvt_id, AuthorDTO user) {
		return null;
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
	public List<MessageDTO> searchMessages(String search, SearchMessagesSort sort, int pageSize, int pageNr) {
		return null;
	}

	@Override
	public Map<String, String> getPreferences(AuthorDTO user) {
		return authorsDAO.getPreferences(user);
	}

	@Override
	public Map<String, String> setPreference(AuthorDTO user, String key, String value) {
		return null;
	}

	@Override
	public List<String> searchAuthor(String searchString) {
		return null;
	}

	@Override
	public void moveThreadTree(long rootMessageId, String destForum) {

	}

	@Override
	public long createPoll(PollDTO pollDTO) {
		return pollsDAO.createPoll(pollDTO);
	}

	@Override
	public boolean updatePollQuestion(PollQuestion pollQuestion, AuthorDTO user) {
		return pollsDAO.updatePollQuestion(pollQuestion, user);
	}

	@Override
	public PollsDTO getPollsByDate(int limit, int page) {
		return pollsDAO.getPollsByDate(limit,  page);
	}

	@Override
	public PollsDTO getPollsByLastVote(int limit, int page) {
		return pollsDAO.getPollsByLastVote(limit,  page);
	}

	@Override
	public PollDTO getPoll(long pollId) {
		return pollsDAO.getPoll(pollId);
	}

	@Override
	public void restoreOrHideMessage(long msgId, int visible) {

	}

	@Override
	public void setSysinfoValue(String key, String value) {

	}

	@Override
	public String getSysinfoValue(String key) {
		return null;
	}

	@Override
	public boolean blockTorExitNodes() {
		return false;
	}

	@Override
	public List<NotificationDTO> getNotifications(String fromNick, String toNick) {
		return null;
	}

	@Override
	public void removeNotification(String fromNick, String toNick, long id) {

	}

	@Override
	public void createNotification(String fromNick, String toNick, long id) {

	}

	@Override
	public long getLastId() {
		return 0;
	}

	@Override
	public List<BookmarkDTO> getBookmarks(AuthorDTO owner) {
		return null;
	}

	@Override
	public boolean existsBookmark(BookmarkDTO bookmark) {
		return false;
	}

	@Override
	public void addBookmark(BookmarkDTO bookmark) {

	}

	@Override
	public void deleteBookmark(BookmarkDTO bookmark) {

	}

	@Override
	public void editBookmark(BookmarkDTO bookmark) {

	}

	@Override
	public int like(long msgId, String nick, boolean upvote) {
		return 0;
	}

	@Override
	public List<DigestArticleDTO> getReadersDigest() {
		return null;
	}

	@Override
	public TagDTO addTag(TagDTO tag) {
		return null;
	}

	@Override
	public void deleTag(TagDTO tag, boolean isAdmin) {

	}

	@Override
	public void getTags(MessagesDTO messages) {

	}

	@Override
	public MessagesDTO getMessagesByTag(int limit, int page, long t_id, List<String> hiddenForums) {
		return messagesDAO.getMessagesByTag(limit, page, t_id, hiddenForums);
	}

	@Override
	public String getMessageTitle(long id) {
		return messagesDAO.getMessageTitle(id);
	}

	@Override
	public List<String> getTitles() {
		return null;
	}

	@Override
	public void setTitles(List<String> titles) {

	}

	@Override
	public void setHiddenForums(AuthorDTO loggedUser, List<String> hiddenForum) {

	}

	@Override
	public List<String> getHiddenForums(AuthorDTO loggedUser) {
		return null;
	}

	@Override
	public List<AdDTO> getAllAds() {
		return null;
	}

	@Override
	public void setAllAds(List<AdDTO> ads) {

	}
}

