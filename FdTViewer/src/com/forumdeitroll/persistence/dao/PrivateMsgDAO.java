package com.forumdeitroll.persistence.dao;

import static com.forumdeitroll.persistence.jooq.Tables.AUTHORS;
import static com.forumdeitroll.persistence.jooq.Tables.PVT_CONTENT;
import static com.forumdeitroll.persistence.jooq.Tables.PVT_RECIPIENT;
import static com.forumdeitroll.persistence.sql.mysql.Utf8Mb4Conv.mb4safe;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.forumdeitroll.FdTException;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.PrivateMsgDTO;
import com.forumdeitroll.persistence.PrivateMsgDTO.ToNickDetailsDTO;
import com.forumdeitroll.taglibs.PagerTag;

public class PrivateMsgDAO extends BaseDAO {

	public PrivateMsgDAO(DSLContext jooq) {
		super(jooq);
	}

	public List<PrivateMsgDTO> getSentPvts(AuthorDTO user, int limit, int pageNr) {

		Result<Record3<Integer, String, Timestamp>> records = jooq.select(PVT_CONTENT.ID, PVT_CONTENT.SUBJECT, PVT_CONTENT.SENDDATE)
			.from(PVT_CONTENT)
			.where(PVT_CONTENT.SENDER.eq(user.getNick()))
			.and(PVT_CONTENT.DELETED.eq(0))
			.orderBy(PVT_CONTENT.SENDDATE.desc())
			.limit(limit)
			.offset(limit*pageNr)
			.fetch();

		List<PrivateMsgDTO> result = new LinkedList<PrivateMsgDTO>();

		for (Record3<Integer, String, Timestamp> record : records) {
				PrivateMsgDTO msg = new PrivateMsgDTO();
				msg.setRead(true); // se l'ho mandato io...
				msg.setId(record.getValue(PVT_CONTENT.ID));
				msg.setSubject(record.getValue(PVT_CONTENT.SUBJECT));
				msg.setDate(record.getValue(PVT_CONTENT.SENDDATE));
				msg.setToNick(getRecipients((int)msg.getId()));

				result.add(msg);
			}

		return result;
	}

	public List<PrivateMsgDTO> getInbox(AuthorDTO user, int limit, int pageNr) {
		List<PrivateMsgDTO> result = new LinkedList<PrivateMsgDTO>();

		Result<Record3<Integer, Integer, Timestamp>> records = jooq.select(PVT_RECIPIENT.PVT_ID, PVT_RECIPIENT.READ, PVT_CONTENT.SENDDATE)
			.from(PVT_RECIPIENT)
			.join(PVT_CONTENT)
			.on(PVT_RECIPIENT.PVT_ID.eq(PVT_CONTENT.ID))
			.where(PVT_RECIPIENT.RECIPIENT.eq(user.getNick()))
			.and(PVT_RECIPIENT.DELETED.eq(0))
			.orderBy(PVT_CONTENT.SENDDATE.desc())
			.limit(limit)
			.offset(limit*pageNr)
			.fetch();

		for (Record3<Integer, Integer, Timestamp> record : records) {
			int id = record.getValue(PVT_RECIPIENT.PVT_ID);
				PrivateMsgDTO msg = new PrivateMsgDTO();
				msg.setId(id);
				msg.setRead(record.getValue(PVT_RECIPIENT.READ) != 0);

				Record3<String, String, Timestamp> record2 = jooq.select(PVT_CONTENT.SENDER, PVT_CONTENT.SUBJECT, PVT_CONTENT.SENDDATE)
					.from(PVT_CONTENT)
					.where(PVT_CONTENT.ID.eq(id))
					.fetchOne();
				msg.setFromNick(record2.getValue(PVT_CONTENT.SENDER));
				msg.setSubject(record2.getValue(PVT_CONTENT.SUBJECT));
				msg.setDate(record2.getValue(PVT_CONTENT.SENDDATE));
				msg.setToNick(getRecipients((int)msg.getId()));

				result.add(msg);
			}

		return result;
	}

	public int getInboxPages(AuthorDTO author) {

		Object count = jooq.selectCount()
				.from(PVT_RECIPIENT)
				.where(PVT_RECIPIENT.RECIPIENT.eq(author.getNick()))
				.and(PVT_RECIPIENT.DELETED.eq(0))
				.fetchOne()
				.getValue(0);
		Integer nElem = (Integer) count;

		return PagerTag.pagify(nElem, 10);
	}

