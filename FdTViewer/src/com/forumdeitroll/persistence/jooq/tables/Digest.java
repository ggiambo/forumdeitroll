/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq.tables;


import com.forumdeitroll.persistence.jooq.Fdtsucker;
import com.forumdeitroll.persistence.jooq.tables.records.DigestRecord;

import java.sql.Timestamp;

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
public class Digest extends TableImpl<DigestRecord> {

	private static final long serialVersionUID = 470934081;

	/**
	 * The reference instance of <code>fdtsucker.digest</code>
	 */
	public static final Digest DIGEST = new Digest();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<DigestRecord> getRecordType() {
		return DigestRecord.class;
	}

	/**
	 * The column <code>fdtsucker.digest.threadId</code>.
	 */
	public final TableField<DigestRecord, Integer> THREADID = createField("threadId", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>fdtsucker.digest.author</code>.
	 */
	public final TableField<DigestRecord, String> AUTHOR = createField("author", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * The column <code>fdtsucker.digest.subject</code>.
	 */
	public final TableField<DigestRecord, String> SUBJECT = createField("subject", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * The column <code>fdtsucker.digest.opener_text</code>.
	 */
	public final TableField<DigestRecord, String> OPENER_TEXT = createField("opener_text", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * The column <code>fdtsucker.digest.excerpt</code>.
	 */
	public final TableField<DigestRecord, String> EXCERPT = createField("excerpt", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * The column <code>fdtsucker.digest.nrOfMessages</code>.
	 */
	public final TableField<DigestRecord, Integer> NROFMESSAGES = createField("nrOfMessages", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>fdtsucker.digest.startDate</code>.
	 */
	public final TableField<DigestRecord, Timestamp> STARTDATE = createField("startDate", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * The column <code>fdtsucker.digest.lastDate</code>.
	 */
	public final TableField<DigestRecord, Timestamp> LASTDATE = createField("lastDate", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * Create a <code>fdtsucker.digest</code> table reference
	 */
	public Digest() {
		this("digest", null);
	}

	/**
	 * Create an aliased <code>fdtsucker.digest</code> table reference
	 */
	public Digest(String alias) {
		this(alias, DIGEST);
	}

	private Digest(String alias, Table<DigestRecord> aliased) {
		this(alias, aliased, null);
	}

	private Digest(String alias, Table<DigestRecord> aliased, Field<?>[] parameters) {
		super(alias, Fdtsucker.FDTSUCKER, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Digest as(String alias) {
		return new Digest(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Digest rename(String name) {
		return new Digest(name, null);
	}
}
