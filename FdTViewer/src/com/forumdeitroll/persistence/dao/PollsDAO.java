package com.forumdeitroll.persistence.dao;

import static com.forumdeitroll.persistence.jooq.Tables.POLL;
import static com.forumdeitroll.persistence.jooq.Tables.POLL_QUESTION;
import static com.forumdeitroll.persistence.jooq.Tables.POLL_USER;
import static com.forumdeitroll.persistence.sql.mysql.Utf8Mb4Conv.mb4safe;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.TableField;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.PollDTO;
import com.forumdeitroll.persistence.PollQuestion;
import com.forumdeitroll.persistence.PollsDTO;
import com.forumdeitroll.persistence.jooq.tables.records.PollQuestionRecord;
import com.forumdeitroll.persistence.jooq.tables.records.PollRecord;

public class PollsDAO extends BaseDAO {

	public PollsDAO(DSLContext jooq) {
		super(jooq);
	}

	public long createPoll(PollDTO pollDTO) {

		Timestamp now = new Timestamp(System.currentTimeMillis());

		PollRecord record = jooq.insertInto(POLL)
				.set(POLL.TITLE, mb4safe(pollDTO.getTitle()))
				.set(POLL.AUTHOR, pollDTO.getAuthor())
				.set(POLL.TEXT, pollDTO.getText())
				.set(POLL.CREATIONDATE, now)
				.set(POLL.UPDATEDATE, now)
				.returning(POLL.ID)
				.fetchOne();

		int id = record.getId();

		// insert poll questions
		for (PollQuestion question : pollDTO.getPollQuestions()) {
			jooq.insertInto(POLL_QUESTION)
					.set(POLL_QUESTION.POLLID, id)
					.set(POLL_QUESTION.SEQUENCE, question.getSequence())
					.set(POLL_QUESTION.TEXT, mb4safe(question.getText()))
					.set(POLL_QUESTION.VOTES, 0)
					.execute();
		}
		return id;
	}

	public boolean updatePollQuestion(PollQuestion pollQuestion, AuthorDTO user) {

		Object count = jooq.selectCount()
				.from(POLL_USER)
				.where(POLL_USER.POLLID.eq((int) pollQuestion.getPollId()))
				.and(POLL_USER.NICK.eq(user.getNick()))
				.fetchOne()
				.getValue(0);

		Integer nrOfMessages = (Integer) count;
		if (nrOfMessages != 0) {
			return false;
		}

		// update
		int votes = jooq.select(POLL_QUESTION.VOTES).
				from(POLL_QUESTION)
				.where(POLL_QUESTION.POLLID.eq((int) pollQuestion.getPollId()))
				.and(POLL_QUESTION.SEQUENCE.eq(pollQuestion.getSequence()))
				.fetchOne()
				.getValue(POLL_QUESTION.VOTES);

		votes++;

		jooq.update(POLL_QUESTION)
				.set(POLL_QUESTION.VOTES, votes)
				.where(POLL_QUESTION.POLLID.eq((int) pollQuestion.getPollId()))
				.and(POLL_QUESTION.SEQUENCE.eq(pollQuestion.getSequence()))
				.execute();

		// update update date :)
		jooq.update(POLL)
				.set(POLL.UPDATEDATE, new Timestamp(System.currentTimeMillis()))
				.where(POLL.ID.eq((int) pollQuestion.getPollId()))
				.execute();

		// 1 troll, 1 vote
		jooq.insertInto(POLL_USER)
				.set(POLL_USER.NICK, user.getNick())
				.set(POLL_USER.POLLID, (int) pollQuestion.getPollId())
				.execute();

		return true;
	}

	public PollsDTO getPollsByDate(int limit, int page) {
		return getPollsBy(POLL.CREATIONDATE, limit, page);
	}

	public PollsDTO getPollsByLastVote(int limit, int page) {
		return getPollsBy(POLL.UPDATEDATE, limit, page);
	}

	private PollsDTO getPollsBy(TableField<PollRecord, ?> by, int limit, int page) {
		Result<PollRecord> records = jooq.selectFrom(POLL)
				.orderBy(by.desc())
				.limit(limit)
				.offset(limit * page)
				.fetch();

		List<PollDTO> res = new ArrayList<PollDTO>(records.size());
		for (PollRecord record : records) {
			res.add(recordToDTO(record));
		}

		Object count = jooq.selectCount()
				.from(POLL)
				.fetchOne()
				.getValue(0);

		int nrOfPolls = (Integer) count;

		return new PollsDTO(res, nrOfPolls);
	}

	public PollDTO getPoll(long pollId) {

		PollRecord record = jooq.selectFrom(POLL)
				.where(POLL.ID.eq((int) pollId))
				.fetchOne();

		return recordToDTO(record);
	}

	private List<PollQuestion> getPollQuestion(long pollId) {

		Result<PollQuestionRecord> records = jooq.selectFrom(POLL_QUESTION)
				.where(POLL_QUESTION.POLLID.eq((int) pollId))
				.orderBy(POLL_QUESTION.SEQUENCE.asc())
				.fetch();

		List<PollQuestion> res = new ArrayList<PollQuestion>(records.size());
		for (PollQuestionRecord record : records) {
			res.add(recordToDTO(record));
		}

		return res;
	}

	private List<String> getPollVoterNicks(long pollId) {

		Result<Record1<String>> records = jooq.select(POLL_USER.NICK)
				.from(POLL_USER)
				.where(POLL_USER.POLLID.eq((int) pollId))
				.fetch();

		List<String> ret = new ArrayList<String>(records.size());
		for (Record1<String> record : records) {
			ret.add(record.getValue(POLL_USER.NICK));
		}
		return ret;
	}

	private PollDTO recordToDTO(PollRecord record) {
		PollDTO pollDTO = new PollDTO();
		pollDTO.setId(record.getId());
		pollDTO.setTitle(record.getTitle());
		pollDTO.setAuthor(record.getAuthor());
		pollDTO.setText(record.getText());
		pollDTO.setCreationDate(record.getCreationdate());
		pollDTO.setUpdateDate(record.getUpdatedate());
		pollDTO.setPollQuestions(getPollQuestion(pollDTO.getId()));
		pollDTO.setVoterNicks(getPollVoterNicks(pollDTO.getId()));
		return pollDTO;
	}

	private PollQuestion recordToDTO(PollQuestionRecord record) {
		PollQuestion questionDTO = new PollQuestion();
		questionDTO.setPollId(record.getPollid());
		questionDTO.setSequence(record.getSequence());
		questionDTO.setText(record.getText());
		questionDTO.setVotes(record.getVotes());
		return questionDTO;
	}

}
