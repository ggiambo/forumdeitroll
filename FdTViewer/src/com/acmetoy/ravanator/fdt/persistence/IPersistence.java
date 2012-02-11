package com.acmetoy.ravanator.fdt.persistence;

import java.util.List;
import java.util.Properties;

public interface IPersistence {

	public void init(Properties databaseConfig) throws Exception;

	public MessageDTO insertMessage(MessageDTO message);

	public List<String> getForums();

	public List<ThreadDTO> getThreads(int limit, int page);

	public List<ThreadDTO> getThreadsByLastPost(int limit, int page);

	public List<ThreadDTO> getAuthorThreadsByLastPost(String author, int limit, int page);

	public MessageDTO getMessage(long id);

	public MessagesDTO getMessagesByDate(int limit, int page);

	public List<MessageDTO> getMessagesByThread(long threadId);

	public MessagesDTO getMessagesByAuthor(String author, int pageSize, int page);

	public MessagesDTO getMessagesByForum(String forum, int pageSize, int page);

	public AuthorDTO getAuthor(String nick);

	public void updateAuthor(AuthorDTO author);

	public boolean updateAuthorPassword(AuthorDTO author, String newPassword);

	public List<MessageDTO> searchMessages(String search, int pageSize, int pageNr);

	public AuthorDTO registerUser(String nick, String password);

	public List<QuoteDTO> getQuotes(AuthorDTO author);

	public void insertUpdateQuote(QuoteDTO quote);

	public void removeQuote(QuoteDTO quote);

	public QuoteDTO getRandomQuote();

	public List<PrivateMsgDTO> getSentPvts(AuthorDTO author, int limit, int pageNr);

	public List<PrivateMsgDTO> getInbox(AuthorDTO author, int limit, int pageNr);
	
	public int getInboxPages(AuthorDTO author);
	
	public int getOutboxPages(AuthorDTO author);

	public boolean sendAPvtForGreatGoods(AuthorDTO author, PrivateMsgDTO privateMsg, String[] recipients);

	public void notifyRead(AuthorDTO recipient, PrivateMsgDTO privateMsg);

	public boolean checkForNewPvts(AuthorDTO author);

	public void deletePvt(long pvt_id, AuthorDTO user);

	public PrivateMsgDTO getPvtDetails(long pvt_id, AuthorDTO user);

	public Properties getPreferences(AuthorDTO user);

	public Properties setPreference(AuthorDTO user, String key, String value);

	void pedonizeThread(long threadId);

	int countThreads();
}
