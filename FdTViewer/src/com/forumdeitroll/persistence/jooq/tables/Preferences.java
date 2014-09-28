/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.3.1" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Preferences extends org.jooq.impl.TableImpl<com.forumdeitroll.persistence.jooq.tables.records.PreferencesRecord> {

	private static final long serialVersionUID = -938643538;

	/**
	 * The singleton instance of <code>fdtsucker.preferences</code>
	 */
	public static final com.forumdeitroll.persistence.jooq.tables.Preferences PREFERENCES = new com.forumdeitroll.persistence.jooq.tables.Preferences();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.forumdeitroll.persistence.jooq.tables.records.PreferencesRecord> getRecordType() {
		return com.forumdeitroll.persistence.jooq.tables.records.PreferencesRecord.class;
	}

	/**
	 * The column <code>fdtsucker.preferences.nick</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.PreferencesRecord, java.lang.String> NICK = createField("nick", org.jooq.impl.SQLDataType.CLOB.length(255).nullable(false), this, "");

	/**
	 * The column <code>fdtsucker.preferences.key</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.PreferencesRecord, java.lang.String> KEY = createField("key", org.jooq.impl.SQLDataType.CLOB.length(255).nullable(false), this, "");

	/**
	 * The column <code>fdtsucker.preferences.value</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.PreferencesRecord, java.lang.String> VALUE = createField("value", org.jooq.impl.SQLDataType.CLOB.length(255).nullable(false), this, "");

	/**
	 * Create a <code>fdtsucker.preferences</code> table reference
	 */
	public Preferences() {
		this("preferences", null);
	}

	/**
	 * Create an aliased <code>fdtsucker.preferences</code> table reference
	 */
	public Preferences(java.lang.String alias) {
		this(alias, com.forumdeitroll.persistence.jooq.tables.Preferences.PREFERENCES);
	}

	private Preferences(java.lang.String alias, org.jooq.Table<com.forumdeitroll.persistence.jooq.tables.records.PreferencesRecord> aliased) {
		this(alias, aliased, null);
	}

	private Preferences(java.lang.String alias, org.jooq.Table<com.forumdeitroll.persistence.jooq.tables.records.PreferencesRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, com.forumdeitroll.persistence.jooq.Fdtsucker.FDTSUCKER, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.forumdeitroll.persistence.jooq.tables.Preferences as(java.lang.String alias) {
		return new com.forumdeitroll.persistence.jooq.tables.Preferences(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.forumdeitroll.persistence.jooq.tables.Preferences rename(java.lang.String name) {
		return new com.forumdeitroll.persistence.jooq.tables.Preferences(name, null);
	}
}