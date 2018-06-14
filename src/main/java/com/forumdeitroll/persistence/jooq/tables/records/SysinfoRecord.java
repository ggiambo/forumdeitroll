/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq.tables.records;


import com.forumdeitroll.persistence.jooq.tables.Sysinfo;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row;
import org.jooq.Row2;
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
public class SysinfoRecord extends TableRecordImpl<SysinfoRecord> implements Record2<String, String> {

	private static final long serialVersionUID = 60345810;

	/**
	 * Setter for <code>fdtsucker.sysinfo.key</code>.
	 */
	public SysinfoRecord setKey(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.sysinfo.key</code>.
	 */
	public String getKey() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>fdtsucker.sysinfo.value</code>.
	 */
	public SysinfoRecord setValue(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.sysinfo.value</code>.
	 */
	public String getValue() {
		return (String) getValue(1);
	}

	// -------------------------------------------------------------------------
	// Record2 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row2<String, String> fieldsRow() {
		return (Row2) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row2<String, String> valuesRow() {
		return (Row2) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return Sysinfo.SYSINFO.KEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return Sysinfo.SYSINFO.VALUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value1() {
		return getKey();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SysinfoRecord value1(String value) {
		setKey(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SysinfoRecord value2(String value) {
		setValue(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SysinfoRecord values(String value1, String value2) {
		value1(value1);
		value2(value2);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached SysinfoRecord
	 */
	public SysinfoRecord() {
		super(Sysinfo.SYSINFO);
	}

	/**
	 * Create a detached, initialised SysinfoRecord
	 */
	public SysinfoRecord(String key, String value) {
		super(Sysinfo.SYSINFO);

		setValue(0, key);
		setValue(1, value);
	}
}