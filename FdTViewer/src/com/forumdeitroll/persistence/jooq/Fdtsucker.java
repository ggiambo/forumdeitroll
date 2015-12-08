/**
 * This class is generated by jOOQ
 */
package com.forumdeitroll.persistence.jooq;


import com.forumdeitroll.persistence.jooq.tables.Ads;
import com.forumdeitroll.persistence.jooq.tables.Authors;
import com.forumdeitroll.persistence.jooq.tables.Bookmarks;
import com.forumdeitroll.persistence.jooq.tables.Digest;
import com.forumdeitroll.persistence.jooq.tables.DigestParticipant;
import com.forumdeitroll.persistence.jooq.tables.Likes;
import com.forumdeitroll.persistence.jooq.tables.Logins;
import com.forumdeitroll.persistence.jooq.tables.Messages;
import com.forumdeitroll.persistence.jooq.tables.Notification;
import com.forumdeitroll.persistence.jooq.tables.Poll;
import com.forumdeitroll.persistence.jooq.tables.PollQuestion;
import com.forumdeitroll.persistence.jooq.tables.PollUser;
import com.forumdeitroll.persistence.jooq.tables.Preferences;
import com.forumdeitroll.persistence.jooq.tables.PvtContent;
import com.forumdeitroll.persistence.jooq.tables.PvtRecipient;
import com.forumdeitroll.persistence.jooq.tables.Quotes;
import com.forumdeitroll.persistence.jooq.tables.Sysinfo;
import com.forumdeitroll.persistence.jooq.tables.Tagnames;
import com.forumdeitroll.persistence.jooq.tables.Tags;
import com.forumdeitroll.persistence.jooq.tables.TagsBind;
import com.forumdeitroll.persistence.jooq.tables.Threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


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
public class Fdtsucker extends SchemaImpl {

	private static final long serialVersionUID = 829853017;

	/**
	 * The reference instance of <code>fdtsucker</code>
	 */
	public static final Fdtsucker FDTSUCKER = new Fdtsucker();

	/**
	 * No further instances allowed
	 */
	private Fdtsucker() {
		super("fdtsucker");
	}

	@Override
	public final List<Table<?>> getTables() {
		List result = new ArrayList();
		result.addAll(getTables0());
		return result;
	}

	private final List<Table<?>> getTables0() {
		return Arrays.<Table<?>>asList(
			Ads.ADS,
			Authors.AUTHORS,
			Bookmarks.BOOKMARKS,
			Digest.DIGEST,
			DigestParticipant.DIGEST_PARTICIPANT,
			Likes.LIKES,
			Logins.LOGINS,
			Messages.MESSAGES,
			Notification.NOTIFICATION,
			Poll.POLL,
			PollQuestion.POLL_QUESTION,
			PollUser.POLL_USER,
			Preferences.PREFERENCES,
			PvtContent.PVT_CONTENT,
			PvtRecipient.PVT_RECIPIENT,
			Quotes.QUOTES,
			Sysinfo.SYSINFO,
			Tagnames.TAGNAMES,
			Tags.TAGS,
			TagsBind.TAGS_BIND,
			Threads.THREADS);
	}
}
