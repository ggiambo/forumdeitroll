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
public class Digest extends org.jooq.impl.TableImpl<com.forumdeitroll.persistence.jooq.tables.records.DigestRecord> {

	private static final long serialVersionUID = 2042800999;

	/**
	 * The singleton instance of <code>fdtsucker.digest</code>
	 */
	public static final com.forumdeitroll.persistence.jooq.tables.Digest DIGEST = new com.forumdeitroll.persistence.jooq.tables.Digest();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.forumdeitroll.persistence.jooq.tables.records.DigestRecord> getRecordType() {
		return com.forumdeitroll.persistence.jooq.tables.records.DigestRecord.class;
	}

	/**
	 * The column <code>fdtsucker.digest.threadId</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.DigestRecord, java.lang.Integer> THREADID = createField("threadId", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>fdtsucker.digest.author</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.DigestRecord, java.lang.String> AUTHOR = createField("author", org.jooq.impl.SQLDataType.CLOB.length(255), this, "");

	/**
	 * The column <code>fdtsucker.digest.subject</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.DigestRecord, java.lang.String> SUBJECT = createField("subject", org.jooq.impl.SQLDataType.CLOB.length(255), this, "");

	/**
	 * The column <code>fdtsucker.digest.opener_text</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.DigestRecord, java.lang.String> OPENER_TEXT = createField("opener_text", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * The column <code>fdtsucker.digest.excerpt</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.DigestRecord, java.lang.String> EXCERPT = createField("excerpt", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * The column <code>fdtsucker.digest.nrOfMessages</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.DigestRecord, java.lang.Integer> NROFMESSAGES = createField("nrOfMessages", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>fdtsucker.digest.startDate</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.DigestRecord, java.sql.Timestamp> STARTDATE = createField("startDate", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * The column <code>fdtsucker.digest.lastDate</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.DigestRecord, java.sql.Timestamp> LASTDATE = createField("lastDate", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * Create a <code>fdtsucker.digest</code> table reference
	 */
	public Digest() {
		this("digest", null);
	}

	/**
	 * Create an aliased <code>fdtsucker.digest</code> table reference
	 */
	public Digest(java.lang.String alias) {
		this(alias, com.forumdeitroll.persistence.jooq.tables.Digest.DIGEST);
	}

	private Digest(java.lang.String alias, org.jooq.Table<com.forumdeitroll.persistence.jooq.tables.records.DigestRecord> aliased) {
		this(alias, aliased, null);
	}

	private Digest(java.lang.String alias, org.jooq.Table<com.forumdeitroll.persistence.jooq.tables.records.DigestRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, com.forumdeitroll.persistence.jooq.Fdtsucker.FDTSUCKER, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.forumdeitroll.persistence.jooq.tables.Digest as(java.lang.String alias) {
		return new com.forumdeitroll.persistence.jooq.tables.Digest(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.forumdeitroll.persistence.jooq.tables.Digest rename(java.lang.String name) {
		return new com.forumdeitroll.persistence.jooq.tables.Digest(name, null);
	}
}