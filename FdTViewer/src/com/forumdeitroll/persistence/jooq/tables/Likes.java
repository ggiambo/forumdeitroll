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
public class Likes extends org.jooq.impl.TableImpl<com.forumdeitroll.persistence.jooq.tables.records.LikesRecord> {

	private static final long serialVersionUID = 710670560;

	/**
	 * The singleton instance of <code>fdtsucker.likes</code>
	 */
	public static final com.forumdeitroll.persistence.jooq.tables.Likes LIKES = new com.forumdeitroll.persistence.jooq.tables.Likes();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.forumdeitroll.persistence.jooq.tables.records.LikesRecord> getRecordType() {
		return com.forumdeitroll.persistence.jooq.tables.records.LikesRecord.class;
	}

	/**
	 * The column <code>fdtsucker.likes.nick</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.LikesRecord, java.lang.String> NICK = createField("nick", org.jooq.impl.SQLDataType.CLOB.length(255).nullable(false), this, "");

	/**
	 * The column <code>fdtsucker.likes.msgId</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.LikesRecord, java.lang.Integer> MSGID = createField("msgId", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>fdtsucker.likes.vote</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.LikesRecord, java.lang.Byte> VOTE = createField("vote", org.jooq.impl.SQLDataType.TINYINT, this, "");

	/**
	 * Create a <code>fdtsucker.likes</code> table reference
	 */
	public Likes() {
		this("likes", null);
	}

	/**
	 * Create an aliased <code>fdtsucker.likes</code> table reference
	 */
	public Likes(java.lang.String alias) {
		this(alias, com.forumdeitroll.persistence.jooq.tables.Likes.LIKES);
	}

	private Likes(java.lang.String alias, org.jooq.Table<com.forumdeitroll.persistence.jooq.tables.records.LikesRecord> aliased) {
		this(alias, aliased, null);
	}

	private Likes(java.lang.String alias, org.jooq.Table<com.forumdeitroll.persistence.jooq.tables.records.LikesRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, com.forumdeitroll.persistence.jooq.Fdtsucker.FDTSUCKER, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.forumdeitroll.persistence.jooq.tables.Likes as(java.lang.String alias) {
		return new com.forumdeitroll.persistence.jooq.tables.Likes(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.forumdeitroll.persistence.jooq.tables.Likes rename(java.lang.String name) {
		return new com.forumdeitroll.persistence.jooq.tables.Likes(name, null);
	}
}