	public int getOutboxPages(AuthorDTO author) {

		Object count = jooq.selectCount()
				.from(PVT_CONTENT)
				.where(PVT_CONTENT.SENDER.eq(author.getNick()))
				.and(PVT_CONTENT.DELETED.eq(0))
				.fetchOne()
				.getValue(0);
		Integer nElem = (Integer) count;

		return PagerTag.pagify(nElem, 10);
	}

	public boolean sendAPvtForGreatGoods(AuthorDTO author, PrivateMsgDTO privateMsg, String[] recipients) {

		//verifica esistenza dei destinatari
		for (String recipient: recipients) {
			if (recipient.equals("")) continue;
			if (!existsRecipient(recipient)) {
				throw new FdTException("Il destinatario "+StringEscapeUtils.escapeHtml4(recipient)+" non esiste.");
			}
		}

		int pvt_id = jooq.insertInto(PVT_CONTENT)
				.set(PVT_CONTENT.SENDER, author.getNick())
				.set(PVT_CONTENT.CONTENT, mb4safe(privateMsg.getText()))
				.set(PVT_CONTENT.SUBJECT, mb4safe(privateMsg.getSubject()))
				.set(PVT_CONTENT.SENDDATE, new Timestamp(System.currentTimeMillis()))
				.set(PVT_CONTENT.REPLYTO, (int)privateMsg.getReplyTo())
				.returning(PVT_CONTENT.ID)
				.fetchOne()
				.getValue(PVT_CONTENT.ID);

		for (String recipient: recipients) {
			if (recipient.equals("")) continue;
			jooq.insertInto(PVT_RECIPIENT)
				.set(PVT_RECIPIENT.PVT_ID, pvt_id)
				.set(PVT_RECIPIENT.RECIPIENT, recipient)
				.execute();
		}

		return true;
	}

	public PrivateMsgDTO getPvtDetails(long pvt_id, AuthorDTO user) {

		Field<?>[] f = new Field<?>[] {
				PVT_CONTENT.CONTENT, PVT_CONTENT.REPLYTO, PVT_CONTENT.SUBJECT, PVT_CONTENT.SENDDATE, PVT_CONTENT.SENDER,
				PVT_RECIPIENT.RECIPIENT, PVT_RECIPIENT.READ
		};

		Result<Record> records = jooq.select(f)
				.from(PVT_CONTENT)
				.join(PVT_RECIPIENT)
				.on(PVT_CONTENT.ID.eq(PVT_RECIPIENT.PVT_ID))
				.where(PVT_CONTENT.ID.eq((int)pvt_id))
				.and(
						PVT_CONTENT.SENDER.eq(user.getNick())
						.or(
							DSL.exists(
								jooq.select(PVT_RECIPIENT.RECIPIENT)
								.from(PVT_RECIPIENT)
								.where(PVT_RECIPIENT.PVT_ID.eq((int)pvt_id))
								.and(PVT_RECIPIENT.RECIPIENT.eq(user.getNick()))
							)
						)
				)
				.fetch();

		//one row per recipient
		PrivateMsgDTO msg = null;
		for (Record record : records) {
			if (msg == null) {
				msg = new PrivateMsgDTO();
				msg.setId(pvt_id);
				//msg.setFromNick(user.getNick());
				msg.setText(record.getValue(PVT_CONTENT.CONTENT));
				msg.setReplyTo(record.getValue(PVT_CONTENT.REPLYTO));
				msg.setSubject(record.getValue(PVT_CONTENT.SUBJECT));
				msg.setDate(record.getValue(PVT_CONTENT.SENDDATE));
				msg.setFromNick(record.getValue(PVT_CONTENT.SENDER));
			}
			ToNickDetailsDTO toNick = new ToNickDetailsDTO();
			toNick.setNick(record.getValue(PVT_RECIPIENT.RECIPIENT));
			toNick.setRead(record.getValue(PVT_RECIPIENT.READ) != 0);
			msg.getToNick().add(toNick);
		}
		return msg;
	}

