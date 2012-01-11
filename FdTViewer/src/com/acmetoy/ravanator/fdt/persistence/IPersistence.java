package com.acmetoy.ravanator.fdt.persistence;

import java.util.List;
import java.util.Properties;

public interface IPersistence {
	
	public void init(Properties databaseConfig) throws Exception;

	public MessageDTO insertMessage(MessageDTO message);

	public List<String> getForums();

	public List<ThreadDTO> getThreads(int limit, int page);

	public MessageDTO getMessage(long id);

	public List<MessageDTO> getMessagesByDate(int limit, int page);

	public List<MessageDTO> getMessagesByThread(long threadId);

	public List<MessageDTO> getMessagesByAuthor(String author, int pageSize, int page);

	public List<MessageDTO> getMessagesByForum(String forum, int pageSize, int page);

	public AuthorDTO getAuthor(String nick);

	public AuthorDTO getAuthor(String nick, String MD5password);

	public void updateAuthor(AuthorDTO author);
	
	public boolean updateAuthorPassword(String nick, String oldMD5Pass, String newMD5password);
	
	public List<MessageDTO> searchMessages(String search, int pageSize, int pageNr);
	
	public AuthorDTO registerUser(String nick, String MD5password);

	public List<QuoteDTO> getQuotes(AuthorDTO author);

	public void insertUpdateQuote(QuoteDTO quote);

	public void removeQuote(QuoteDTO quote);

	public String getRandomQuote();

}
