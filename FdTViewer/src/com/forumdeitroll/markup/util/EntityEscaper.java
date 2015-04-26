package com.forumdeitroll.markup.util;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang3.text.translate.EntityArrays;

public class EntityEscaper {
	private static char[][] entityMapping = entityMapping();
	private static char[][] entityMapping() {
		int size = EntityArrays.BASIC_ESCAPE().length + EntityArrays.ISO8859_1_ESCAPE().length + EntityArrays.HTML40_EXTENDED_ESCAPE().length + EntityArrays.APOS_ESCAPE().length;
		char[][] mapping = new char[size][];
		int index = 0;
		for (String[] pair : EntityArrays.BASIC_ESCAPE()) {
			mapping[index++] = (pair[0] + pair[1]).toCharArray();
		}
		for (String[] pair : EntityArrays.ISO8859_1_ESCAPE()) {
			mapping[index++] = (pair[0] + pair[1]).toCharArray();
		}
		for (String[] pair : EntityArrays.HTML40_EXTENDED_ESCAPE()) {
			mapping[index++] = (pair[0] + pair[1]).toCharArray();
		}
		for (String[] pair : EntityArrays.APOS_ESCAPE()) {
			mapping[index++] = (pair[0] + pair[1]).toCharArray();
		}
		return mapping;
	}
	
	public static void writeEscaped(Writer out, char[] buffer, int offset, int length) throws IOException {
		for (int i = 0; i < length; i++) {
			char c = buffer[i + offset];
			if (!Chars.isAlphanum(c)) {
				for (char[] pair : entityMapping) {
					if (c == pair[0]) {
						out.write(pair, 1, pair.length - 1);
						c = 0;
						break;
					}
				}
			}
			if (c == 0)
				continue;
			out.write(c);
		}
	}

	public static String escape(String s, int offset, int length) {
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char c = s.charAt(i + offset);
			if (!Chars.isAlphanum(c)) {
				for (char[] pair : entityMapping) {
					if (c == pair[0]) {
						out.append(pair, 1, pair.length -1);
						c = 0;
						break;
					}
				}
			}
			if (c == 0) {
				continue;
			}
			out.append(c);
		}
		return out.toString();
	}

	public static String escape(String s) {
		return escape(s, 0, s.length());
	}
}
