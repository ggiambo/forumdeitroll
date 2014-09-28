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
public class SysinfoRecord extends org.jooq.impl.TableRecordImpl<com.forumdeitroll.persistence.jooq.tables.records.SysinfoRecord> implements org.jooq.Record2<java.lang.String, java.lang.String> {

	private static final long serialVersionUID = -1654043644;

	/**
	 * Setter for <code>fdtsucker.sysinfo.key</code>.
	 */
	public SysinfoRecord setKey(java.lang.String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.sysinfo.key</code>.
	 */
	public java.lang.String getKey() {
		return (java.lang.String) getValue(0);
	}

	/**
	 * Setter for <code>fdtsucker.sysinfo.value</code>.
	 */
	public SysinfoRecord setValue(java.lang.String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.sysinfo.value</code>.
	 */
	public java.lang.String getValue() {
		return (java.lang.String) getValue(1);
	}

	// -------------------------------------------------------------------------
	// Record2 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row2<java.lang.String, java.lang.String> fieldsRow() {
		return (org.jooq.Row2) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row2<java.lang.String, java.lang.String> valuesRow() {
		return (org.jooq.Row2) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field1() {
		return com.forumdeitroll.persistence.jooq.tables.Sysinfo.SYSINFO.KEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field2() {
		return com.forumdeitroll.persistence.jooq.tables.Sysinfo.SYSINFO.VALUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value1() {
		return getKey();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value2() {
		return getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SysinfoRecord value1(java.lang.String value) {
		setKey(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SysinfoRecord value2(java.lang.String value) {
		setValue(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SysinfoRecord values(java.lang.String value1, java.lang.String value2) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached SysinfoRecord
	 */
	public SysinfoRecord() {
		super(com.forumdeitroll.persistence.jooq.tables.Sysinfo.SYSINFO);
	}

	/**
	 * Create a detached, initialised SysinfoRecord
	 */
	public SysinfoRecord(java.lang.String key, java.lang.String value) {
		super(com.forumdeitroll.persistence.jooq.tables.Sysinfo.SYSINFO);

		setValue(0, key);
		setValue(1, value);
	}
}