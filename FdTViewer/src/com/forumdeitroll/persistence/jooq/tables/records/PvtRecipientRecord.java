/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq.tables.records;


import com.forumdeitroll.persistence.jooq.tables.PvtRecipient;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Row;
import org.jooq.Row4;
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
public class PvtRecipientRecord extends TableRecordImpl<PvtRecipientRecord> implements Record4<Integer, String, Integer, Integer> {

	private static final long serialVersionUID = 949512086;

	/**
	 * Setter for <code>fdtsucker.pvt_recipient.pvt_id</code>.
	 */
	public PvtRecipientRecord setPvtId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.pvt_recipient.pvt_id</code>.
	 */
	public Integer getPvtId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>fdtsucker.pvt_recipient.recipient</code>.
	 */
	public PvtRecipientRecord setRecipient(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.pvt_recipient.recipient</code>.
	 */
	public String getRecipient() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>fdtsucker.pvt_recipient.read</code>.
	 */
	public PvtRecipientRecord setRead(Integer value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.pvt_recipient.read</code>.
	 */
	public Integer getRead() {
		return (Integer) getValue(2);
	}

	/**
	 * Setter for <code>fdtsucker.pvt_recipient.deleted</code>.
	 */
	public PvtRecipientRecord setDeleted(Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.pvt_recipient.deleted</code>.
	 */
	public Integer getDeleted() {
		return (Integer) getValue(3);
	}

	// -------------------------------------------------------------------------
	// Record4 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row4<Integer, String, Integer, Integer> fieldsRow() {
		return (Row4) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row4<Integer, String, Integer, Integer> valuesRow() {
		return (Row4) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return PvtRecipient.PVT_RECIPIENT.PVT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return PvtRecipient.PVT_RECIPIENT.RECIPIENT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return PvtRecipient.PVT_RECIPIENT.READ;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return PvtRecipient.PVT_RECIPIENT.DELETED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getPvtId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getRecipient();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value3() {
		return getRead();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value4() {
		return getDeleted();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvtRecipientRecord value1(Integer value) {
		setPvtId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvtRecipientRecord value2(String value) {
		setRecipient(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvtRecipientRecord value3(Integer value) {
		setRead(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvtRecipientRecord value4(Integer value) {
		setDeleted(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvtRecipientRecord values(Integer value1, String value2, Integer value3, Integer value4) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached PvtRecipientRecord
	 */
	public PvtRecipientRecord() {
		super(PvtRecipient.PVT_RECIPIENT);
	}

	/**
	 * Create a detached, initialised PvtRecipientRecord
	 */
	public PvtRecipientRecord(Integer pvtId, String recipient, Integer read, Integer deleted) {
		super(PvtRecipient.PVT_RECIPIENT);

		setValue(0, pvtId);
		setValue(1, recipient);
		setValue(2, read);
		setValue(3, deleted);
	}
}