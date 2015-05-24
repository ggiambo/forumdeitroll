/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq.tables.records;


import com.forumdeitroll.persistence.jooq.tables.Authors;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.Row;
import org.jooq.Row7;
import org.jooq.impl.TableRecordImpl;


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
public class AuthorsRecord extends TableRecordImpl<AuthorsRecord> implements Record7<String, Integer, byte[], String, String, String, byte[]> {

	private static final long serialVersionUID = -1448068470;

	/**
	 * Setter for <code>fdtsucker.authors.nick</code>.
	 */
	public AuthorsRecord setNick(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.authors.nick</code>.
	 */
	public String getNick() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>fdtsucker.authors.messages</code>.
	 */
	public AuthorsRecord setMessages(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.authors.messages</code>.
	 */
	public Integer getMessages() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>fdtsucker.authors.avatar</code>.
	 */
	public AuthorsRecord setAvatar(byte[] value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.authors.avatar</code>.
	 */
	public byte[] getAvatar() {
		return (byte[]) getValue(2);
	}

	/**
	 * Setter for <code>fdtsucker.authors.password</code>.
	 */
	public AuthorsRecord setPassword(String value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.authors.password</code>.
	 */
	public String getPassword() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>fdtsucker.authors.salt</code>.
	 */
	public AuthorsRecord setSalt(String value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.authors.salt</code>.
	 */
	public String getSalt() {
		return (String) getValue(4);
	}

	/**
	 * Setter for <code>fdtsucker.authors.hash</code>.
	 */
	public AuthorsRecord setHash(String value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.authors.hash</code>.
	 */
	public String getHash() {
		return (String) getValue(5);
	}

	/**
	 * Setter for <code>fdtsucker.authors.signature_image</code>.
	 */
	public AuthorsRecord setSignatureImage(byte[] value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.authors.signature_image</code>.
	 */
	public byte[] getSignatureImage() {
		return (byte[]) getValue(6);
	}

	// -------------------------------------------------------------------------
	// Record7 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row7<String, Integer, byte[], String, String, String, byte[]> fieldsRow() {
		return (Row7) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row7<String, Integer, byte[], String, String, String, byte[]> valuesRow() {
		return (Row7) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return Authors.AUTHORS.NICK;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return Authors.AUTHORS.MESSAGES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<byte[]> field3() {
		return Authors.AUTHORS.AVATAR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return Authors.AUTHORS.PASSWORD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field5() {
		return Authors.AUTHORS.SALT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field6() {
		return Authors.AUTHORS.HASH;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<byte[]> field7() {
		return Authors.AUTHORS.SIGNATURE_IMAGE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value1() {
		return getNick();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value2() {
		return getMessages();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] value3() {
		return getAvatar();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value4() {
		return getPassword();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value5() {
		return getSalt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value6() {
		return getHash();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] value7() {
		return getSignatureImage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorsRecord value1(String value) {
		setNick(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorsRecord value2(Integer value) {
		setMessages(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorsRecord value3(byte[] value) {
		setAvatar(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorsRecord value4(String value) {
		setPassword(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorsRecord value5(String value) {
		setSalt(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorsRecord value6(String value) {
		setHash(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorsRecord value7(byte[] value) {
		setSignatureImage(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthorsRecord values(String value1, Integer value2, byte[] value3, String value4, String value5, String value6, byte[] value7) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached AuthorsRecord
	 */
	public AuthorsRecord() {
		super(Authors.AUTHORS);
	}

	/**
	 * Create a detached, initialised AuthorsRecord
	 */
	public AuthorsRecord(String nick, Integer messages, byte[] avatar, String password, String salt, String hash, byte[] signatureImage) {
		super(Authors.AUTHORS);

		setValue(0, nick);
		setValue(1, messages);
		setValue(2, avatar);
		setValue(3, password);
		setValue(4, salt);
		setValue(5, hash);
		setValue(6, signatureImage);
	}
}
