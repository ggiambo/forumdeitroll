package com.forumdeitroll.persistence.dao;

import com.forumdeitroll.persistence.ThreadDTO;
import com.forumdeitroll.persistence.ThreadsDTO;
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
				where =	where.and(MESSAGES.FORUM.isNull().or(MESSAGES.FORUM.notIn(hiddenForums)));
			}

		} else {
			where = where.and(MESSAGES.FORUM.equal(forum));
		}

		Result<Record> result = where.orderBy(THREADS.LASTID.desc())
				.limit(limit).offset(limit * page)
				.fetch();

		if (result.isEmpty()) {
			return new ThreadsDTO();
		}


		int threadsCount = countThreads(forum);
		if (hiddenForums != null && !hiddenForums.isEmpty() && forum == null) {
			for (String hiddenForum : hiddenForums) {
				threadsCount -= countMessages(hiddenForum);
			}
		}
		return new ThreadsDTO(getThreads(result, true), threadsCount);

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

	private List<ThreadDTO> getThreads(Result<Record> result, boolean fetchLastId) {
		List<ThreadDTO> messages = new ArrayList<ThreadDTO>();

		for (Record record : result) {
			ThreadDTO message = new ThreadDTO();
			if (fetchLastId) {
				message.setLastId(record.getValue(THREADS.LASTID));
			}
			message.setAuthor(getAuthor(record.getValue(MESSAGES.AUTHOR)));
			message.setDate(record.getValue(MESSAGES.DATE));
			message.setForum(record.getValue(MESSAGES.FORUM));
			message.setNumberOfMessages(getNumberOfMessages(record.getValue(MESSAGES.THREADID)));
			message.setId(record.getValue(THREADS.THREADID));
			message.setIsVisible(record.getValue(MESSAGES.VISIBLE));
			message.setRank(record.getValue(MESSAGES.RANK));
			message.setSubject(record.getValue(MESSAGES.SUBJECT));
			messages.add(message);
		}
		return messages;
	}

	private int getNumberOfMessages(long threadId) {
		return jooq.selectFrom(MESSAGES)
				.where(MESSAGES.THREADID.eq((int)threadId))
				.fetchCount();

	}

}