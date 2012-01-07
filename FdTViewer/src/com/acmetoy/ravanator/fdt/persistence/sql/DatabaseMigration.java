package com.acmetoy.ravanator.fdt.persistence.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.acmetoy.ravanator.fdt.persistence.PersistenceFactory;
import com.acmetoy.ravanator.fdt.servlets.Messages;

public abstract class DatabaseMigration {
	
	private static final Logger LOG = Logger.getLogger(DatabaseMigration.class);
	
	private static final Pattern PATTERN_URL = Pattern.compile("<a.+?href=\"(.+?)\".+?</a>");
	private static final Pattern PATTERN_EMO = Pattern.compile("<IMG SRC=\"images/emo/(.+?)\\.gif\".*?>");
	private static final Pattern PATTERN_YT = Pattern.compile("<div class='ytvideo'>.*?'http://www.youtube.com/v/(.+?)'.*?</div>");
	//private static final Pattern PATTERN_IMG = Pattern.compile("<img.*?src=\"(.+?)\".*?>");
	private static final Pattern PATTERN_IMG = Pattern.compile("<a.+?><img.*?src=\"(.+?)\".*?></a>");
	private static final Pattern PATTERN_BLOCKQUOTE = Pattern.compile("<blockquote><pre width='34px' style='border: 1px dashed gray'><b>(.+?)</b></pre></blockquote>");

	private static final Map<String, String> EMO_MAP = Messages.getEmoMap();
	
	public static void main(String[] args) {

		int chunkSize = 1000;
		
		DecimalFormat percentFormat = new DecimalFormat("000");
		
		GenericSQLPersistence pers = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			pers = (GenericSQLPersistence) PersistenceFactory.getInstance();
			conn = pers.getConnection();
			
			rs = conn.prepareStatement("select count(id) from messages").executeQuery();
			rs.next();
			double messages = rs.getDouble(1);
			LOG.info("Start database migration: " + messages + " messages");
			
			int i = 0;
			List<MessageDTO> msgs = pers.getMessagesByDate(chunkSize, i);
			stmt = conn.prepareStatement("update messages set text = ? where id = ?");
			String newText;
			while (msgs != null && msgs.size() > 0) {
				for (MessageDTO msg : msgs) {
					try {
						newText = updateText(msg.getText());
					} catch (Exception e) {
						LOG.fatal(e);
						return;
					}
					if (!newText.equals(msg.getText())) {
						stmt.setString(1, newText);
						stmt.setLong(2, msg.getId());
						try {
							stmt.execute();
						} catch (Exception e) {
							LOG.error(msg, e);
						}
					}
				}
				i++;
				msgs = pers.getMessagesByDate(chunkSize, i);
				double percent = (i*chunkSize) / messages;
				LOG.info("Migrating database: " + percentFormat.format(percent*100) + "% done");
			}			
			
		} catch (Exception e) {
			LOG.fatal(e);
			return;
		} finally {
			if (pers != null) {
				pers.close(rs, stmt, conn);
			}
		}
		LOG.info("End database migration");
	}
	
	private static String updateText(String text) {
		Matcher m;
		
		// img
		m = PATTERN_IMG.matcher(text);
		while (m.find()) {
			String replace = "[img]" + m.group(1) + "[/img]";
			text = m.replaceFirst(Matcher.quoteReplacement(replace));
			m = PATTERN_IMG.matcher(text);
		}
		
		// links
		m = PATTERN_URL.matcher(text);
		while (m.find()) {
			String replace = m.group(1);
			text = m.replaceFirst(Matcher.quoteReplacement(replace));
			m = PATTERN_URL.matcher(text);
		}
		
		// faccine
		m = PATTERN_EMO.matcher(text);
		while (m.find()) {
			String replace = EMO_MAP.get(m.group(1));
			text = m.replaceFirst(Matcher.quoteReplacement(replace));
			m = PATTERN_EMO.matcher(text);
		}
		
		// youtube
		m = PATTERN_YT.matcher(text);
		while (m.find()) {
			String replace = "[yt]" + m.group(1) + "[/yt]";
			text = m.replaceFirst(Matcher.quoteReplacement(replace));
			m = PATTERN_YT.matcher(text);
		}
		
		// quote
		m = PATTERN_BLOCKQUOTE.matcher(text);
		while (m.find()) {
			String replace = "[code]" + m.group(1).replaceAll("&nbsp;", " ") + "[/code]";
			text = m.replaceFirst(Matcher.quoteReplacement(replace));
			m = PATTERN_BLOCKQUOTE.matcher(text);
		}
		
		return text;
	}

}
