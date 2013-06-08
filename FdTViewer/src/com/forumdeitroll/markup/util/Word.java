package com.forumdeitroll.markup.util;

import java.io.IOException;

import com.forumdeitroll.markup.Emoticon;
import com.forumdeitroll.markup.Emoticons;
import com.forumdeitroll.markup.RenderIO;
import com.forumdeitroll.markup.RenderOptions;
import com.forumdeitroll.markup.RenderState;

public class Word {

	private static final char[][] STOPWORDS = new char[][] {
			"<BR>".toCharArray(),
			" ".toCharArray(),
			"\t".toCharArray(),
			"[".toCharArray(),
			"<".toCharArray() };

	public static boolean word(RenderIO io, RenderState state, RenderOptions opts) throws IOException {

		if (io.startWith(STOPWORDS[0])) // <BR>
			return false;

		int end = io.count;

		for (int i = 0; i < STOPWORDS.length; i++) {
			int p = io.indexOf(STOPWORDS[i], 0);
			if (p != -1 && p < end)
				end = p;
		}

		if (end == 0) {
			//word start with stopword or empty word
			if (io.count == 0)
				return false;
			
			io.copy(1);
			io.skip(1);
			return true;
		}

		if (!emoticons(io, state, end)) {
			if (!autolink(io, state, opts, end)) {
				if (!exponential(io, state, end)) {
					io.copy(end);
				}
			}
		}

		io.skip(end);
		return true;
	}

	private static final char[] CARET = "^".toCharArray();

	private static boolean exponential(RenderIO io, RenderState state, int end) throws IOException {
		int pcaret = io.indexOf(CARET, 0);
		if (pcaret == -1 || end == 1)
			return false;
		if (pcaret == 0) {
			int i;
			for (i = 1; i < end - 1; i++) {
				if (io.buffer[i] != '_') {
					break;
				}
			}
			if (i == end - 1) {
				if (io.buffer[i] == '^') {
					return false;
				}
			}
		}

		if (pcaret == -1 || pcaret >= end)
			return false;
		
		int ncaret = 0;
		int fromIndex = 0;
		
		do {
			
			if (pcaret > fromIndex)
				io.copy(fromIndex, pcaret - fromIndex);
			
			io.write("<sup>");
			
			fromIndex = (pcaret + 1);
			ncaret++;
			
			pcaret = io.indexOf(CARET, fromIndex);
			
			if (pcaret == -1 || pcaret >= end)
				break;
			
		} while (true);

		if (fromIndex < end)
			io.copy(fromIndex, end - fromIndex);
		
		while (ncaret > 0) {
			io.write("</sup>");
			ncaret--;
		}
		
		io.skip(end);
		return true;
	}
	
	private static char[] FDT_IT_THREAD_URL_INIT = "http://www.forumdeitroll.it/ms.aspx?m_id=".toCharArray();
	
	private static char[] FDT_IT_MESSAGE_URL_INIT = "http://www.forumdeitroll.it/m.aspx?m_id=".toCharArray();

	private static boolean autolink(RenderIO io, RenderState state, RenderOptions opts, int end) throws IOException {
		if (Links.isLink(io.buffer, 0, end)) {
			int[] boundaries = opts.renderYoutube
				? YouTube.extractYoucode(io.buffer, 0, end)
				: null;
			if (boundaries != null && state.embedCount < YouTube.MAX_EMBED) {
				if (opts.embedYoutube) {
					YouTube.writeYtEmbed(io.out, io.buffer, boundaries[0], boundaries[1] - boundaries[0]);
				} else {
					YouTube.writeYTImage(io.out, io.buffer, boundaries[0], boundaries[1] - boundaries[0]);
				}
				state.embedCount++;
				return true;
			}
			// nel vecchio forum non esisteva il tag [url], i link interni di cui fare rewrite sono solo autolinks
			else if (io.startWith(FDT_IT_THREAD_URL_INIT)) {
				io.write("<a rel='nofollow noreferrer' target='_blank' href=\"Threads?action=getByThread&threadId=");
				EntityEscaper.writeEscaped(io.out, io.buffer, FDT_IT_THREAD_URL_INIT.length, end - FDT_IT_THREAD_URL_INIT.length);
				io.write("\">");
				EntityEscaper.writeEscaped(io.out, io.buffer, 0, end);
				io.write("</a>");
				return true;
			}
			else if (io.startWith(FDT_IT_MESSAGE_URL_INIT)) {
				io.write("<a rel='nofollow noreferrer' target='_blank' href=\"Threads?action=getByMessage&msgId=");
				EntityEscaper.writeEscaped(io.out, io.buffer, FDT_IT_MESSAGE_URL_INIT.length, end - FDT_IT_MESSAGE_URL_INIT.length);
				io.write("\">");
				EntityEscaper.writeEscaped(io.out, io.buffer, 0, end);
				io.write("</a>");
				return true;
			}
			else {
				Links.writeLinkTag(io, 0, end, 0, end);
				return true;
			}
			
		}
		return false;
	}

	private static char[][][] emoseqs = initEmoticons();

	private static char[][][] initEmoticons() {
		emoseqs = new char[Emoticons.getInstance().tutte.size() * 2][][];
		int index = 0;
		for (Emoticon emo : Emoticons.getInstance().tutte) {
			emoseqs[index++] = new char[][] { emo.sequence.toCharArray(), emo.htmlReplacement.toCharArray() };
			emoseqs[index++] = new char[][] { emo.sequenceUpcase.toCharArray(), emo.htmlReplacement.toCharArray() };
		}
		return emoseqs;
	}

	private static boolean emoticons(RenderIO io, RenderState state, int end)
			throws IOException {
		int initialCount = state.emotiCount;

		int p = 0;

		while (p < end) {

			if (state.emotiCount >= Emoticons.MAX_EMOTICONS)
				break;

			boolean found = false;
			for (char[][] pair : emoseqs) {

				if (state.emotiCount >= Emoticons.MAX_EMOTICONS)
					break;

				char[] seq = pair[0];
				char[] rep = pair[1];
				boolean initSpace = seq[0] == ' ';
				int offset = p == 0 && initSpace ? 1 : 0;
				int length = seq.length - (p == 0 && initSpace ? 1 : 0);

				if (Chars.indexOf(io.buffer, 0, end, seq, offset, length, p, false, true) == p) {

					if (initialCount == state.emotiCount && p != 0)
						io.copy(p);

					io.write(rep);
					p += length - 1;
					state.emotiCount++;
					found = true;
					break;
				}
			}

			if (!found && initialCount != state.emotiCount)
				io.write(io.buffer, p, 1);

			p++;
		}

		return initialCount != state.emotiCount;
	}
}
