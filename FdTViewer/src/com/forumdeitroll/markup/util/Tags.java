package com.forumdeitroll.markup.util;

import java.io.IOException;

import com.forumdeitroll.markup.RenderIO;
import com.forumdeitroll.markup.RenderState;

public class Tags {
	
	private static final char[] TAG_B = "<b>".toCharArray();
	private static final char[] TAG_I = "<i>".toCharArray();
	private static final char[] TAG_S = "<s>".toCharArray();
	private static final char[] TAG_U = "<u>".toCharArray();
	private static final char[] TAG_B_END = "</b>".toCharArray();
	private static final char[] TAG_I_END = "</i>".toCharArray();
	private static final char[] TAG_S_END = "</s>".toCharArray();
	private static final char[] TAG_U_END = "</u>".toCharArray();
	
	private static final char[][] OPENING_TAGS = new char[][] {TAG_B, TAG_I, TAG_S, TAG_U};
	private static final char[][] CLOSING_TAGS = new char[][] {TAG_B_END, TAG_I_END, TAG_S_END, TAG_U_END};
	
	public static boolean tags(RenderIO io, RenderState state) throws IOException {
		for (int i = 0; i < OPENING_TAGS.length; i++) {
			if (io.startWithICase(OPENING_TAGS[i])) {
				io.copy(OPENING_TAGS[i].length);
				io.skip(OPENING_TAGS[i].length);
				state.tags[i]++;
				return true;
			}
		}
		for (int i = 0; i < CLOSING_TAGS.length; i++) {
			if (io.startWithICase(CLOSING_TAGS[i])) {
				io.copy(CLOSING_TAGS[i].length);
				io.skip(CLOSING_TAGS[i].length);
				state.tags[i]--;
				return true;
			}
		}
		return false;
	}
	
	public static void cleanup(RenderIO io, RenderState state) throws IOException {
		for (int i = 0; i < CLOSING_TAGS.length; i++) {
			while (state.tags[i] > 0) {
				io.write(CLOSING_TAGS[i]);
				state.tags[i]--;
			}
		}
	}
}
