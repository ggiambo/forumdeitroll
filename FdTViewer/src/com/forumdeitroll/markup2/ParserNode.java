package com.forumdeitroll.markup2;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.StringWriter;

import com.forumdeitroll.markup.Emoticons;
import com.forumdeitroll.markup.util.Links;
import com.forumdeitroll.markup.util.EntityEscaper;
import com.forumdeitroll.markup.util.Chars;

interface ParserNode {
	public void render(final Writer w, final Parser.Status status) throws Exception;
	public void printDebug(final PrintWriter w, final int depth);
	public boolean isQuoteGroup();
	public boolean isBR();


	public static class QuoteGroup implements ParserNode {
		public String scrittoDa;
		public boolean oldStyleScrittoDa;
		public CharSequence scrittoDaQuoteRun;
		public int level;
		public final List<ParserNode> childs = new ArrayList<ParserNode>();

		public void setScrittoDa(final Token t) {
			scrittoDa = t.text.toString();
			oldStyleScrittoDa = t.oldStyle;
			scrittoDaQuoteRun = t.scrittoDaQuoteRun;
		}

		void openSpan(final Writer w) throws Exception {
			w.write("<span class='quoteLvl" + ((level-1) % 4 + 1) + "'>");
		}

		boolean collapseQuotesHere(final Parser.Status status) {
			return status.rops.collapseQuotes && (level == 1);
		}

		/*
		Il codice di questa funzione e` piu` complicato di quello che potrebbe essere perche' deve copiare un paio di comportamenti "peculiari" della precedente implementazione del parser.
		Vedere la descrizione dei flag firstChunkPrinted e floatedBr per i dettagli.
		*/
		public void render(final Writer w, final Parser.Status status) throws Exception {
			boolean insideSpan = false;

			if (level > 0) {
				if (scrittoDa != null) {
					if ((level != 1) || !status.firstChunkPrinted) {
						openSpan(w);
						insideSpan = true;
					}
					if (scrittoDaQuoteRun != null) {
						w.write(scrittoDaQuoteRun.toString());
					}
					if (oldStyleScrittoDa) {
						w.write("- ");
					}
					if (!scrittoDa.equals("")) {
						w.write("Scritto da: <a href=\"User?action=getUserInfo&nick=" + scrittoDa + "\">" + scrittoDa + "</a>");
					} else {
						w.write("Scritto da: ");
					}
					status.floatedBr = true;
				}
			}

			if (collapseQuotesHere(status)) {
				if (scrittoDa != null) {
					w.write("<BR></span>");
					status.floatedBr = false;
				}
				w.write("<div class='quote-container'><div>");
				openSpan(w);
				insideSpan = true;
			}

			for (final ParserNode child: childs) {
				if (child.isQuoteGroup()) {
					if (insideSpan && level > 0) {
						w.write("</span>");
						insideSpan = false;
					}
					if (status.floatedBr) {
						status.floatedBr = false;
						w.write("<BR>");
					}
					child.render(w, status);
				} else {
					status.firstChunkPrinted = true;
					if (status.floatedBr) {
						status.floatedBr = false;
						w.write("<BR>");
					}
					if (!insideSpan && level > 0) {
						openSpan(w);
						insideSpan = true;
					}
					child.render(w, status);
				}
			}

			if (insideSpan && level > 0) {
				w.write("</span>");
			}
			if (collapseQuotesHere(status)) {
				w.write("</div></div>");
				status.floatedBr = false;
			}
			if (status.floatedBr) {
				w.write("<BR>");
				status.floatedBr = false;
			}
		}

		public void printDebug(final PrintWriter w, final int depth) {
			Rendering.indent(w, depth);
			w.println("QUOTE GROUP <" + scrittoDa + "> " + level);
			for (final ParserNode node: childs) {
				node.printDebug(w, depth+1);
			}
		}

		public boolean isQuoteGroup() {
			return true;
		}

		public boolean isBR() {
			return false;
		}
	}

	public static class Chunk implements ParserNode {
		public final List<ParserNode> childs = new ArrayList<ParserNode>();

		public void render3(final Writer w, final Parser.Status status, final boolean floatBr) throws Exception {
			for (int i = 0; i < childs.size(); ++i) {
				final ParserNode child = childs.get(i);
				if ((i == childs.size()-1) && child.isBR() && floatBr) {
					status.floatedBr = true;
					break;
				}

				child.render(w, status);
			}
		}

		public void render(final Writer w, final Parser.Status status) throws Exception {
			render3(w, status, true);
		}

		public void printDebug(final PrintWriter w, final int depth) {
			Rendering.indent(w, depth);
			w.println("CHUNK");
			for (final ParserNode node: childs) {
				node.printDebug(w, depth+1);
			}
		}

		public boolean isQuoteGroup() {
			return false;
		}

		public boolean isBR() {
			return false;
		}
	}

