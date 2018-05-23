package com.forumdeitroll.markup.util;

public class Wildcard {
	public static String toRegex(String wildcard) {
		StringBuilder regex = new StringBuilder();
		for (char c : wildcard.toCharArray()) {
			switch (c) {
			case '*':
				regex.append(".*");
				break;
			case '?':
				regex.append(".");
				break;
			default:
				if ("\\+()^$.{}[]|".indexOf(c) != -1) {
					regex.append('\\');
				}
				regex.append(c);
			}
		}
		return regex.toString();
	}
}
