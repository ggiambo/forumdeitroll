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
public class Tagnames extends org.jooq.impl.TableImpl<com.forumdeitroll.persistence.jooq.tables.records.TagnamesRecord> {

	private static final long serialVersionUID = -243607749;

	/**
	 * The singleton instance of <code>fdtsucker.tagnames</code>
	 */
	public static final com.forumdeitroll.persistence.jooq.tables.Tagnames TAGNAMES = new com.forumdeitroll.persistence.jooq.tables.Tagnames();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.forumdeitroll.persistence.jooq.tables.records.TagnamesRecord> getRecordType() {
		return com.forumdeitroll.persistence.jooq.tables.records.TagnamesRecord.class;
	}

	/**
	 * The column <code>fdtsucker.tagnames.t_id</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.TagnamesRecord, java.lang.Integer> T_ID = createField("t_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>fdtsucker.tagnames.value</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.TagnamesRecord, java.lang.String> VALUE = createField("value", org.jooq.impl.SQLDataType.CLOB.length(255).nullable(false), this, "");

	/**
	 * Create a <code>fdtsucker.tagnames</code> table reference
	 */
	public Tagnames() {
		this("tagnames", null);
	}

	/**
	 * Create an aliased <code>fdtsucker.tagnames</code> table reference
	 */
	public Tagnames(java.lang.String alias) {
		this(alias, com.forumdeitroll.persistence.jooq.tables.Tagnames.TAGNAMES);
	}

	private Tagnames(java.lang.String alias, org.jooq.Table<com.forumdeitroll.persistence.jooq.tables.records.TagnamesRecord> aliased) {
		this(alias, aliased, null);
	}

	private Tagnames(java.lang.String alias, org.jooq.Table<com.forumdeitroll.persistence.jooq.tables.records.TagnamesRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, com.forumdeitroll.persistence.jooq.Fdtsucker.FDTSUCKER, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<com.forumdeitroll.persistence.jooq.tables.records.TagnamesRecord, java.lang.Integer> getIdentity() {
		return com.forumdeitroll.persistence.jooq.Keys.IDENTITY_TAGNAMES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<com.forumdeitroll.persistence.jooq.tables.records.TagnamesRecord> getPrimaryKey() {
		return com.forumdeitroll.persistence.jooq.Keys.KEY_TAGNAMES_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<com.forumdeitroll.persistence.jooq.tables.records.TagnamesRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<com.forumdeitroll.persistence.jooq.tables.records.TagnamesRecord>>asList(com.forumdeitroll.persistence.jooq.Keys.KEY_TAGNAMES_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.forumdeitroll.persistence.jooq.tables.Tagnames as(java.lang.String alias) {
		return new com.forumdeitroll.persistence.jooq.tables.Tagnames(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.forumdeitroll.persistence.jooq.tables.Tagnames rename(java.lang.String name) {
		return new com.forumdeitroll.persistence.jooq.tables.Tagnames(name, null);
	}
}