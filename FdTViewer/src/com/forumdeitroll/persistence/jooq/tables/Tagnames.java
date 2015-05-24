/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq.tables;


import com.forumdeitroll.persistence.jooq.Fdtsucker;
import com.forumdeitroll.persistence.jooq.Keys;
import com.forumdeitroll.persistence.jooq.tables.records.TagnamesRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
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
public class Tagnames extends TableImpl<TagnamesRecord> {

	private static final long serialVersionUID = -306842374;

	/**
	 * The reference instance of <code>fdtsucker.tagnames</code>
	 */
	public static final Tagnames TAGNAMES = new Tagnames();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<TagnamesRecord> getRecordType() {
		return TagnamesRecord.class;
	}

	/**
	 * The column <code>fdtsucker.tagnames.t_id</code>.
	 */
	public final TableField<TagnamesRecord, Integer> T_ID = createField("t_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>fdtsucker.tagnames.value</code>.
	 */
	public final TableField<TagnamesRecord, String> VALUE = createField("value", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

	/**
	 * Create a <code>fdtsucker.tagnames</code> table reference
	 */
	public Tagnames() {
		this("tagnames", null);
	}

	/**
	 * Create an aliased <code>fdtsucker.tagnames</code> table reference
	 */
	public Tagnames(String alias) {
		this(alias, TAGNAMES);
	}

	private Tagnames(String alias, Table<TagnamesRecord> aliased) {
		this(alias, aliased, null);
	}

	private Tagnames(String alias, Table<TagnamesRecord> aliased, Field<?>[] parameters) {
		super(alias, Fdtsucker.FDTSUCKER, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<TagnamesRecord, Integer> getIdentity() {
		return Keys.IDENTITY_TAGNAMES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<TagnamesRecord> getPrimaryKey() {
		return Keys.KEY_TAGNAMES_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<TagnamesRecord>> getKeys() {
		return Arrays.<UniqueKey<TagnamesRecord>>asList(Keys.KEY_TAGNAMES_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tagnames as(String alias) {
		return new Tagnames(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Tagnames rename(String name) {
		return new Tagnames(name, null);
	}
}
