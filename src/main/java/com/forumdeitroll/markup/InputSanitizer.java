package com.forumdeitroll.markup;


import org.apache.commons.lang3.StringUtils;

public class InputSanitizer {
	
	public static String sanitizeText(String text) {
		// replace dei caratteri HTML
		text = StringUtils.defaultString(text);
		text = text.replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\n", "<BR>");

		// restore <i>, <b>, <u> e <s>
		for (String t : new String[] {"i", "b", "u", "s"}) {
			text = text.replaceAll("(?i)&lt;" + t + "&gt;", "<" + t + ">");
			text = text.replaceAll("(?i)&lt;/" + t + "&gt;", "</" + t + ">");
		}
		
		return text;
	}
	
	public static String sanitizeSubject(String subject) {
		return subject.replaceAll(">", "&gt;").replaceAll("<", "&lt;");
	}
	public static String sanitizeForum(String forum) {
		return forum.replaceAll(">", "&gt;").replaceAll("<", "&lt;");
	}
}
