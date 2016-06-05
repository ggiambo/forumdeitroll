/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq.tables.records;


import com.forumdeitroll.persistence.jooq.tables.TagsBind;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Row;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


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
public class TagsBindRecord extends UpdatableRecordImpl<TagsBindRecord> implements Record3<Integer, Integer, String> {

	private static final long serialVersionUID = 1754501032;

	/**
	 * Setter for <code>fdtsucker.tags_bind.t_id</code>.
	 */
	public TagsBindRecord setTId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.tags_bind.t_id</code>.
	 */
	public Integer getTId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>fdtsucker.tags_bind.m_id</code>.
	 */
	public TagsBindRecord setMId(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.tags_bind.m_id</code>.
	 */
	public Integer getMId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>fdtsucker.tags_bind.author</code>.
	 */
	public TagsBindRecord setAuthor(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.tags_bind.author</code>.
	 */
	public String getAuthor() {
		return (String) getValue(2);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record2<Integer, Integer> key() {
		return (Record2) super.key();
	}

	// -------------------------------------------------------------------------
	// Record3 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<Integer, Integer, String> fieldsRow() {
		return (Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<Integer, Integer, String> valuesRow() {
		return (Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return TagsBind.TAGS_BIND.T_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return TagsBind.TAGS_BIND.M_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return TagsBind.TAGS_BIND.AUTHOR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getTId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value2() {
		return getMId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getAuthor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsBindRecord value1(Integer value) {
		setTId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsBindRecord value2(Integer value) {
		setMId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsBindRecord value3(String value) {
		setAuthor(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsBindRecord values(Integer value1, Integer value2, String value3) {
		value1(value1);
		value2(value2);
		value3(value3);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached TagsBindRecord
	 */
	public TagsBindRecord() {
		super(TagsBind.TAGS_BIND);
	}

	/**
	 * Create a detached, initialised TagsBindRecord
	 */
	public TagsBindRecord(Integer tId, Integer mId, String author) {
		super(TagsBind.TAGS_BIND);

		setValue(0, tId);
		setValue(1, mId);
		setValue(2, author);
	}
}