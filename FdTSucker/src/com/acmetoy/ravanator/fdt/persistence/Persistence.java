package com.acmetoy.ravanator.fdt.persistence;

import java.util.List;

public interface Persistence {
	
	public void insertMessage(MessageDTO message);
	
	public boolean hasMessage(long id);
	
	public MessageDTO getMessage(long id);
	
	public List<MessageDTO> getMessagesByDate(int limit);
	
	public List<MessageDTO> getMessagesByDate(int limit, int page);
	
	public List<MessageDTO> getMessagesByThread(long threadId);

	public long getLastMessageId();
	
	public boolean hasAuthor(String nick);

	public void insertAuthor(AuthorDTO author);

	public void updateAuthor(AuthorDTO author);
	
	public AuthorDTO getAuthor(String nick);

	public List<MessageDTO> getMessagesByAuthor(String author, int pageSize, int page);

}
