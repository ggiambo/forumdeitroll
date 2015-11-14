package com.forumdeitroll.persistence.dao;

import com.forumdeitroll.persistence.ThreadDTO;
import com.forumdeitroll.persistence.ThreadsDTO;
import com.forumdeitroll.persistence.jooq.tables.Messages;
import com.forumdeitroll.persistence.jooq.tables.records.MessagesRecord;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.forumdeitroll.persistence.jooq.Tables.*;

public class ThreadsDAO extends BaseDAO {

	public ThreadsDAO(DSLContext jooq) {
		super(jooq);
	}

	public ThreadsDTO getThreadsByLastPost(String forum, int limit, int page, List<String> hiddenForums) {

		List<Field<?>> fields = new ArrayList<Field<?>>();
		fields.addAll(Arrays.asList(MESSAGES.fields()));
		fields.add(THREADS.LASTID);

		SelectConditionStep<Record> where = jooq.select(fields)
				.from(MESSAGES.join(THREADS).on(MESSAGES.ID.equal(THREADS.LASTID)))
				.where(DSL.trueCondition());

		if ("".equals(forum)) {
			where = where.and(MESSAGES.FORUM.isNull());
		} else if (forum == null) {
			if (hiddenForums != null && !hiddenForums.isEmpty()) {
				where = where.and(MESSAGES.FORUM.isNull().or(MESSAGES.FORUM.notIn(hiddenForums)));
			}

		} else {
			where = where.and(MESSAGES.FORUM.equal(forum));
		}

		Result<Record> records = where.orderBy(THREADS.LASTID.desc())
				.limit(limit).offset(limit * page)
				.fetch();

		if (records.isEmpty()) {
			return new ThreadsDTO();
		}

		int threadsCount = countThreads(forum);
		if (hiddenForums != null && !hiddenForums.isEmpty() && forum == null) {
			for (String hiddenForum : hiddenForums) {
				threadsCount -= countMessages(hiddenForum);
			}
		}

		List<ThreadDTO> res = new ArrayList<ThreadDTO>(records.size());
		for (Record record : records) {
			res.add(recordToDTO(record, true));
		}

		return new ThreadsDTO(res, threadsCount);

	}

	public ThreadsDTO getThreadsByLastPostGroupByUser(String forum, int limit, int page, List<String> hiddenForums) {

		SelectConditionStep<Record> where =
			jooq.select(MESSAGES.fields())
			.from(MESSAGES)
			.join(THREADS).on(THREADS.THREADID.eq(MESSAGES.ID))
			.where(MESSAGES.AUTHOR.isNotNull())
		;

		if ("".equals(forum)) {
			where = where.and(MESSAGES.FORUM.isNull());
		} else if (forum == null) {
			if (hiddenForums != null && !hiddenForums.isEmpty()) {
				where = where.and(MESSAGES.FORUM.isNull().or(MESSAGES.FORUM.notIn(hiddenForums)));
			}

		} else {
			where = where.and(MESSAGES.FORUM.equal(forum));
		}

		Result<Record> records = where.orderBy(THREADS.THREADID.desc())
				.limit(limit).offset(limit * page)
				.fetch();

		if (records.isEmpty()) {
			return new ThreadsDTO();
		}

		int threadsCount = countThreads(forum);
		if (hiddenForums != null && !hiddenForums.isEmpty() && forum == null) {
			for (String hiddenForum : hiddenForums) {
				threadsCount -= countMessages(hiddenForum);
			}
		}

		List<ThreadDTO> res = new ArrayList<ThreadDTO>(records.size());
		for (Record record : records) {
			res.add(recordToDTO(record, false));
		}

		return new ThreadsDTO(res, threadsCount);

	}

