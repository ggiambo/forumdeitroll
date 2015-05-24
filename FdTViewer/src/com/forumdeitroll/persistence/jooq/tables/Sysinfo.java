/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq.tables;


import com.forumdeitroll.persistence.jooq.Fdtsucker;
import com.forumdeitroll.persistence.jooq.tables.records.SysinfoRecord;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;


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
public class Sysinfo extends TableImpl<SysinfoRecord> {

	private static final long serialVersionUID = -1795755479;

	/**
	 * The reference instance of <code>fdtsucker.sysinfo</code>
	 */
	public static final Sysinfo SYSINFO = new Sysinfo();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<SysinfoRecord> getRecordType() {
		return SysinfoRecord.class;
	}

	/**
	 * The column <code>fdtsucker.sysinfo.key</code>.
	 */
	public final TableField<SysinfoRecord, String> KEY = createField("key", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

	/**
	 * The column <code>fdtsucker.sysinfo.value</code>.
	 */
	public final TableField<SysinfoRecord, String> VALUE = createField("value", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

	/**
	 * Create a <code>fdtsucker.sysinfo</code> table reference
	 */
	public Sysinfo() {
		this("sysinfo", null);
	}

	/**
	 * Create an aliased <code>fdtsucker.sysinfo</code> table reference
	 */
	public Sysinfo(String alias) {
		this(alias, SYSINFO);
	}

	private Sysinfo(String alias, Table<SysinfoRecord> aliased) {
		this(alias, aliased, null);
	}

	private Sysinfo(String alias, Table<SysinfoRecord> aliased, Field<?>[] parameters) {
		super(alias, Fdtsucker.FDTSUCKER, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Sysinfo as(String alias) {
		return new Sysinfo(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Sysinfo rename(String name) {
		return new Sysinfo(name, null);
	}
}
