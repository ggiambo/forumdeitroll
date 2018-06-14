/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq.tables;


import com.forumdeitroll.persistence.jooq.Fdtsucker;
import com.forumdeitroll.persistence.jooq.tables.records.AuthorsRecord;

import java.sql.Date;

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
public class Authors extends TableImpl<AuthorsRecord> {

	private static final long serialVersionUID = -321282346;

	/**
	 * The reference instance of <code>fdtsucker.authors</code>
	 */
	public static final Authors AUTHORS = new Authors();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<AuthorsRecord> getRecordType() {
		return AuthorsRecord.class;
	}

	/**
	 * The column <code>fdtsucker.authors.nick</code>.
	 */
	public final TableField<AuthorsRecord, String> NICK = createField("nick", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

	/**
	 * The column <code>fdtsucker.authors.messages</code>.
	 */
	public final TableField<AuthorsRecord, Integer> MESSAGES = createField("messages", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>fdtsucker.authors.avatar</code>.
	 */
	public final TableField<AuthorsRecord, byte[]> AVATAR = createField("avatar", org.jooq.impl.SQLDataType.BLOB, this, "");

	/**
	 * The column <code>fdtsucker.authors.password</code>.
	 */
	public final TableField<AuthorsRecord, String> PASSWORD = createField("password", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

	/**
	 * The column <code>fdtsucker.authors.salt</code>.
	 */
	public final TableField<AuthorsRecord, String> SALT = createField("salt", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * The column <code>fdtsucker.authors.hash</code>.
	 */
	public final TableField<AuthorsRecord, String> HASH = createField("hash", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * The column <code>fdtsucker.authors.signature_image</code>.
	 */
	public final TableField<AuthorsRecord, byte[]> SIGNATURE_IMAGE = createField("signature_image", org.jooq.impl.SQLDataType.BLOB, this, "");

	/**
	 * The column <code>fdtsucker.authors.creationDate</code>.
	 */
	public final TableField<AuthorsRecord, Date> CREATIONDATE = createField("creationDate", org.jooq.impl.SQLDataType.DATE, this, "");

	/**
	 * The column <code>fdtsucker.authors.enabled</code>.
	 */
	public final TableField<AuthorsRecord, Byte> ENABLED = createField("enabled", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaulted(true), this, "");

	/**
	 * Create a <code>fdtsucker.authors</code> table reference
	 */
	public Authors() {
		this("authors", null);
	}

	/**
	 * Create an aliased <code>fdtsucker.authors</code> table reference
	 */
	public Authors(String alias) {
		this(alias, AUTHORS);
	}

	private Authors(String alias, Table<AuthorsRecord> aliased) {
		this(alias, aliased, null);
	}

	private Authors(String alias, Table<AuthorsRecord> aliased, Field<?>[] parameters) {
		super(alias, Fdtsucker.FDTSUCKER, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Authors as(String alias) {
		return new Authors(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Authors rename(String name) {
		return new Authors(name, null);
	}
}