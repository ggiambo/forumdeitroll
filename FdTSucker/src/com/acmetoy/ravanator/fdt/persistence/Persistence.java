package com.acmetoy.ravanator.fdt.persistence;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.FdTConfig;

public abstract class Persistence {

	private static final Logger LOG = Logger.getLogger(Persistence.class);

	private static Persistence instance;

	public static synchronized Persistence getInstance() throws Exception {
		if (instance == null) {
			try {
				String persistenceNickName = FdTConfig.getProperty("persistence.nickName");
				String databaseClass = FdTConfig.getProperty(persistenceNickName + ".class");
				Class<? extends Persistence> c = Class.forName(databaseClass).asSubclass(Persistence.class);
				Constructor<? extends Persistence> cons = c.getConstructor(Properties.class);
				instance = cons.newInstance(FdTConfig.getDatabaseConfig(persistenceNickName));
			} catch (Exception e) {
				LOG.error("Cannot instantiate Persistence " + FdTConfig.getProperty("persistence.nickName"), e);
				throw e;
			}
		}
		return instance;
	}

	public abstract void insertMessage(MessageDTO message);

	public abstract boolean hasMessage(long id);

	public abstract List<String> getForums();

	public abstract List<ThreadDTO> getThreads(int limit, int page);

	public abstract MessageDTO getMessage(long id);

	public abstract List<MessageDTO> getMessagesByDate(int limit);

	public abstract List<MessageDTO> getMessagesByDate(int limit, int page);

	public abstract List<MessageDTO> getMessagesByThread(long threadId);

	public abstract List<MessageDTO> getMessagesByAuthor(String author, int pageSize, int page);

	public abstract List<MessageDTO> getMessagesByForum(String forum, int pageSize, int page);

	public abstract long getLastMessageId();

	public abstract boolean hasAuthor(String nick);

	public abstract void insertAuthor(AuthorDTO author);

	public abstract void updateAuthor(AuthorDTO author);

	public abstract AuthorDTO getAuthor(String nick);

	public abstract List<MessageDTO> searchMessages(String search, int pageSize, int pageNr);

	public abstract long countMessages();

	public abstract List<Long> getParentIds(int limit, int page);

	public abstract void updateMessageParentId(long id, long parentId);
	
}
