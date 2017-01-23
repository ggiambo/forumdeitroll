/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq.tables.records;


import com.forumdeitroll.persistence.jooq.tables.Bookmarks;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Row;
import org.jooq.Row3;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.1"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BookmarksRecord extends TableRecordImpl<BookmarksRecord> implements Record3<String, Integer, String> {

	private static final long serialVersionUID = -1435715886;

	/**
	 * Setter for <code>fdtsucker.bookmarks.nick</code>.
	 */
	public BookmarksRecord setNick(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.bookmarks.nick</code>.
	 */
	public String getNick() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>fdtsucker.bookmarks.msgId</code>.
	 */
	public BookmarksRecord setMsgid(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.bookmarks.msgId</code>.
	 */
	public Integer getMsgid() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>fdtsucker.bookmarks.subject</code>.
	 */
	public BookmarksRecord setSubject(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.bookmarks.subject</code>.
	 */
	public String getSubject() {
		return (String) getValue(2);
	}

	// -------------------------------------------------------------------------
	// Record3 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<String, Integer, String> fieldsRow() {
		return (Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<String, Integer, String> valuesRow() {
		return (Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return Bookmarks.BOOKMARKS.NICK;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return Bookmarks.BOOKMARKS.MSGID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return Bookmarks.BOOKMARKS.SUBJECT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value1() {
		return getNick();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value2() {
		return getMsgid();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getSubject();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BookmarksRecord value1(String value) {
		setNick(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BookmarksRecord value2(Integer value) {
		setMsgid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BookmarksRecord value3(String value) {
		setSubject(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BookmarksRecord values(String value1, Integer value2, String value3) {
		value1(value1);
		value2(value2);
		value3(value3);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached BookmarksRecord
	 */
	public BookmarksRecord() {
		super(Bookmarks.BOOKMARKS);
	}

	/**
	 * Create a detached, initialised BookmarksRecord
	 */
	public BookmarksRecord(String nick, Integer msgid, String subject) {
		super(Bookmarks.BOOKMARKS);

		setValue(0, nick);
		setValue(1, msgid);
		setValue(2, subject);
	}
}