	public ThreadsDTO getThreads(String forum, int limit, int page, List<String> hiddenForums) {

		SelectConditionStep<MessagesRecord> where = jooq.selectFrom(MESSAGES)
				.where(MESSAGES.THREADID.eq(MESSAGES.ID));


		if ("".equals(forum)) {
			where = where.and(MESSAGES.FORUM.isNull());
		} else if (forum == null) {
			if (hiddenForums != null && !hiddenForums.isEmpty()) {
				where = where.and(
						MESSAGES.FORUM.isNull().or(MESSAGES.FORUM.notIn(hiddenForums))
				);
			}
		} else {
			where = where.and(MESSAGES.FORUM.eq(forum));
		}

		Result<MessagesRecord> records = where.orderBy(MESSAGES.ID.desc())
				.limit(limit)
				.offset(limit * page)
				.fetch();

		int threadsCount = countThreads(forum);
		if (hiddenForums != null && !hiddenForums.isEmpty() && forum == null) {
			for (String hiddenForum : hiddenForums) {
				threadsCount -= countMessages(hiddenForum);
			}
		}

		List<ThreadDTO> res = new ArrayList<ThreadDTO>(records.size());
		for (MessagesRecord record : records) {
			res.add(recordToDTO(record, false));
		}

		return new ThreadsDTO(res, threadsCount);
	}

	public ThreadsDTO getAuthorThreadsByLastPost(String author, int limit, int page, List<String> hiddenForums) {

		Messages lastRow = MESSAGES.as("lastRow");
		Messages authorRows = MESSAGES.as("authorRows");

		List<Field<?>> fields = new ArrayList<Field<?>>();
		fields.addAll(Arrays.asList(lastRow.fields()));
		fields.add(THREADS.LASTID);

		SelectConditionStep<Record> where = jooq.select(fields)
				.from(lastRow)
				.join(THREADS).on(lastRow.ID.eq(THREADS.LASTID))
				.join(authorRows).on(THREADS.THREADID.eq(authorRows.THREADID))
				.where(authorRows.AUTHOR.eq(author));

		if (hiddenForums != null && !hiddenForums.isEmpty()) {
			where = where.and(
					authorRows.FORUM.isNull().or(authorRows.FORUM.notIn(hiddenForums))
			);
		}

		Result<Record> records = where.groupBy(THREADS.THREADID)
				.orderBy(THREADS.LASTID.desc())
				.limit(limit)
				.offset(limit * page)
				.fetch();

		List<ThreadDTO> res = new ArrayList<ThreadDTO>(records.size());
		for (Record record : records) {
			res.add(recordToDTO(record, true));
		}

		return new ThreadsDTO(res, Integer.MAX_VALUE);
	}

	private int countThreads(String forum) {
		String keyValue;
		if (forum == null) {
			keyValue = "threads.total";
		} else {
			keyValue = "threads.forum." + forum;
		}

		String res = jooq.select(SYSINFO.VALUE)
				.from(SYSINFO)
				.where(SYSINFO.KEY.equal(keyValue))
				.fetchOne(SYSINFO.VALUE);

		return Integer.parseInt(res);
	}

	private int countMessages(String forum) {

		String keyValue;
		if (forum == null) {
			keyValue = "messages.total";
		} else {
			keyValue = "messages.forum." + forum;
		}

		String res = jooq.select(SYSINFO.VALUE)
				.from(SYSINFO)
				.where(SYSINFO.KEY.equal(keyValue))
				.fetchOne(SYSINFO.VALUE);

		return Integer.parseInt(res);
	}

	private ThreadDTO recordToDTO(Record record, boolean fetchLastId) {
		ThreadDTO thread = new ThreadDTO();
		if (fetchLastId) {
			thread.setLastId(record.getValue(THREADS.LASTID).longValue());
		}
		thread.setAuthor(getAuthor(record.getValue(MESSAGES.AUTHOR)));
		thread.setDate(record.getValue(MESSAGES.DATE));
		thread.setForum(record.getValue(MESSAGES.FORUM));
		thread.setNumberOfMessages(getNumberOfMessages(record.getValue(MESSAGES.THREADID).longValue()));
		thread.setId(record.getValue(THREADS.THREADID).longValue());
		thread.setIsVisible(record.getValue(MESSAGES.VISIBLE).intValue());
		thread.setRank(record.getValue(MESSAGES.RANK));
		thread.setSubject(record.getValue(MESSAGES.SUBJECT));

		return thread;
	}

	private int getNumberOfMessages(long threadId) {
		return jooq.fetchCount(
				jooq.selectFrom(MESSAGES)
						.where(MESSAGES.THREADID.eq((int) threadId))
		);
	}

}