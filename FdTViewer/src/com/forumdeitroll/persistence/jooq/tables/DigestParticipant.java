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
public class DigestParticipant extends org.jooq.impl.TableImpl<com.forumdeitroll.persistence.jooq.tables.records.DigestParticipantRecord> {

	private static final long serialVersionUID = 354604563;

	/**
	 * The singleton instance of <code>fdtsucker.digest_participant</code>
	 */
	public static final com.forumdeitroll.persistence.jooq.tables.DigestParticipant DIGEST_PARTICIPANT = new com.forumdeitroll.persistence.jooq.tables.DigestParticipant();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.forumdeitroll.persistence.jooq.tables.records.DigestParticipantRecord> getRecordType() {
		return com.forumdeitroll.persistence.jooq.tables.records.DigestParticipantRecord.class;
	}

	/**
	 * The column <code>fdtsucker.digest_participant.threadId</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.DigestParticipantRecord, java.lang.Integer> THREADID = createField("threadId", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>fdtsucker.digest_participant.author</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.DigestParticipantRecord, java.lang.String> AUTHOR = createField("author", org.jooq.impl.SQLDataType.CLOB.length(255), this, "");

	/**
	 * Create a <code>fdtsucker.digest_participant</code> table reference
	 */
	public DigestParticipant() {
		this("digest_participant", null);
	}

	/**
	 * Create an aliased <code>fdtsucker.digest_participant</code> table reference
	 */
	public DigestParticipant(java.lang.String alias) {
		this(alias, com.forumdeitroll.persistence.jooq.tables.DigestParticipant.DIGEST_PARTICIPANT);
	}

	private DigestParticipant(java.lang.String alias, org.jooq.Table<com.forumdeitroll.persistence.jooq.tables.records.DigestParticipantRecord> aliased) {
		this(alias, aliased, null);
	}

	private DigestParticipant(java.lang.String alias, org.jooq.Table<com.forumdeitroll.persistence.jooq.tables.records.DigestParticipantRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, com.forumdeitroll.persistence.jooq.Fdtsucker.FDTSUCKER, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.forumdeitroll.persistence.jooq.tables.DigestParticipant as(java.lang.String alias) {
		return new com.forumdeitroll.persistence.jooq.tables.DigestParticipant(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.forumdeitroll.persistence.jooq.tables.DigestParticipant rename(java.lang.String name) {
		return new com.forumdeitroll.persistence.jooq.tables.DigestParticipant(name, null);
	}
}