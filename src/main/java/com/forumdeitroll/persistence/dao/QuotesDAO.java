package com.forumdeitroll.persistence.dao;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.QuoteDTO;
import com.forumdeitroll.persistence.jooq.tables.records.QuotesRecord;
import org.jooq.*;

import java.util.ArrayList;
import java.util.List;

import static com.forumdeitroll.persistence.jooq.Tables.AUTHORS;
import static com.forumdeitroll.persistence.jooq.Tables.QUOTES;
import static com.forumdeitroll.persistence.sql.mysql.Utf8Mb4Conv.mb4safe;

public class QuotesDAO extends BaseDAO {

	public QuotesDAO(DSLContext jooq) {
		super(jooq);
	}

	public List<QuoteDTO> getQuotes(AuthorDTO author) {

		Result<QuotesRecord> records = jooq.selectFrom(QUOTES)
				.where(QUOTES.NICK.equal(author.getNick()))
				.orderBy(QUOTES.ID.asc())
				.fetch();


		List<QuoteDTO> out = new ArrayList<>(records.size());
		for (QuotesRecord record : records) {
			out.add(recordToDTO(record));
		}

		return out;
	}

	public List<QuoteDTO> getAllQuotes() {
		return getAllQuotes(-1, -1);
	}

	public List<QuoteDTO> getAllQuotes(int limit, int page) {

		SelectLimitStep<Record3<Integer, String, String>> select = jooq.select(QUOTES.ID, AUTHORS.NICK, QUOTES.CONTENT)
				.from(QUOTES)
				.join(AUTHORS).on(QUOTES.NICK.eq(AUTHORS.NICK))
				.where(AUTHORS.MESSAGES.greaterThan(0))
				.and(AUTHORS.HASH.notEqual(AuthorDTO.BANNED_TAG))
				.orderBy(QUOTES.NICK.asc());

		Result<Record3<Integer, String, String>> records;
		if (limit > 0) {
			select = (SelectLimitStep<Record3<Integer, String, String>>) select
					.limit(limit)
					.offset(limit * page);
		}

		records = select.fetch();

		final List<QuoteDTO> out = new ArrayList<>(records.size());
		for (Record record : records) {
			QuoteDTO dto = new QuoteDTO();
			dto.setId(record.getValue(QUOTES.ID).longValue());
			dto.setContent(record.getValue(QUOTES.CONTENT));
			dto.setNick(record.getValue(QUOTES.NICK));
			out.add(dto);
		}

		return out;
	}

	public void insertUpdateQuote(QuoteDTO quote) {
		if (quote.getId() > 0) {
			updateQuote(quote);
		} else {
			insertQuote(quote);
		}
	}

	public void removeQuote(QuoteDTO quote) {
		jooq.delete(QUOTES)
				.where(QUOTES.ID.eq((int) quote.getId()))
				.and((QUOTES.NICK.eq(quote.getNick())))
				.execute();
	}

	private void updateQuote(QuoteDTO quote) {
		jooq.update(QUOTES)
				.set(QUOTES.NICK, quote.getNick())
				.set(QUOTES.CONTENT, mb4safe(quote.getContent()))
				.where(QUOTES.ID.eq((int) quote.getId()))
				.and(QUOTES.NICK.equal(quote.getNick()))
				.execute();

	}

	private void insertQuote(QuoteDTO quote) {
		jooq.insertInto(QUOTES)
				.set(QUOTES.NICK,quote.getNick())
				.set(QUOTES.CONTENT, mb4safe(quote.getContent()))
				.returning(QUOTES.ID)
				.fetchOne();
	}

	private QuoteDTO recordToDTO(QuotesRecord record) {
		QuoteDTO dto = new QuoteDTO();
		dto.setId(record.getId());
		dto.setContent(record.getContent());
		dto.setNick(record.getNick());
		return dto;
	}

}