	public static class TagDelimited implements ParserNode {
		public Token openTag;
		public String ot;
		public String arg;
		public Chunk body;
		public boolean isClosed;

		static Map<String, Rendering.OfTag> renderers = new HashMap<String, Rendering.OfTag>();
		static {
			renderers.put("img", Rendering.imgRendering);
			renderers.put("url", Rendering.urlRendering);
			renderers.put("code", Rendering.codeRendering);
			renderers.put("yt", Rendering.ytRendering);
			renderers.put("color", Rendering.colorRendering);
			renderers.put("spoiler", Rendering.spoilerRendering);
			renderers.put("b", Rendering.htmlRendering);
			renderers.put("i", Rendering.htmlRendering);
			renderers.put("u", Rendering.htmlRendering);
			renderers.put("s", Rendering.htmlRendering);
		}

		public void setOpenTag(final Token openTag) {
			this.openTag = openTag;
			ot = openTag.text.toString();
			ot = ot.substring(1, ot.length()-1);
			int e = ot.indexOf("=");
			if (e > 0) {
				arg = ot.substring(e+1);
				ot = ot.substring(0, e);
			} else {
				e = ot.indexOf(" ");
				if (e > 0) {
					arg = ot.substring(e+1);
					ot = ot.substring(0, e);
				}
			}
			ot = ot.toLowerCase();
		}

		void writeBadTag(final Writer w, final Parser.Status status) throws Exception {
			w.write("[" + ot);
			if (arg != null) {
				if (ot.equals("url")) {
					w.write("=");
				} else {
					w.write(" ");
				}
				w.write(arg);
			}
			w.write("]");
			body.render(w, status);
			if (isClosed) {
				w.write("[/" + ot + "]");
			}
		}

		ParserNode.TokenNode bodyIsOne(final Token.Type tokenType) {
			if (body.childs.size() != 1) {
				return null;
			}
			final ParserNode b = body.childs.get(0);
			if (!(b instanceof TokenNode)) {
				return null;
			}
			final TokenNode tn = (TokenNode)b;
			if (tn.t.tokenType != tokenType) {
				return null;
			}
			return tn;
		}

		public void render(final Writer w, final Parser.Status status) throws Exception {
			final Rendering.OfTag rot = renderers.get(ot);
			if (rot == null) {
				w.write(" **UNIMPLEMENTED** ");
				return;
			}
			rot.render(w, status, this);
		}

		public void printDebug(final PrintWriter w, final int depth) {
			Rendering.indent(w, depth);
			w.println("TAG DELIMITED <" + ot + ">");
			for (final ParserNode node: body.childs) {
				node.printDebug(w, depth+1);
			}
		}

		public boolean isQuoteGroup() {
			return false;
		}

		public boolean isBR() {
			return false;
		}
	}

	public static class TokenNode implements ParserNode {
		public final Token t;
		public TokenNode(final Token t) {
			this.t = t;
		}

		public void render(final Writer w, final Parser.Status status) throws Exception {
			switch (t.tokenType) {
			case TEXT:
				w.write(t.text.buf, t.text.start, t.text.length());
				break;

			case NL:
				w.write("<BR>");
				break;

			case QUOTE_RUN:
				w.write(t.text.buf, t.text.start, t.text.length());
				break;

			case EMOTICON:
				if (status.emotiCount < Emoticons.MAX_EMOTICONS) {
					++status.emotiCount;
					w.write(t.repl);
				} else {
					w.write(t.text.buf, t.text.start, t.text.length());
				}
				break;

			case LINK:
				if (status.autolinkDisableCount > 0) {
					w.write(t.text.buf, t.text.start, t.text.length());
					return;
				}
				if (!Links.isLink(t.text.buf, t.text.start, t.text.length())) {
					w.write(t.text.buf, t.text.start, t.text.length());
					return;
				}
				try {
					if (!Rendering.doYoutubeLink(w, status, t.text)) {
						Rendering.writeLinkTag(w, t.text);
					}
				} catch (Exception e) {
					w.write(t.text.buf, t.text.start, t.text.length());
				}
				break;

			case PUTTANATA_MICIDIALE:
				for (int i = 0; i < t.puttanataMicidialeDepth; ++i) {
					w.write("<sup>");
				}
				w.write(t.text.buf, t.text.start, t.text.length());
				for (int i = 0; i < t.puttanataMicidialeDepth; ++i) {
					w.write("</sup>");
				}
				break;

			case CLOSE_TAG:
				EntityEscaper.writeEscaped(w, t.text.buf, t.text.start, t.text.length());
				break;

			default:
				w.write(" **UNIMPLEMENTED** ");
			}
		}

		public void printDebug(final PrintWriter w, final int depth) {
			Rendering.indent(w, depth);
			w.println(t.toString());
		}

		public boolean isQuoteGroup() {
			return false;
		}

		public boolean isBR() {
			return t.tokenType == Token.Type.NL;
		}
	}
}
