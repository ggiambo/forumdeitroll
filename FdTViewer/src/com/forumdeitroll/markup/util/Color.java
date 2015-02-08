package com.forumdeitroll.markup.util;

import java.io.IOException;

import com.forumdeitroll.markup.RenderIO;
import com.forumdeitroll.markup.RenderState;

public class Color {

	private static final char[] COLOR = "[color #".toCharArray();
	private static final char[] COLOR_END = "[/color]".toCharArray();

	private static final char[] CLOSE_SQUARE_BRACKET = "]".toCharArray();

	public static boolean color(RenderIO io, RenderState state) throws IOException {
		if (io.startWith(COLOR)) {
			int end = io.indexOf(CLOSE_SQUARE_BRACKET, COLOR.length);
			if (end == -1)
				return false;
			if (end - COLOR.length != 3 && end - COLOR.length != 6)
				return false;
			for (int i = COLOR.length; i < end; i++)
				if (! Chars.isAlphanum(io.buffer[i]))
					return false;
			io.write("<span style='color: #");
			io.copy(COLOR.length, end - COLOR.length);
			io.write("'>");
			io.skip(end + 1);
			state.colors++;
			return true;
		}
		if (io.startWith(COLOR_END)) {
			io.write("</span>");
			io.skip(COLOR_END.length);
			state.colors--;
			return true;
		}
		return false;
	}

	public static void cleanup(RenderIO io, RenderState state) throws IOException {
		while (state.colors > 0) {
			io.write("</span>");
			state.colors--;
		}
	}
}
