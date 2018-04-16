package com.forumdeitroll.persistence.dao;

import com.forumdeitroll.persistence.NotificationDTO;
import com.forumdeitroll.persistence.jooq.tables.records.NotificationRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;

import static com.forumdeitroll.persistence.jooq.Tables.*;

public class MiscDAO extends BaseDAO {

	public MiscDAO(DSLContext jooq) {
		super(jooq);
	}

	public List<String> getForums() {

		Result<Record1<String>> records = jooq.select(MESSAGES.FORUM)
			.from(MESSAGES)
			.where(MESSAGES.FORUM.isNotNull())
			.groupBy(MESSAGES.FORUM)
			.orderBy(DSL.count(MESSAGES.ID).desc(), MESSAGES.FORUM.asc())
			.fetch();

		List<String> res = new ArrayList<String>(records.size());
		for (Record1<String> record : records) {
			res.add(record.getValue(MESSAGES.FORUM));
		}

		return res;
	}

	public List<NotificationDTO> getNotifications(String fromNick, String toNick) {

		SelectConditionStep<NotificationRecord> where = jooq.selectFrom(NOTIFICATION).where(DSL.trueCondition());
		if (StringUtils.isNotEmpty(fromNick)) {
			where = where.and(NOTIFICATION.FROMNICK.eq(fromNick));
		}
		if (StringUtils.isNotEmpty(toNick)) {
			where = where.and(NOTIFICATION.TONICK.eq(toNick));
		}

		Result<NotificationRecord> records = where.orderBy(NOTIFICATION.ID.asc())
				.fetch();

		List<NotificationDTO> res = new ArrayList<NotificationDTO>(records.size());
		for (NotificationRecord record : records) {
			res.add(recordToDTO(record));
		}

		return res;
	}

	public void createNotification(String fromNick, String toNick, long id) {
		jooq.insertInto(NOTIFICATION)
			.set(NOTIFICATION.FROMNICK, fromNick)
			.set(NOTIFICATION.TONICK, toNick)
			.set(NOTIFICATION.MSGID, (int)id)
			.execute();
	}

	public void removeNotification(String fromNick, String toNick, long id) {

		DeleteConditionStep<NotificationRecord> where = jooq.delete(NOTIFICATION)
			.where(NOTIFICATION.FROMNICK.eq(fromNick));

			if (StringUtils.isNotEmpty(toNick)) {
				where = where.and(NOTIFICATION.TONICK.eq(toNick));
			}

			where.and(NOTIFICATION.ID.eq((int) id))
				.execute();
	}

	public int like(long msgId, String nick, boolean upvote) {
		Byte oldVoteValue = jooq.select(LIKES.VOTE)
				.from(LIKES)
				.where(LIKES.NICK.eq(nick))
				.and(LIKES.MSGID.eq((int) msgId))
				.fetchOne(LIKES.VOTE);

		boolean hasVoted = false;
		boolean oldVote = false;
		if (oldVoteValue != null) {
			hasVoted = true;
			oldVote = oldVoteValue == 1 ? true : false;
		}

		if (hasVoted && oldVote == upvote) {
			// doppio voto, stessa direzione
			return 0;
		}


		int rankIncrement;
		if (hasVoted) {
			rankIncrement = 2;
		} else {
			rankIncrement = 1;
		}
		if (!upvote) {
			rankIncrement = -1*rankIncrement;
		}

		Integer rank = jooq.select(MESSAGES.RANK)
				.from(MESSAGES)
				.where(MESSAGES.ID.eq((int) msgId))
				.fetchOne(MESSAGES.RANK);

		jooq.update(MESSAGES)
			.set(MESSAGES.RANK, rank + rankIncrement)
			.where(MESSAGES.ID.eq((int) msgId))
			.execute();

		Byte vote = (byte)(upvote ? 1 : 0);
		if (!hasVoted) {
			jooq.insertInto(LIKES)
				.set(LIKES.NICK, nick)
				.set(LIKES.MSGID, (int)msgId)
				.set(LIKES.VOTE, vote)
					.execute();
		} else {
			jooq.update(LIKES)
				.set(LIKES.VOTE, vote)
				.where(LIKES.NICK.eq(nick))
				.and(LIKES.MSGID.eq((int)msgId))
				.execute();
		}

		return rankIncrement;
	}

	public long getLastId() {
		Record1<Integer> maxId = jooq.select(DSL.max(MESSAGES.ID))
			.from(MESSAGES)
			.fetchOne();

		return maxId.value1();
	}

	private NotificationDTO recordToDTO(NotificationRecord record) {
		NotificationDTO notification = new NotificationDTO();
		notification = new NotificationDTO();
		notification.setId(record.getId());
		notification.setFromNick(record.getFromnick());
		notification.setToNick(record.getTonick());
		notification.setMsgId(record.getMsgid());
		return notification;
	}

}
