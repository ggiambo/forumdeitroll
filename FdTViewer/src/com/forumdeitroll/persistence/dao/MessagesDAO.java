package com.forumdeitroll.persistence.dao;

import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.MessagesDTO;
import com.forumdeitroll.persistence.SearchMessagesSort;
import com.forumdeitroll.persistence.jooq.tables.records.MessagesRecord;
import com.forumdeitroll.persistence.jooq.tables.records.SysinfoRecord;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;

import java.sql.*;
import java.util.List;


import static com.forumdeitroll.persistence.jooq.Tables.*;
import static com.forumdeitroll.persistence.sql.mysql.Utf8Mb4Conv.mb4safe;

public class MessagesDAO extends BaseDAO {

	public MessagesDAO(DSLContext jooq) {
		super(jooq);
	}

	public MessageDTO insertMessage(MessageDTO message) {
		if (message.getParentId() != -1) {
			if (message.getId() == -1) {
				return getMessage(insertReplyMessage(message));
			}
			return getMessage(insertEditMessage(message));
		}
		return getMessage(insertNewMessage(message));
	}


	public MessageDTO getMessage(long id) {
		MessagesRecord record = jooq.selectFrom(MESSAGES)
				.where(MESSAGES.ID.equal((int) id))
				.fetchAny();

		if (record == null) {
			return new MessageDTO();
		}

		return recordToDTO(record, false);

	}

	public List<MessageDTO> getMessagesByThread(long threadId) {
		return null; // TODO
	}

	public List<MessageDTO> searchMessages(String search, SearchMessagesSort sort, int pageSize, int pageNr) {
		return null; // TODO
	}

	public String getMessageTitle(long id) {
		return null; // TODO
	}

	public MessagesDTO getMessages(String forum, String author, int limit, int page, List<String> hiddenForums) {
		return null; // TODO
	}

	public MessagesDTO getMessagesByTag(int limit, int page, long t_id, List<String> hiddenForums) {
		return null; // TODO
	}

	private long insertReplyMessage(MessageDTO message) {

		MessagesRecord record = jooq.insertInto(MESSAGES)
				.set(MESSAGES.PARENTID, (int) message.getParentId())
				.set(MESSAGES.THREADID, (int) message.getThreadId())
				.set(MESSAGES.TEXT, mb4safe(message.getTextReal()))
				.set(MESSAGES.SUBJECT, mb4safe(message.getSubjectReal()))
				.set(MESSAGES.AUTHOR, message.getAuthor().getNick())
				.set(MESSAGES.FORUM, message.getForum())
				.set(MESSAGES.DATE, new Timestamp(message.getDate().getTime()))
				.set(MESSAGES.VISIBLE, (byte) message.getVisibleReal())
				.returning(MESSAGES.ID)
				.fetchOne();


		increaseNumberOfMessages(message.getForum(), false);

		long id = record.getId();
		updateLastIdInThread(message.getThreadId(), id);

		return id;
	}

	private long insertEditMessage(MessageDTO message) {

		jooq.update(MESSAGES)
				.set(MESSAGES.TEXT, mb4safe(message.getTextReal()))
				.set(MESSAGES.SUBJECT, mb4safe(message.getSubjectReal()))
				.where(MESSAGES.ID.equal((int) message.getId()));


		return message.getId();
	}

	private long insertNewMessage(MessageDTO message) {

		MessagesRecord record = jooq.insertInto(MESSAGES)
				.set(MESSAGES.PARENTID, -1)
				.set(MESSAGES.THREADID, -1)
				.set(MESSAGES.TEXT, mb4safe(message.getTextReal()))
				.set(MESSAGES.SUBJECT, mb4safe(message.getSubjectReal()))
				.set(MESSAGES.AUTHOR, message.getAuthor().getNick())
				.set(MESSAGES.FORUM, message.getForum())
				.set(MESSAGES.DATE, new Timestamp(message.getDate().getTime()))
				.set(MESSAGES.VISIBLE, (byte) message.getVisibleReal())
				.returning(MESSAGES.ID)
				.fetchOne();


		increaseNumberOfMessages(message.getForum(), true);

		int id = record.getId();
		record.setParentid(id);
		record.setThreadid(id);

		insertThread(id);

		return id;
	}

	private void increaseNumberOfMessages(String forum, boolean isNewThread) {

		forum = forum == null ? "" : forum;

		SysinfoRecord record;
		record = jooq.selectFrom(SYSINFO)
				.where(SYSINFO.KEY.equal("messages.forum." + forum))
				.fetchOne();
		record.setValue(SYSINFO.VALUE, record.getValue(SYSINFO.VALUE) + 1);

		record = jooq.selectFrom(SYSINFO)
				.where(SYSINFO.KEY.equal("messages.total"))
				.fetchOne();
		record.setValue(SYSINFO.VALUE, record.getValue(SYSINFO.VALUE) + 1);

		if (isNewThread) {
			record = jooq.selectFrom(SYSINFO)
					.where(SYSINFO.KEY.equal("threads.forum." + forum))
					.fetchOne();
			record.setValue(SYSINFO.VALUE, record.getValue(SYSINFO.VALUE) + 1);

			record = jooq.selectFrom(SYSINFO)
					.where(SYSINFO.KEY.equal("threads.total"))
					.fetchOne();
			record.setValue(SYSINFO.VALUE, record.getValue(SYSINFO.VALUE) + 1);
		}

	}

	private void updateLastIdInThread(long threadId, long lastId) {
		jooq.update(THREADS)
				.set(THREADS.LASTID, (int) lastId)
				.where(THREADS.THREADID.equal((int) threadId));
	}

	private void insertThread(long threadId) {
		jooq.insertInto(THREADS)
				.values(THREADS.LASTID, threadId)
				.values(THREADS.THREADID, threadId);
	}

	private MessageDTO recordToDTO(Record record, boolean search) {
		MessageDTO message = new MessageDTO();
		message.setId(record.getValue(MESSAGES.ID));
		message.setParentId(record.getValue(MESSAGES.PARENTID));
		message.setThreadId(record.getValue(MESSAGES.THREADID));
		message.setText(record.getValue(MESSAGES.TEXT));
		message.setSubject(record.getValue(MESSAGES.SUBJECT));
		message.setAuthor(getAuthor(record.getValue(MESSAGES.AUTHOR)));
		message.setForum(record.getValue(MESSAGES.FORUM));
		message.setDate(record.getValue(MESSAGES.DATE));
		message.setIsVisible(record.getValue(MESSAGES.VISIBLE));
		message.setRank(record.getValue(MESSAGES.RANK));

		// TODO, usati nel search
//		if (search) {
//			message.setSearchRelevance(rs.getDouble("relevance"));
//			message.setSearchCount(rs.getInt("count"));
//		}

		return message;
	}

}
