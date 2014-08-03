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
public class PollUser extends org.jooq.impl.TableImpl<com.forumdeitroll.persistence.jooq.tables.records.PollUserRecord> {

	private static final long serialVersionUID = -1445655339;

	/**
	 * The singleton instance of <code>fdtsucker.poll_user</code>
	 */
	public static final com.forumdeitroll.persistence.jooq.tables.PollUser POLL_USER = new com.forumdeitroll.persistence.jooq.tables.PollUser();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.forumdeitroll.persistence.jooq.tables.records.PollUserRecord> getRecordType() {
		return com.forumdeitroll.persistence.jooq.tables.records.PollUserRecord.class;
	}

	/**
	 * The column <code>fdtsucker.poll_user.nick</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.PollUserRecord, java.lang.String> NICK = createField("nick", org.jooq.impl.SQLDataType.VARCHAR.length(256).nullable(false), this, "");

	/**
	 * The column <code>fdtsucker.poll_user.pollId</code>.
	 */
	public final org.jooq.TableField<com.forumdeitroll.persistence.jooq.tables.records.PollUserRecord, java.lang.Integer> POLLID = createField("pollId", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * Create a <code>fdtsucker.poll_user</code> table reference
	 */
	public PollUser() {
		this("poll_user", null);
	}

	/**
	 * Create an aliased <code>fdtsucker.poll_user</code> table reference
	 */
	public PollUser(java.lang.String alias) {
		this(alias, com.forumdeitroll.persistence.jooq.tables.PollUser.POLL_USER);
	}

	private PollUser(java.lang.String alias, org.jooq.Table<com.forumdeitroll.persistence.jooq.tables.records.PollUserRecord> aliased) {
		this(alias, aliased, null);
	}

	private PollUser(java.lang.String alias, org.jooq.Table<com.forumdeitroll.persistence.jooq.tables.records.PollUserRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, com.forumdeitroll.persistence.jooq.Fdtsucker.FDTSUCKER, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.forumdeitroll.persistence.jooq.tables.PollUser as(java.lang.String alias) {
		return new com.forumdeitroll.persistence.jooq.tables.PollUser(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.forumdeitroll.persistence.jooq.tables.PollUser rename(java.lang.String name) {
		return new com.forumdeitroll.persistence.jooq.tables.PollUser(name, null);
	}
}
