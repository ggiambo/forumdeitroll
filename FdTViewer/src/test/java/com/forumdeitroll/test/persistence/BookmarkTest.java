package com.forumdeitroll.test.persistence;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.BookmarkDTO;
import com.forumdeitroll.test.BaseTest;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

public class BookmarkTest extends BaseTest {

	@Test
	public void test_getBookmarks() {

		AuthorDTO author = new AuthorDTO(null);
		author.setNick("Sfigato");

		List<BookmarkDTO> res = bookmarksDAO.getBookmarks(author);
		assertNotNull(res);
		assertEquals(2, res.size());

		// sort by msgid
		Collections.sort(res, new Comparator<BookmarkDTO>() {
			public int compare(BookmarkDTO q1, BookmarkDTO q2) {
				double delta = q1.getMsgId() - q2.getMsgId();
				return (int) delta;
			}
		});

		BookmarkDTO bookmark = res.get(0);
		assertEquals(author.getNick(), bookmark.getNick());
		assertEquals(4, bookmark.getMsgId());
		assertEquals("Stupido reply", bookmark.getSubject());

		bookmark = res.get(1);
		assertEquals(author.getNick(), bookmark.getNick());
		assertEquals(7, bookmark.getMsgId());
		assertEquals("Mio messaggio", bookmark.getSubject());

	}

	@Test
	public void test_existsBookmark() {

		BookmarkDTO bookmark = new BookmarkDTO();
		bookmark.setNick("Sfigato");
		bookmark.setMsgId(4);
		assertTrue(bookmarksDAO.existsBookmark(bookmark));

		bookmark = new BookmarkDTO();
		bookmark.setNick("Sfigato");
		bookmark.setMsgId(1);
		assertFalse(bookmarksDAO.existsBookmark(bookmark));
	}

	@Test
	public void test_addBookmark() {

		AuthorDTO author = new AuthorDTO(null);
		author.setNick("admin");

		BookmarkDTO newBookmark = new BookmarkDTO();
		newBookmark.setNick(author.getNick());
		newBookmark.setMsgId(9);
		newBookmark.setSubject("Reply del reply");
		bookmarksDAO.addBookmark(newBookmark);

		List<BookmarkDTO> res = bookmarksDAO.getBookmarks(author);
		assertNotNull(res);
		assertEquals(2, res.size());

		// sort by msgid
		Collections.sort(res, new Comparator<BookmarkDTO>() {
			public int compare(BookmarkDTO q1, BookmarkDTO q2) {
				double delta = q1.getMsgId() - q2.getMsgId();
				return (int) delta;
			}
		});

		BookmarkDTO bookmark = res.get(0);
		assertEquals(author.getNick(), bookmark.getNick());
		assertEquals(1, bookmark.getMsgId());
		assertEquals("Primissimo messaggio", bookmark.getSubject());

		bookmark = res.get(1);
		assertEquals(author.getNick(), bookmark.getNick());
		assertEquals(newBookmark.getNick(), bookmark.getNick());
		assertEquals(9, bookmark.getMsgId());
		assertEquals(newBookmark.getSubject(), bookmark.getSubject());

	}

	@Test
	public void test_deleteBookmark() {

		AuthorDTO author = new AuthorDTO(null);
		author.setNick("Sfigato");

		BookmarkDTO removedBookmark = new BookmarkDTO();
		removedBookmark.setNick(author.getNick());
		removedBookmark.setMsgId(4);
		bookmarksDAO.deleteBookmark(removedBookmark);

		List<BookmarkDTO> res = bookmarksDAO.getBookmarks(author);
		assertNotNull(res);
		assertEquals(1, res.size());

		// sort by msgid
		Collections.sort(res, new Comparator<BookmarkDTO>() {
			public int compare(BookmarkDTO q1, BookmarkDTO q2) {
				double delta = q1.getMsgId() - q2.getMsgId();
				return (int) delta;
			}
		});

		BookmarkDTO bookmark = res.get(0);
		assertEquals(author.getNick(), bookmark.getNick());
		assertEquals(7, bookmark.getMsgId());
		assertEquals("Mio messaggio", bookmark.getSubject());

	}

	@Test
	public void test_editBookmark() {

		AuthorDTO author = new AuthorDTO(null);
		author.setNick("Sfigato");

		BookmarkDTO editedBookmark = new BookmarkDTO();
		editedBookmark.setNick(author.getNick());
		editedBookmark.setMsgId(4);
		editedBookmark.setSubject("Nuovo soggetto !!");

		bookmarksDAO.editBookmark(editedBookmark);

		List<BookmarkDTO> res = bookmarksDAO.getBookmarks(author);
		assertNotNull(res);
		assertEquals(2, res.size());

		// sort by msgid
		Collections.sort(res, new Comparator<BookmarkDTO>() {
			public int compare(BookmarkDTO q1, BookmarkDTO q2) {
				double delta = q1.getMsgId() - q2.getMsgId();
				return (int) delta;
			}
		});

		BookmarkDTO bookmark = res.get(0);
		assertEquals(author.getNick(), bookmark.getNick());
		assertEquals(4, bookmark.getMsgId());
		assertEquals("Nuovo soggetto !!", bookmark.getSubject());

		bookmark = res.get(1);
		assertEquals(author.getNick(), bookmark.getNick());
		assertEquals(7, bookmark.getMsgId());
		assertEquals("Mio messaggio", bookmark.getSubject());

	}

}
