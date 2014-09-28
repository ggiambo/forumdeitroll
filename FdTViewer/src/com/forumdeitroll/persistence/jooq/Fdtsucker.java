/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.3.1" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Fdtsucker extends org.jooq.impl.SchemaImpl {

	private static final long serialVersionUID = 1497399193;

	/**
	 * The singleton instance of <code>fdtsucker</code>
	 */
	public static final Fdtsucker FDTSUCKER = new Fdtsucker();

	/**
	 * No further instances allowed
	 */
	private Fdtsucker() {
		super("fdtsucker");
	}

	@Override
	public final java.util.List<org.jooq.Table<?>> getTables() {
		java.util.List result = new java.util.ArrayList();
		result.addAll(getTables0());
		return result;
	}

	private final java.util.List<org.jooq.Table<?>> getTables0() {
		return java.util.Arrays.<org.jooq.Table<?>>asList(
			com.forumdeitroll.persistence.jooq.tables.Ads.ADS,
			com.forumdeitroll.persistence.jooq.tables.Authors.AUTHORS,
			com.forumdeitroll.persistence.jooq.tables.Bookmarks.BOOKMARKS,
			com.forumdeitroll.persistence.jooq.tables.Digest.DIGEST,
			com.forumdeitroll.persistence.jooq.tables.DigestParticipant.DIGEST_PARTICIPANT,
			com.forumdeitroll.persistence.jooq.tables.Likes.LIKES,
			com.forumdeitroll.persistence.jooq.tables.Messages.MESSAGES,
			com.forumdeitroll.persistence.jooq.tables.Notification.NOTIFICATION,
			com.forumdeitroll.persistence.jooq.tables.Poll.POLL,
			com.forumdeitroll.persistence.jooq.tables.PollQuestion.POLL_QUESTION,
			com.forumdeitroll.persistence.jooq.tables.PollUser.POLL_USER,
			com.forumdeitroll.persistence.jooq.tables.Preferences.PREFERENCES,
			com.forumdeitroll.persistence.jooq.tables.PvtContent.PVT_CONTENT,
			com.forumdeitroll.persistence.jooq.tables.PvtRecipient.PVT_RECIPIENT,
			com.forumdeitroll.persistence.jooq.tables.Quotes.QUOTES,
			com.forumdeitroll.persistence.jooq.tables.Sysinfo.SYSINFO,
			com.forumdeitroll.persistence.jooq.tables.Tagnames.TAGNAMES,
			com.forumdeitroll.persistence.jooq.tables.Tags.TAGS,
			com.forumdeitroll.persistence.jooq.tables.TagsBind.TAGS_BIND,
			com.forumdeitroll.persistence.jooq.tables.Threads.THREADS);
	}
}