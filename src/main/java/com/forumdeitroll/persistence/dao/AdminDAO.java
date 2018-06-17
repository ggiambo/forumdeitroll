package com.forumdeitroll.persistence.dao;

import com.forumdeitroll.persistence.DAOFactory;
import com.forumdeitroll.persistence.MessageDTO;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import static com.forumdeitroll.persistence.jooq.Tables.*;

public class AdminDAO extends BaseDAO {

	public AdminDAO(DSLContext jooq) {
		super(jooq);
	}

	public void moveThreadTree(MessageDTO msg, String destForum) {

		int rootMessageId = (int)msg.getId();

		// tutti i children di questo messaggio, lui compreso
		Stack<Integer> parents = new Stack<>();
		ArrayList<Integer> messages = new ArrayList<>();
		parents.push(rootMessageId);
		int currentId;
		while (!parents.isEmpty()) {
			currentId = parents.pop();
			messages.add(currentId);

			Result<Record1<Integer>> records = jooq.select(MESSAGES.ID)
					.from(MESSAGES)
					.where(MESSAGES.PARENTID.eq(currentId))
					.fetch();

			for (Record1<Integer> record : records) {
				Integer id = record.getValue(MESSAGES.ID);
				if (id != currentId) {
					parents.push(id);
				}
			}
		}

		// setta a tutti i messaggi il threadId = rootMessageId e il nuovo forum
		int res = jooq.update(MESSAGES)
				.set(MESSAGES.THREADID, rootMessageId)
				.set(MESSAGES.FORUM, destForum)
				.where(MESSAGES.ID.in(messages))
				.execute();

		// nuovo parent per il root del nuovo thread
		jooq.update(MESSAGES)
			.set(MESSAGES.PARENTID, rootMessageId)
			.where(MESSAGES.ID.eq(rootMessageId))
			.execute();


		String srcForum = msg.getForum();
		srcForum = srcForum == null ? "" : srcForum;
		// update numero di messaggi
		increaseNumberOfMessagesFor(srcForum, -1 * res);
		increaseNumberOfMessagesFor(destForum, res);

		// update numero di threads
		increaseNumberOfThreadsFor(srcForum, -1);
		increaseNumberOfThreadsFor(destForum, 1);

		// update tabella threads
		DAOFactory.getMessagesDAO().updateLastIdInThread(rootMessageId);
		DAOFactory.getMessagesDAO().updateLastIdInThread(msg.getThreadId());
	}

	public void restoreOrHideMessage(long msgId, int visible) {
		jooq.update(MESSAGES)
			.set(MESSAGES.VISIBLE, (byte)visible)
			.where(MESSAGES.ID.eq((int) msgId))
			.execute();
	}

	public List<String> getTitles() {
		Result<Record1<String>> records = jooq.select(SYSINFO.VALUE)
			.from(SYSINFO)
			.where(SYSINFO.KEY.like("title.%"))
			.orderBy(SYSINFO.KEY.asc())
			.fetch();

		List<String> ret = new ArrayList<>(records.size());
		for (Record record : records) {
			ret.add(record.getValue(SYSINFO.VALUE));
		}

		return ret;
	}

	public void setTitles(List<String> titles) {

		jooq.delete(SYSINFO)
			.where(SYSINFO.KEY.like("title.%"))
			.execute();

		int index = 0;
		for (String title : titles) {
			jooq.insertInto(SYSINFO)
				.set(SYSINFO.KEY, "title." + index++)
				.set(SYSINFO.VALUE, title)
				.execute();
		}
	}

	public Collection<String> getAdmins() {
		return jooq.select(PREFERENCES.NICK)
				.from(PREFERENCES)
				.where(PREFERENCES.KEY.like("super"))
				.fetch(PREFERENCES.NICK);
	}

}
