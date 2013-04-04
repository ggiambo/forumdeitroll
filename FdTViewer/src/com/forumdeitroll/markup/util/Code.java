package com.forumdeitroll.markup.util;

import java.io.IOException;

import com.forumdeitroll.markup.RenderIO;
import com.forumdeitroll.markup.RenderState;

public class Code {
	
	private static final char[] CODE_OPEN = "[code ".toCharArray();
	private static final char[] CODE = "[code]".toCharArray();
	private static final char[] CODE_END = "[/code]".toCharArray();
	
	private static final char[] CLOSE_SQUARE_BRACKET = "]".toCharArray();
	private static final char[] BR = "<BR>".toCharArray();
	
	public static boolean code(RenderIO io, RenderState state) throws IOException {
		if (io.startWith(CODE)) {
			int end = io.indexOf(CODE_END, CODE.length);
			int brpos = io.indexOf(BR, CODE.length);
			if (end != -1 && ((brpos != -1 && end < brpos) || brpos == -1)) {
				// [code]...[/code] su una sola linea
				io.write("<span style='font-family: monospace'>");
				io.copy(CODE.length, end - CODE.length);
				io.write("</span>");
				io.skip(end + CODE_END.length);
				return true;
			}
			// [code]...[/code] su piu' linee
			io.write("<pre class='code'>");
			io.skip(CODE.length);
			state.codeTagOpen = true;
			return true;
		}
		if (io.startWith(CODE_OPEN)) {
			int end = io.indexOf(CLOSE_SQUARE_BRACKET, CODE_OPEN.length);
			if (end == -1 || (end != -1 && end > (CODE_OPEN.length + 12)))
				return false;
			// [code $lang] ... [/code]
			for (int i = CODE_OPEN.length; i < end; i++)
				if (!Chars.isAlphanum(io.buffer[i]))
					return false;
			io.write("<pre class='brush: ");
			io.copy(CODE_OPEN.length, end - CODE_OPEN.length);
			io.write("; class-name: code'>");
			io.skip(end + 1);
			state.codeTagOpen = true;
			return true;
		}
		return false;
	}
	
	public static boolean codeEnd(RenderIO io, RenderState state) throws IOException {
		if (io.startWith(CODE_END)) {
			io.write("</pre>");
			io.skip(CODE_END.length);
			state.codeTagOpen = false;
			return true;
		}
		return false;
	}
	
	public static void cleanup(RenderIO io, RenderState state) throws IOException {
		if (state.codeTagOpen)
			io.write("</pre>");
		state.codeTagOpen = false;
	}
	
	public static boolean brToNewline(RenderIO io, RenderState state) throws IOException {
		if (!state.codeTagOpen)
			return false;
		if (!io.startWith(BR))
			return false;
		io.write("\n");
		io.skip(BR.length);
		return true;
	}
}
