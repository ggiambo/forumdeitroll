package com.forumdeitroll.persistence.dao;

import static com.forumdeitroll.persistence.jooq.Tables.MESSAGES;
import static com.forumdeitroll.persistence.jooq.Tables.SYSINFO;
import static com.forumdeitroll.persistence.jooq.Tables.TAGS_BIND;
import static com.forumdeitroll.persistence.jooq.Tables.THREADS;
import static com.forumdeitroll.persistence.sql.mysql.Utf8Mb4Conv.mb4safe;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;

import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.MessagesDTO;
import com.forumdeitroll.persistence.SearchMessagesSort;
import com.forumdeitroll.persistence.jooq.tables.records.MessagesRecord;
import com.forumdeitroll.persistence.jooq.tables.records.SysinfoRecord;

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
		Result<MessagesRecord> records = jooq.selectFrom(MESSAGES)
				.where(MESSAGES.THREADID.eq((int) threadId))
				.orderBy(MESSAGES.ID.asc())
				.fetch();

		List<MessageDTO> res = new ArrayList<MessageDTO>(records.size());
		for (MessagesRecord record : records) {
			res.add(recordToDTO(record, false));
		}

		return res;
	}

	public List<MessageDTO> searchMessages(String search, SearchMessagesSort sort, int pageSize, int pageNr) {
		return null; // TODO
	}

	public String getMessageTitle(long id) {
		String title = jooq.select(MESSAGES.SUBJECT)
				.from(MESSAGES)
				.where(MESSAGES.ID.eq((int) id))
				.fetchOne(MESSAGES.SUBJECT);

		return title;
	}

	public MessagesDTO getMessages(String forum, String author, int limit, int page, List<String> hiddenForums) {

		SelectConditionStep<MessagesRecord> select = jooq.selectFrom(MESSAGES).where("1=1");

		if ("".equals(forum)) {
			select = select.and(MESSAGES.FORUM.isNull());
		} else if (forum == null) {
			if (hiddenForums != null && !hiddenForums.isEmpty()) {
				select = select.and(
						MESSAGES.FORUM.isNull().or(MESSAGES.FORUM.notIn(hiddenForums))
						);
			}
		} else {
			select = select.and(MESSAGES.FORUM.eq(forum));
		}
		if (StringUtils.isNotEmpty(author)) {
			select = select.and(MESSAGES.AUTHOR.eq(author));
		}

		Result<MessagesRecord> records = select.orderBy(MESSAGES.ID.desc())
				.limit(limit)
				.offset(limit*page)
				.fetch();

		int messagesCount = countMessages(forum);
		if (hiddenForums != null && !hiddenForums.isEmpty() && forum == null) {
			for (String hiddenForum : hiddenForums) {
				messagesCount -= countMessages(hiddenForum);
			}
		}

		List<MessageDTO> res = new ArrayList<MessageDTO>(records.size());
		for (MessagesRecord record : records) {
			res.add(recordToDTO(record, false));
		}

		return new MessagesDTO(res, messagesCount);
	}

	public MessagesDTO getMessagesByTag(int limit, int page, long t_id, List<String> hiddenForums) {

		SelectConditionStep<Record> where = jooq.select(MESSAGES.fields())
				.from(MESSAGES)
				.join(TAGS_BIND).on(MESSAGES.ID.eq(TAGS_BIND.M_ID))
				.where(TAGS_BIND.T_ID.eq((int) t_id));

		if (hiddenForums != null && !hiddenForums.isEmpty()) {
			where = where.and(
					MESSAGES.FORUM.isNull().or(MESSAGES.FORUM.notIn(hiddenForums))
					);
		}

		Result<Record> records = where.orderBy(TAGS_BIND.M_ID.desc())
				.limit(limit)
				.offset(limit*page)
				.fetch();

		List<MessageDTO> messages = new ArrayList<MessageDTO>(records.size());
		for (Record record : records) {
			messages.add(recordToDTO(record, false));
		}

		Object value = jooq.selectCount()
				.from(TAGS_BIND)
				.where(TAGS_BIND.T_ID.eq((int) t_id))
				.fetchOne()
				.getValue(0);

		Integer nrOfMessages = (Integer)value;

		return new MessagesDTO(messages, nrOfMessages);
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
			.where(MESSAGES.ID.equal((int) message.getId()))
			.execute();

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
		record.setParentid(id)
			.setThreadid(id)
			.update();

		insertThread(id);

		return id;
	}

	private void increaseNumberOfMessages(String forum, boolean isNewThread) {

		forum = forum == null ? "" : forum;

		int nr = 0;
		SysinfoRecord record;
		record = jooq.selectFrom(SYSINFO)
				.where(SYSINFO.KEY.equal("messages.forum." + forum))
				.fetchOne();
		nr = Integer.parseInt(record.getValue());
		nr++;
		record.setValue(SYSINFO.VALUE, Integer.toString(nr));

		record = jooq.selectFrom(SYSINFO)
				.where(SYSINFO.KEY.equal("messages.total"))
				.fetchOne();
		nr = Integer.parseInt(record.getValue());
		nr++;
		record.setValue(SYSINFO.VALUE, Integer.toString(nr));

		if (isNewThread) {
			record = jooq.selectFrom(SYSINFO)
					.where(SYSINFO.KEY.equal("threads.forum." + forum))
					.fetchOne();
			nr = Integer.parseInt(record.getValue());
			nr++;
			record.setValue(SYSINFO.VALUE, Integer.toString(nr));

			record = jooq.selectFrom(SYSINFO)
					.where(SYSINFO.KEY.equal("threads.total"))
					.fetchOne();
			nr = Integer.parseInt(record.getValue());
			nr++;
			record.setValue(SYSINFO.VALUE, Integer.toString(nr));
		}

	}

	private int countMessages(String forum) {

		String key;
		if (forum == null) {
			key = "messages.total";
		} else {
			key = "messages.forum." + forum;
		}

		String value = jooq.select(SYSINFO.VALUE)
				.from(SYSINFO)
				.where(SYSINFO.KEY.eq(key))
				.fetchOne()
				.getValue(SYSINFO.VALUE);

		return Integer.parseInt(value);

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
