package com.forumdeitroll.persistence.dao;

import static com.forumdeitroll.persistence.jooq.tables.Digest.DIGEST;
import static com.forumdeitroll.persistence.jooq.tables.DigestParticipant.DIGEST_PARTICIPANT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.jooq.types.DayToSecond;

import com.forumdeitroll.persistence.DigestArticleDTO;

public class DigestDAO extends BaseDAO {
	public DigestDAO(DSLContext jooq) {
		super(jooq);
	}
	public List<DigestArticleDTO> getReadersDigest() {

		Field<DayToSecond> rank = DSL.timestampDiff(DIGEST.STARTDATE, DIGEST.LASTDATE).multiply(DIGEST.NROFMESSAGES).as("rank");

		List<Field<?>> fields = new ArrayList<Field<?>>();
		fields.addAll(Arrays.asList(DIGEST.fields()));
		fields.addAll(Arrays.asList(DIGEST_PARTICIPANT.fields()));
		fields.add(rank);

		Result<Record> records = jooq.select(fields)
			.from(DIGEST, DIGEST_PARTICIPANT)
			.where(DIGEST.THREADID.eq(DIGEST_PARTICIPANT.THREADID))
			.orderBy(rank)
			.fetch();
		List<DigestArticleDTO> results = new ArrayList<DigestArticleDTO>();
		DigestArticleDTO current = null;
		for (Record record : records) {
			if (current == null || current.getThreadId() != record.getValue(DIGEST.THREADID)) {
				current = new DigestArticleDTO();
				current.setThreadId(record.getValue(DIGEST.THREADID).longValue());
				current.setAuthor(record.getValue(DIGEST.AUTHOR));
				current.setSubject(record.getValue(DIGEST.SUBJECT));
				current.setOpenerText(record.getValue(DIGEST.OPENER_TEXT));
				current.setExcerpt(record.getValue(DIGEST.EXCERPT));
				current.setStartDate(record.getValue(DIGEST.STARTDATE));
				current.setLastDate(record.getValue(DIGEST.LASTDATE));
				current.setNrOfMessages(record.getValue(DIGEST.NROFMESSAGES));
				if (record.getValue(DIGEST_PARTICIPANT.AUTHOR) != null && !current.getParticipants().contains(record.getValue(DIGEST_PARTICIPANT.AUTHOR))) {
					current.getParticipants().add(record.getValue(DIGEST_PARTICIPANT.AUTHOR));
				}
				results.add(current);
			} else {
				if (record.getValue(DIGEST_PARTICIPANT.AUTHOR) != null && !current.getParticipants().contains(record.getValue(DIGEST_PARTICIPANT.AUTHOR))) {
					current.getParticipants().add(record.getValue(DIGEST_PARTICIPANT.AUTHOR));
				}
			}
		}
		return results;
	}

/**

	Da /opt/fdt/digest (riportata per versionamento):

	DROP TABLE digest;

	CREATE TABLE digest
	(
	    `threadId` INT(11),
	    `author` TINYTEXT,
	    `subject` TINYTEXT,
	    `opener_text` LONGTEXT,
	    `excerpt` LONGTEXT,
	    `nrOfMessages` INT(6),
	    `startDate` datetime,
	    `lastDate` datetime
	) ENGINE=MyISAM DEFAULT CHARSET=utf8;

	INSERT INTO digest
	SELECT id, author, subject, text, NULL, nrOfMessages, startdate, lastdate
	FROM messages, (
	    SELECT threadId, COUNT(threadId) as nrOfMessages, MIN(date) as startdate, MAX(date) as lastdate
	    FROM messages
	    WHERE `date` >= DATE_SUB(SYSDATE(), INTERVAL 1 MONTH)
	    GROUP BY threadId
	    ORDER BY COUNT(threadId) DESC
	    LIMIT 0,50
	) AS a
	WHERE id = a.threadId;

	UPDATE digest
	SET excerpt = (
	    SELECT `text`
	    FROM messages
	    WHERE messages.threadId = digest.threadId
	    GROUP BY parentId
	    ORDER BY parentId DESC
	    LIMIT 0, 1
	);

	DROP TABLE digest_participant;

	CREATE TABLE digest_participant
	(
	    `threadId` INT(11),
	    `author` TINYTEXT
	) ENGINE=MyISAM DEFAULT CHARSET=utf8;

	INSERT INTO digest_participant
	SELECT DISTINCT messages.`threadId`, messages.`author`
	FROM messages, digest
	WHERE messages.threadId = digest.threadId;


*/
}
