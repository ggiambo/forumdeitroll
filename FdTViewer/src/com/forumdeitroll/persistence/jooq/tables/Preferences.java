/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq.tables;


import com.forumdeitroll.persistence.jooq.Fdtsucker;
import com.forumdeitroll.persistence.jooq.tables.records.PreferencesRecord;

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
public class Preferences extends TableImpl<PreferencesRecord> {

	private static final long serialVersionUID = 1149826652;

	/**
	 * The reference instance of <code>fdtsucker.preferences</code>
	 */
	public static final Preferences PREFERENCES = new Preferences();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<PreferencesRecord> getRecordType() {
		return PreferencesRecord.class;
	}

	/**
	 * The column <code>fdtsucker.preferences.nick</code>.
	 */
	public final TableField<PreferencesRecord, String> NICK = createField("nick", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

	/**
	 * The column <code>fdtsucker.preferences.key</code>.
	 */
	public final TableField<PreferencesRecord, String> KEY = createField("key", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

	/**
	 * The column <code>fdtsucker.preferences.value</code>.
	 */
	public final TableField<PreferencesRecord, String> VALUE = createField("value", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

	/**
	 * Create a <code>fdtsucker.preferences</code> table reference
	 */
	public Preferences() {
		this("preferences", null);
	}

	/**
	 * Create an aliased <code>fdtsucker.preferences</code> table reference
	 */
	public Preferences(String alias) {
		this(alias, PREFERENCES);
	}

	private Preferences(String alias, Table<PreferencesRecord> aliased) {
		this(alias, aliased, null);
	}

	private Preferences(String alias, Table<PreferencesRecord> aliased, Field<?>[] parameters) {
		super(alias, Fdtsucker.FDTSUCKER, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Preferences as(String alias) {
		return new Preferences(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Preferences rename(String name) {
		return new Preferences(name, null);
	}
}
