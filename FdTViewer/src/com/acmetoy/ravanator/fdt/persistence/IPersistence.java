package com.acmetoy.ravanator.fdt.persistence;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface IPersistence extends Serializable {

	public void init(Properties databaseConfig) throws Exception;

	public MessageDTO insertMessage(MessageDTO message);

	public List<String> getForums();

	/**
	 * 
	 * @param forum se null tutti i messaggi, se stringa vuota tutti i messaggi con forum NULL ("Principale")
	 * @param limit
	 * @param page
	 * @param hideProcCatania
	 * @return
	 */
	public ThreadsDTO getThreads(String forum, int limit, int page, boolean hideProcCatania);

	/**
	 * 
	 * @param forum se null tutti i messaggi, se stringa vuota tutti i messaggi con forum NULL ("Principale")
	 * @param limit
	 * @param page
	 * @param hideProcCatania
	 * @return
	 */
	public ThreadsDTO getThreadsByLastPost(String forum, int limit, int page, boolean hideProcCatania);

	public List<ThreadDTO> getAuthorThreadsByLastPost(String author, int limit, int page, boolean hideProcCatania);

	public MessageDTO getMessage(long id);

	/**
	 * 
	 * @param forum se null tutti i messaggi, se stringa vuota tutti i messaggi con forum NULL ("Principale")
	 * @param limit
	 * @param page
	 * @param hideProcCatania
	 * @return
	 */
	public MessagesDTO getMessages(String forum, int limit, int page, boolean hideProcCatania);

	public List<MessageDTO> getMessagesByThread(long threadId);

	/**
	 * 
	 * @param forum se null tutti i messaggi, se stringa vuota tutti i messaggi con forum NULL ("Principale")
	 * @param limit
	 * @param page
	 * @param hideProcCatania
	 * @return
	 */
	public MessagesDTO getMessagesByAuthor(String author, String forum, int pageSize, int page);

	public AuthorDTO getAuthor(String nick);
	
	public List<AuthorDTO> getAuthors(boolean onlyActive);

	public void updateAuthor(AuthorDTO author);

	public boolean updateAuthorPassword(AuthorDTO author, String newPassword);

	public List<MessageDTO> searchMessages(String search, SearchMessagesSort sort, int pageSize, int pageNr);

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

	public Map<String, String> getPreferences(AuthorDTO user);

	public Map<String, String> setPreference(AuthorDTO user, String key, String value);

	public List<String> searchAuthor(String searchString);

	public void pedonizeThreadTree(long rootMessageId);
	
	public void createPoll(PollDTO pollDTO);
	
	public boolean updatePollQuestion(PollQuestion pollQuestion, AuthorDTO user);
	
	public PollsDTO getPollsByDate(int limit, int page);
	
	public PollsDTO getPollsByLastVote(int limit, int page);

	public PollDTO getPoll(long pollId);

	public void restoreOrHideMessage(long msgId, boolean visible);

}