	public void notifyRead(AuthorDTO recipient, PrivateMsgDTO privateMsg) {
		notifyPvt(recipient, privateMsg, true);
	}

	public void notifyUnread(AuthorDTO recipient, PrivateMsgDTO privateMsg) {
		notifyPvt(recipient, privateMsg, false);
	}

	public boolean checkForNewPvts(AuthorDTO recipient) {
		Object count = jooq.selectCount()
			.from(PVT_RECIPIENT)
			.where(PVT_RECIPIENT.RECIPIENT.eq(recipient.getNick()))
			.and(PVT_RECIPIENT.READ.eq(0))
			.and(PVT_RECIPIENT.DELETED.eq(0))
			.fetchOne()
			.getValue(0);

		return ((Integer)count) > 0;
	}

	public void deletePvt(long pvt_id, AuthorDTO user) {

		jooq.update(PVT_RECIPIENT)
			.set(PVT_RECIPIENT.DELETED, 1)
			.where(PVT_RECIPIENT.PVT_ID.eq((int)pvt_id))
			.and(PVT_RECIPIENT.RECIPIENT.eq(user.getNick()))
			.execute();

		jooq.update(PVT_CONTENT)
			.set(PVT_CONTENT.DELETED, 1)
			.where(PVT_CONTENT.ID.eq((int)pvt_id))
			.and(PVT_CONTENT.SENDER.eq(user.getNick()))
			.execute();

		//cleanup - eventualmente opzionale oppure lanciata da timertask
		List<Integer> deletedPvtContent = jooq.select(PVT_CONTENT.ID)
			.from(PVT_CONTENT)
			.where(PVT_CONTENT.DELETED.eq(1))
			.fetch(PVT_CONTENT.ID);
		jooq.delete(PVT_RECIPIENT)
			.where(PVT_RECIPIENT.PVT_ID.notIn(deletedPvtContent))
			.and(PVT_RECIPIENT.DELETED.eq(1))
			.execute();

		List<Integer> notDeletedPvtRecipient = jooq.select(PVT_RECIPIENT.PVT_ID)
				.from(PVT_RECIPIENT)
				.where(PVT_RECIPIENT.DELETED.eq(0))
				.fetch(PVT_RECIPIENT.PVT_ID);
		jooq.delete(PVT_RECIPIENT)
			.where(PVT_RECIPIENT.PVT_ID.notIn(notDeletedPvtRecipient))
			.and(PVT_RECIPIENT.DELETED.eq(1))
			.execute();

	}

	private void notifyPvt(AuthorDTO recipient, PrivateMsgDTO privateMsg, boolean read) {
		int result = jooq.update(PVT_RECIPIENT)
				.set(PVT_RECIPIENT.READ, read ? 1 : 0)
				.where(PVT_RECIPIENT.RECIPIENT.eq(recipient.getNick()))
				.and(PVT_RECIPIENT.PVT_ID.eq((int) privateMsg.getId()))
				.execute();
		if (result > 1) { // 0 == messaggio gia' letto
			//throw new Exception("Le scimmie presto! "+recipient.getNick()+"ha aggiornato "+result+" records!");
		}
	}

	private boolean existsRecipient(String recipient)  {

		Object count = jooq.selectCount()
			.from(AUTHORS)
			.where(AUTHORS.NICK.eq(recipient))
			.and(AUTHORS.HASH.isNotNull())
			.fetchOne()
			.getValue(0);

		return ((Integer)count) != 0;
	}

	private List<ToNickDetailsDTO> getRecipients(int msgId) {
		Result<Record2<String, Integer>> records = jooq.select(PVT_RECIPIENT.RECIPIENT, PVT_RECIPIENT.READ)
				.from(PVT_RECIPIENT)
				.where(PVT_RECIPIENT.PVT_ID.eq(msgId))
				.fetch();

		List<ToNickDetailsDTO> res = new ArrayList<ToNickDetailsDTO>(records.size());
		for (Record2<String, Integer> record2 : records) {
			ToNickDetailsDTO toNick = new ToNickDetailsDTO();
			toNick.setNick(record2.getValue(PVT_RECIPIENT.RECIPIENT));
			toNick.setRead(record2.getValue(PVT_RECIPIENT.READ) != 0);
			res.add(toNick);
		}

		return res;
	}

}
