/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq.tables;


import com.forumdeitroll.persistence.jooq.Fdtsucker;
import com.forumdeitroll.persistence.jooq.Keys;
import com.forumdeitroll.persistence.jooq.tables.records.TagsRecord;

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
public class Tags extends TableImpl<TagsRecord> {

	private static final long serialVersionUID = -2093381615;

	/**
	 * The reference instance of <code>fdtsucker.tags</code>
	 */
	public static final Tags TAGS = new Tags();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<TagsRecord> getRecordType() {
		return TagsRecord.class;
	}

	/**
	 * The column <code>fdtsucker.tags.tagName</code>.
	 */
	public final TableField<TagsRecord, String> TAGNAME = createField("tagName", org.jooq.impl.SQLDataType.VARCHAR.length(12).nullable(false), this, "");

	/**
	 * The column <code>fdtsucker.tags.used</code>.
	 */
	public final TableField<TagsRecord, Integer> USED = createField("used", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * Create a <code>fdtsucker.tags</code> table reference
	 */
	public Tags() {
		this("tags", null);
	}

	/**
	 * Create an aliased <code>fdtsucker.tags</code> table reference
	 */
	public Tags(String alias) {
		this(alias, TAGS);
	}

	private Tags(String alias, Table<TagsRecord> aliased) {
		this(alias, aliased, null);
	}

	private Tags(String alias, Table<TagsRecord> aliased, Field<?>[] parameters) {
		super(alias, Fdtsucker.FDTSUCKER, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<TagsRecord>> getKeys() {
		return Arrays.<UniqueKey<TagsRecord>>asList(Keys.KEY_TAGS_TAGNAME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tags as(String alias) {
		return new Tags(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Tags rename(String name) {
		return new Tags(name, null);
	}
}
