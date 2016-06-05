/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq.tables.records;


import com.forumdeitroll.persistence.jooq.tables.Ads;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


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
public class AdsRecord extends UpdatableRecordImpl<AdsRecord> implements Record4<Integer, String, String, String> {

	private static final long serialVersionUID = 1153053785;

	/**
	 * Setter for <code>fdtsucker.ads.id</code>.
	 */
	public AdsRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.ads.id</code>.
	 */
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>fdtsucker.ads.title</code>.
	 */
	public AdsRecord setTitle(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.ads.title</code>.
	 */
	public String getTitle() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>fdtsucker.ads.visurl</code>.
	 */
	public AdsRecord setVisurl(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.ads.visurl</code>.
	 */
	public String getVisurl() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>fdtsucker.ads.content</code>.
	 */
	public AdsRecord setContent(String value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.ads.content</code>.
	 */
	public String getContent() {
		return (String) getValue(3);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<Integer> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record4 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row4<Integer, String, String, String> fieldsRow() {
		return (Row4) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row4<Integer, String, String, String> valuesRow() {
		return (Row4) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return Ads.ADS.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return Ads.ADS.TITLE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return Ads.ADS.VISURL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return Ads.ADS.CONTENT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getTitle();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getVisurl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value4() {
		return getContent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AdsRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AdsRecord value2(String value) {
		setTitle(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AdsRecord value3(String value) {
		setVisurl(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AdsRecord value4(String value) {
		setContent(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AdsRecord values(Integer value1, String value2, String value3, String value4) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached AdsRecord
	 */
	public AdsRecord() {
		super(Ads.ADS);
	}

	/**
	 * Create a detached, initialised AdsRecord
	 */
	public AdsRecord(Integer id, String title, String visurl, String content) {
		super(Ads.ADS);

		setValue(0, id);
		setValue(1, title);
		setValue(2, visurl);
		setValue(3, content);
	}
}