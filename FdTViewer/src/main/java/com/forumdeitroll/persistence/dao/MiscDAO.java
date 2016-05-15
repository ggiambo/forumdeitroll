package com.forumdeitroll.persistence.dao;

import static com.forumdeitroll.persistence.jooq.Tables.LIKES;
import static com.forumdeitroll.persistence.jooq.Tables.MESSAGES;
import static com.forumdeitroll.persistence.jooq.Tables.NOTIFICATION;
import static com.forumdeitroll.persistence.jooq.Tables.TAGNAMES;
import static com.forumdeitroll.persistence.jooq.Tables.TAGS_BIND;
import static com.forumdeitroll.persistence.sql.mysql.Utf8Mb4Conv.mb4safe;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.MessagesDTO;
import com.forumdeitroll.persistence.NotificationDTO;
import com.forumdeitroll.persistence.TagDTO;
import com.forumdeitroll.persistence.jooq.tables.records.NotificationRecord;
import com.forumdeitroll.persistence.jooq.tables.records.TagsBindRecord;

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

	public void getTags(MessagesDTO messages) {

		if (messages.getMessages().size() == 0) return;

		List<Integer> messageIds = new ArrayList<Integer>(messages.getMessages().size());
		for (MessageDTO msg : messages.getMessages()) {
			messageIds.add((int) msg.getId());
		}

		Result<Record4<Integer, Integer, String, String>> records = jooq.select(TAGNAMES.T_ID, TAGS_BIND.M_ID, TAGS_BIND.AUTHOR, TAGNAMES.VALUE)
				.from(TAGNAMES)
				.join(TAGS_BIND).on(TAGNAMES.T_ID.eq(TAGS_BIND.T_ID))
				.where(TAGS_BIND.M_ID.in(messageIds))
				.orderBy(TAGS_BIND.M_ID)
				.fetch();

		long currentMid = -1;
		MessageDTO currentMessage = null;
		for (Record4<Integer, Integer, String, String> record : records) {
			TagDTO tag = new TagDTO();
			tag.setT_id(record.getValue(TAGNAMES.T_ID).longValue());
			tag.setM_id(record.getValue(TAGS_BIND.M_ID).longValue());
			tag.setAuthor(record.getValue(TAGS_BIND.AUTHOR));
			tag.setValue(record.getValue(TAGNAMES.VALUE));
			if (tag.getM_id() != currentMid) {
				for (MessageDTO message : messages.getMessages()) {
					if (message.getId() == tag.getM_id()) {
						currentMessage = message;
						currentMid = tag.getM_id();
						break;
					}
				}
				currentMessage.setTags(new ArrayList<TagDTO>());
			}
			currentMessage.getTags().add(tag);
		}

	}

	public TagDTO addTag(TagDTO tag) {

		Integer t_id = jooq.select(TAGNAMES.T_ID)
				.from(TAGNAMES)
				.where(TAGNAMES.VALUE.eq(tag.getValue()))
				.fetchOne(TAGNAMES.T_ID);
		if (t_id == null) {
			t_id = jooq.insertInto(TAGNAMES)
					.set(TAGNAMES.VALUE, mb4safe(tag.getValue()))
					.returning(TAGNAMES.T_ID)
					.fetchOne()
					.getTId();
		}

		TagsBindRecord tagsBindRecord = jooq.selectFrom(TAGS_BIND)
				.where(TAGS_BIND.T_ID.eq(t_id))
				.and(TAGS_BIND.M_ID.eq((int) tag.getM_id()))
				.fetchOne();
		if (tagsBindRecord != null) {
			tag.setT_id(t_id);
			tag.setAuthor(tagsBindRecord.getAuthor());
			return tag;
		}

		jooq.insertInto(TAGS_BIND)
			.set(TAGS_BIND.T_ID, t_id)
			.set(TAGS_BIND.M_ID, (int)tag.getM_id())
			.set(TAGS_BIND.AUTHOR, tag.getAuthor())
			.execute();
		tag.setT_id(t_id);
		return tag;
	}

	public void deleTag(TagDTO tag, boolean isAdmin) {
		if (isAdmin) {
			int res = jooq.delete(TAGS_BIND)
					.where(TAGS_BIND.T_ID.eq((int) tag.getT_id()))
					.and(TAGS_BIND.M_ID.eq((int) tag.getM_id()))
					.execute();
			if (res != 1) {
				// e' difficile che un admin si metta a furmigare
				// i tag non vengono mai eliminati da tagnames
				throw new RuntimeException("Hai eliminato "+res+" recordz da tags_bind!");
			}
		} else {
			int res = jooq.delete(TAGS_BIND)
					.where(TAGS_BIND.T_ID.eq((int) tag.getT_id()))
					.and(TAGS_BIND.M_ID.eq((int) tag.getM_id()))
					.and(TAGS_BIND.AUTHOR.eq(tag.getAuthor()))
					.execute();
			if (res != 1) {
				// se non sei l'owner del tag oppure stai furmigando
				// i tag non vengono mai eliminati da tagnames
				throw new RuntimeException("Hai eliminato "+res+" recordz da tags_bind!");
			}
		}
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
