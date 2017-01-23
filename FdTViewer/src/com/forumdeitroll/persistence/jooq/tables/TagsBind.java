/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq.tables;


import com.forumdeitroll.persistence.jooq.Fdtsucker;
import com.forumdeitroll.persistence.jooq.Keys;
import com.forumdeitroll.persistence.jooq.tables.records.TagsBindRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
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
public class TagsBind extends TableImpl<TagsBindRecord> {

	private static final long serialVersionUID = -928464795;

	/**
	 * The reference instance of <code>fdtsucker.tags_bind</code>
	 */
	public static final TagsBind TAGS_BIND = new TagsBind();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<TagsBindRecord> getRecordType() {
		return TagsBindRecord.class;
	}

	/**
	 * The column <code>fdtsucker.tags_bind.t_id</code>.
	 */
	public final TableField<TagsBindRecord, Integer> T_ID = createField("t_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>fdtsucker.tags_bind.m_id</code>.
	 */
	public final TableField<TagsBindRecord, Integer> M_ID = createField("m_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>fdtsucker.tags_bind.author</code>.
	 */
	public final TableField<TagsBindRecord, String> AUTHOR = createField("author", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

	/**
	 * Create a <code>fdtsucker.tags_bind</code> table reference
	 */
	public TagsBind() {
		this("tags_bind", null);
	}

	/**
	 * Create an aliased <code>fdtsucker.tags_bind</code> table reference
	 */
	public TagsBind(String alias) {
		this(alias, TAGS_BIND);
	}

	private TagsBind(String alias, Table<TagsBindRecord> aliased) {
		this(alias, aliased, null);
	}

	private TagsBind(String alias, Table<TagsBindRecord> aliased, Field<?>[] parameters) {
		super(alias, Fdtsucker.FDTSUCKER, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<TagsBindRecord> getPrimaryKey() {
		return Keys.KEY_TAGS_BIND_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<TagsBindRecord>> getKeys() {
		return Arrays.<UniqueKey<TagsBindRecord>>asList(Keys.KEY_TAGS_BIND_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsBind as(String alias) {
		return new TagsBind(alias, this);
	}

	/**
	 * Rename this table
	 */
	public TagsBind rename(String name) {
		return new TagsBind(name, null);
	}
}