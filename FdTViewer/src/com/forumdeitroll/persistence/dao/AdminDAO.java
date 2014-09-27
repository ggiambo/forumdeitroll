package com.forumdeitroll.persistence.dao;

import static com.forumdeitroll.persistence.jooq.Tables.ADS;
import static com.forumdeitroll.persistence.jooq.Tables.MESSAGES;
import static com.forumdeitroll.persistence.jooq.Tables.SYSINFO;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;

import com.forumdeitroll.persistence.AdDTO;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.jooq.tables.records.AdsRecord;

public class AdminDAO extends BaseDAO {

	public AdminDAO(DSLContext jooq) {
		super(jooq);
	}

	public void moveThreadTree(MessageDTO msg, String destForum) {

		int rootMessageId = (int)msg.getId();

		// tutti i children di questo messaggio, lui compreso
		Stack<Integer> parents = new Stack<Integer>();
		ArrayList<Integer> messages = new ArrayList<Integer>();
		parents.push(rootMessageId);
		int currentId = rootMessageId;
		while (!parents.isEmpty()) {
			currentId = parents.pop();
			messages.add(currentId);

			Result<Record1<Integer>> records = jooq.select(MESSAGES.ID)
					.from(MESSAGES)
					.where(MESSAGES.PARENTID.eq((int)currentId))
					.fetch();

			for (Record1<Integer> record : records) {
				Integer id = record.getValue(MESSAGES.ID);
				if (id.intValue() != currentId) {
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

		List<String> ret = new ArrayList<String>(records.size());
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

	public List<AdDTO> getAllAds() {
		Result<AdsRecord> records = jooq.selectFrom(ADS)
			.orderBy(ADS.ID)
			.fetch();

		final List<AdDTO> res = new ArrayList<AdDTO>(records.size());
		for (AdsRecord record : records) {
			res.add(recordToDTO(record));
		}
		return res;
	}

	private AdDTO recordToDTO(AdsRecord record) {
		final AdDTO dto = new AdDTO();
		dto.setId(record.getId());
		dto.setTitle(record.getTitle());
		dto.setVisurl(record.getVisurl());
		dto.setContent(record.getContent());
		return dto;
	}

}
