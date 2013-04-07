package com.forumdeitroll.markup.util;

import java.io.IOException;

import com.forumdeitroll.markup.RenderIO;
import com.forumdeitroll.markup.RenderOptions;
import com.forumdeitroll.markup.RenderState;

public class Line {
	
	private static final char[] BR = "<BR>".toCharArray();
	private static final char[] QUOTE = "&gt;".toCharArray();
	private static final char[] SP_QUOTE = " &gt;".toCharArray();
	
	private static final char[] SCRITTO_DA_OLD = "- Scritto da: ".toCharArray();
	private static final char[] SCRITTO_DA_NEW = "Scritto da: ".toCharArray();
	
	public static void firstLine(RenderIO io, RenderState state, RenderOptions opts) throws IOException {
		
		int quoteLvl = 0;
		char[] q = QUOTE;
		int pq = 0;
		while (io.indexOf(q, pq) == pq) {
			pq += q.length;
			q = SP_QUOTE;
			quoteLvl++;
		}
		int scrittoda = (scrittoda=io.indexOf(SCRITTO_DA_OLD, pq)) != -1
				? scrittoda
				: io.indexOf(SCRITTO_DA_NEW, pq);
		
		if (scrittoda != -1) {
			if (quoteLvl == 0 && scrittoda == 0) {
				quoteLvl++;
			} else {
				int prefixlen = BR.length + quoteLvl * (QUOTE.length + 1);
				if (quoteLvl > 0 && (prefixlen == scrittoda || (prefixlen - 1) == scrittoda)) {
					quoteLvl++;
				}
			}
		}
		
		if (opts.collapseQuotes && quoteLvl > 0 && scrittoda == -1) {
			io.write("<div class='quote-container'><div>");
			state.multiLineQuoteStarted = true;
		}
		if (quoteLvl > 0) {
			io.write(String.format("<span class='quoteLvl%d'>", (quoteLvl % 4 == 0 ? 4 : quoteLvl % 4)));
		}
		
		if (scrittoda == -1) {
			io.copy(pq);
			io.skip(pq);
		} else {
			int pnick = scrittoda + SCRITTO_DA_NEW.length;
			if (io.buffer[scrittoda] == '-') {
				pnick += 2;
			}
			int endnick = (endnick = io.indexOf(BR, pnick)) == -1 ? io.count : endnick;
			if (pnick == endnick) {
				io.copy(pq);
				io.skip(pq);
			} else {
				io.copy(pnick);
				io.write("<a href=\"User?action=getUserInfo&nick=");
				EntityEscaper.writeEscaped(io.out, io.buffer, pnick, endnick - pnick);
				io.write("\">");
				EntityEscaper.writeEscaped(io.out, io.buffer, pnick, endnick - pnick);
				io.write("</a>");
				io.skip(endnick);	
			}
		}
		
		state.firstLine = false;
		state.quoteLevel = quoteLvl;
	}
	
	public static boolean line(RenderIO io, RenderState state, RenderOptions opts) throws IOException {
		if (!io.startWith(BR))
			return false;
		
		int quoteLvl = 0;
		char[] q = QUOTE;
		int pq = BR.length;
		while (io.indexOf(q, pq) == pq) {
			pq += q.length;
			q = SP_QUOTE;
			quoteLvl++;
		}
		int scrittoda = (scrittoda=io.indexOf(SCRITTO_DA_OLD, pq)) != -1
				? scrittoda
				: io.indexOf(SCRITTO_DA_NEW, pq);
		
		if (scrittoda != -1) {
			if (quoteLvl == 0 && scrittoda == 0) {
				quoteLvl++;
			} else {
				int prefixlen = BR.length + quoteLvl * (QUOTE.length + 1);
				if (quoteLvl > 0 && (prefixlen == scrittoda || (prefixlen - 1) == scrittoda)) {
					quoteLvl++;
				}
			}
		}
		
		if (state.quoteLevel > 0) {
			io.write("</span>");
		}
		boolean br = true;
		if (opts.collapseQuotes) {
			if (state.multiLineQuoteStarted && quoteLvl == 0) {
				io.write("</div></div>");
				state.multiLineQuoteStarted = false;
				br = false;
			}
		}
		
		if (br)
			io.write(BR);
		
		if (opts.collapseQuotes) {
			if (!state.multiLineQuoteStarted && quoteLvl != 0 && scrittoda == -1) {
				io.write("<div class='quote-container'><div>");
				state.multiLineQuoteStarted = true;
			}
		}
		if (quoteLvl > 0) {
			io.write(String.format("<span class='quoteLvl%d'>", (quoteLvl % 4 == 0 ? 4 : quoteLvl % 4)));
		}
		
		state.quoteLevel = quoteLvl;
		
		if (scrittoda == -1) {
			io.copy(BR.length, pq - BR.length);
			io.skip(pq);
		} else {
			int pnick = scrittoda + SCRITTO_DA_NEW.length;
			if (io.buffer[scrittoda] == '-') {
				pnick += 2;
			}
			int endnick = (endnick = io.indexOf(BR, pnick)) == -1 ? io.count : endnick;
			if (pnick == endnick) {
				io.copy(BR.length, pq - BR.length);
				io.skip(pq);
			} else {
				io.copy(BR.length, pnick - BR.length);
				io.write("<a href=\"User?action=getUserInfo&nick=");
				EntityEscaper.writeEscaped(io.out, io.buffer, pnick, endnick - pnick);
				io.write("\">");
				EntityEscaper.writeEscaped(io.out, io.buffer, pnick, endnick - pnick);
				io.write("</a>");
				io.skip(endnick);	
			}
		}
		
		
		return true;
	}
	
	public static void cleanup(RenderIO io, RenderState state, RenderOptions opts) throws IOException {
		if (state.quoteLevel > 0) {
			io.write("</span>");
		}
		if (opts.collapseQuotes) {
			if (state.multiLineQuoteStarted) {
				io.write("</div></div>");
				state.multiLineQuoteStarted = false;
			}
		}
	}
}
