package com.forumdeitroll.markup.util;

import java.io.IOException;

import com.forumdeitroll.markup.RenderIO;
import com.forumdeitroll.markup.RenderState;

public class Spoiler {
	
	private static final char[] SPOILER = "[spoiler]".toCharArray();
	private static final char[] SPOILER_END = "[/spoiler]".toCharArray();
	
	public static boolean spoiler(RenderIO io, RenderState state) throws IOException {
		if (io.startWith(SPOILER)) {
			io.write("<div class='spoiler'><span class='spoilerWarning'>SPOILER!!!</span> ");
			io.skip(SPOILER.length);
			state.spoilers++;
			return true;
		}
		if (io.startWith(SPOILER_END)) {
			io.write("</div>");
			io.skip(SPOILER_END.length);
			state.spoilers--;
			return true;
		}
		return false;
	}
	
	public static void cleanup(RenderIO io, RenderState state) throws IOException {
		while (state.spoilers > 0) {
			io.write("</div>");
			state.spoilers--;
		}
	}
}
