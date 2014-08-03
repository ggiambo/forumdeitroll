/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.3.1" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PvtRecipientRecord extends org.jooq.impl.TableRecordImpl<com.forumdeitroll.persistence.jooq.tables.records.PvtRecipientRecord> implements org.jooq.Record4<java.lang.Integer, java.lang.String, java.lang.Integer, java.lang.Integer> {

	private static final long serialVersionUID = 1068872306;

	/**
	 * Setter for <code>fdtsucker.pvt_recipient.pvt_id</code>.
	 */
	public PvtRecipientRecord setPvtId(java.lang.Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.pvt_recipient.pvt_id</code>.
	 */
	public java.lang.Integer getPvtId() {
		return (java.lang.Integer) getValue(0);
	}

	/**
	 * Setter for <code>fdtsucker.pvt_recipient.recipient</code>.
	 */
	public PvtRecipientRecord setRecipient(java.lang.String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.pvt_recipient.recipient</code>.
	 */
	public java.lang.String getRecipient() {
		return (java.lang.String) getValue(1);
	}

	/**
	 * Setter for <code>fdtsucker.pvt_recipient.read</code>.
	 */
	public PvtRecipientRecord setRead(java.lang.Integer value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.pvt_recipient.read</code>.
	 */
	public java.lang.Integer getRead() {
		return (java.lang.Integer) getValue(2);
	}

	/**
	 * Setter for <code>fdtsucker.pvt_recipient.deleted</code>.
	 */
	public PvtRecipientRecord setDeleted(java.lang.Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.pvt_recipient.deleted</code>.
	 */
	public java.lang.Integer getDeleted() {
		return (java.lang.Integer) getValue(3);
	}

	// -------------------------------------------------------------------------
	// Record4 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<java.lang.Integer, java.lang.String, java.lang.Integer, java.lang.Integer> fieldsRow() {
		return (org.jooq.Row4) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<java.lang.Integer, java.lang.String, java.lang.Integer, java.lang.Integer> valuesRow() {
		return (org.jooq.Row4) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field1() {
		return com.forumdeitroll.persistence.jooq.tables.PvtRecipient.PVT_RECIPIENT.PVT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field2() {
		return com.forumdeitroll.persistence.jooq.tables.PvtRecipient.PVT_RECIPIENT.RECIPIENT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field3() {
		return com.forumdeitroll.persistence.jooq.tables.PvtRecipient.PVT_RECIPIENT.READ;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field4() {
		return com.forumdeitroll.persistence.jooq.tables.PvtRecipient.PVT_RECIPIENT.DELETED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value1() {
		return getPvtId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value2() {
		return getRecipient();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value3() {
		return getRead();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value4() {
		return getDeleted();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvtRecipientRecord value1(java.lang.Integer value) {
		setPvtId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvtRecipientRecord value2(java.lang.String value) {
		setRecipient(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvtRecipientRecord value3(java.lang.Integer value) {
		setRead(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvtRecipientRecord value4(java.lang.Integer value) {
		setDeleted(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvtRecipientRecord values(java.lang.Integer value1, java.lang.String value2, java.lang.Integer value3, java.lang.Integer value4) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached PvtRecipientRecord
	 */
	public PvtRecipientRecord() {
		super(com.forumdeitroll.persistence.jooq.tables.PvtRecipient.PVT_RECIPIENT);
	}

	/**
	 * Create a detached, initialised PvtRecipientRecord
	 */
	public PvtRecipientRecord(java.lang.Integer pvtId, java.lang.String recipient, java.lang.Integer read, java.lang.Integer deleted) {
		super(com.forumdeitroll.persistence.jooq.tables.PvtRecipient.PVT_RECIPIENT);

		setValue(0, pvtId);
		setValue(1, recipient);
		setValue(2, read);
		setValue(3, deleted);
	}
}
