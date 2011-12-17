package com.acmetoy.ravanator.fdt.persistence;

import java.util.List;
import java.util.Properties;

public interface IPersistence {
	
	public void init(Properties databaseConfig) throws Exception;

	public void insertMessage(MessageDTO message);

	public boolean hasMessage(long id);

	public List<String> getForums();

	public List<ThreadDTO> getThreads(int limit, int page);

	public MessageDTO getMessage(long id);

	public List<MessageDTO> getMessagesByDate(int limit, int page);

	public List<MessageDTO> getMessagesByThread(long threadId);

	public List<MessageDTO> getMessagesByAuthor(String author, int pageSize, int page);

	public List<MessageDTO> getMessagesByForum(String forum, int pageSize, int page);

	public long getLastMessageId();

	public void insertUpdateAuthor(AuthorDTO author);

	public AuthorDTO getAuthor(String nick);

	public List<MessageDTO> searchMessages(String search, int pageSize, int pageNr);

}
