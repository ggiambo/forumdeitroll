package com.forumdeitroll.persistence.dao;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.BookmarkDTO;
import com.forumdeitroll.persistence.jooq.tables.records.BookmarksRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;

import static com.forumdeitroll.persistence.jooq.Tables.BOOKMARKS;
import static com.forumdeitroll.persistence.sql.mysql.Utf8Mb4Conv.mb4safe;

public class BookmarksDAO extends BaseDAO {

	public BookmarksDAO(DSLContext jooq) {
		super(jooq);
	}

	public List<BookmarkDTO> getBookmarks(AuthorDTO owner) {

		Result<BookmarksRecord> records = jooq.selectFrom(BOOKMARKS)
				.where(BOOKMARKS.NICK.eq(owner.getNick()))
				.fetch();

		List<BookmarkDTO> ret = new ArrayList<>(records.size());
		for (BookmarksRecord record : records) {
			ret.add(recordToDTO(record));
		}

		return ret;
	}

	public boolean existsBookmark(BookmarkDTO bookmark) {

		Object count = jooq.selectCount()
			.from(BOOKMARKS)
			.where(BOOKMARKS.NICK.eq(bookmark.getNick()))
			.and(BOOKMARKS.MSGID.eq((int) bookmark.getMsgId()))
			.fetchOne()
			.getValue(0);

		return ((Integer)count) > 0;
	}

	public void addBookmark(BookmarkDTO bookmark) {
		jooq.insertInto(BOOKMARKS)
				.set(BOOKMARKS.NICK, bookmark.getNick())
				.set(BOOKMARKS.MSGID, (int)bookmark.getMsgId())
				.set(BOOKMARKS.SUBJECT, mb4safe(bookmark.getSubject()))
				.execute();
	}

	public void deleteBookmark(BookmarkDTO bookmark) {
		jooq.delete(BOOKMARKS)
			.where(BOOKMARKS.NICK.eq(bookmark.getNick()))
			.and(BOOKMARKS.MSGID.eq((int) bookmark.getMsgId()))
			.execute();
	}

	public void editBookmark(BookmarkDTO bookmark) {
		jooq.update(BOOKMARKS)
			.set(BOOKMARKS.SUBJECT, bookmark.getSubject())
			.where(BOOKMARKS.MSGID.eq((int) bookmark.getMsgId()))
			.and(BOOKMARKS.NICK.equal(bookmark.getNick()))
			.execute();
	}

	private BookmarkDTO recordToDTO(BookmarksRecord record) {
		BookmarkDTO bookmark = new BookmarkDTO();
		bookmark.setNick(record.getNick());
		bookmark.setMsgId(record.getMsgid());
		bookmark.setSubject(record.getSubject());
		if (StringUtils.isEmpty(bookmark.getSubject())) {
			bookmark.setSubject("Vai al messaggio " + bookmark.getMsgId());
		}
		return bookmark;
	}

}
