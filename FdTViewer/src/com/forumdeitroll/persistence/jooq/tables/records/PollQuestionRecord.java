/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.3.1" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PollQuestionRecord extends org.jooq.impl.TableRecordImpl<com.forumdeitroll.persistence.jooq.tables.records.PollQuestionRecord> implements org.jooq.Record4<java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.Integer> {

	private static final long serialVersionUID = -2086591471;

	/**
	 * Setter for <code>fdtsucker.poll_question.pollId</code>.
	 */
	public PollQuestionRecord setPollid(java.lang.Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.poll_question.pollId</code>.
	 */
	public java.lang.Integer getPollid() {
		return (java.lang.Integer) getValue(0);
	}

	/**
	 * Setter for <code>fdtsucker.poll_question.sequence</code>.
	 */
	public PollQuestionRecord setSequence(java.lang.Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.poll_question.sequence</code>.
	 */
	public java.lang.Integer getSequence() {
		return (java.lang.Integer) getValue(1);
	}

	/**
	 * Setter for <code>fdtsucker.poll_question.text</code>.
	 */
	public PollQuestionRecord setText(java.lang.String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.poll_question.text</code>.
	 */
	public java.lang.String getText() {
		return (java.lang.String) getValue(2);
	}

	/**
	 * Setter for <code>fdtsucker.poll_question.votes</code>.
	 */
	public PollQuestionRecord setVotes(java.lang.Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>fdtsucker.poll_question.votes</code>.
	 */
	public java.lang.Integer getVotes() {
		return (java.lang.Integer) getValue(3);
	}

	// -------------------------------------------------------------------------
	// Record4 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.Integer> fieldsRow() {
		return (org.jooq.Row4) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.Integer> valuesRow() {
		return (org.jooq.Row4) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field1() {
		return com.forumdeitroll.persistence.jooq.tables.PollQuestion.POLL_QUESTION.POLLID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field2() {
		return com.forumdeitroll.persistence.jooq.tables.PollQuestion.POLL_QUESTION.SEQUENCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field3() {
		return com.forumdeitroll.persistence.jooq.tables.PollQuestion.POLL_QUESTION.TEXT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field4() {
		return com.forumdeitroll.persistence.jooq.tables.PollQuestion.POLL_QUESTION.VOTES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value1() {
		return getPollid();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value2() {
		return getSequence();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value3() {
		return getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value4() {
		return getVotes();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PollQuestionRecord value1(java.lang.Integer value) {
		setPollid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PollQuestionRecord value2(java.lang.Integer value) {
		setSequence(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PollQuestionRecord value3(java.lang.String value) {
		setText(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PollQuestionRecord value4(java.lang.Integer value) {
		setVotes(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PollQuestionRecord values(java.lang.Integer value1, java.lang.Integer value2, java.lang.String value3, java.lang.Integer value4) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached PollQuestionRecord
	 */
	public PollQuestionRecord() {
		super(com.forumdeitroll.persistence.jooq.tables.PollQuestion.POLL_QUESTION);
	}

	/**
	 * Create a detached, initialised PollQuestionRecord
	 */
	public PollQuestionRecord(java.lang.Integer pollid, java.lang.Integer sequence, java.lang.String text, java.lang.Integer votes) {
		super(com.forumdeitroll.persistence.jooq.tables.PollQuestion.POLL_QUESTION);

		setValue(0, pollid);
		setValue(1, sequence);
		setValue(2, text);
		setValue(3, votes);
	}
}