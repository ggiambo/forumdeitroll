package com.forumdeitroll.markup2;

import com.forumdeitroll.markup.RenderOptions;

class Parser {
	static final boolean debug = false;
	static final boolean shitCodeComp = true;

	static class Status {
		public boolean shitCodeComp;
		public RenderOptions rops;
		public int immyCount = 0;
		public int embedCount = 0;
		public int emotiCount = 0;
		/*
		Questo flag esiste perche' il vecchio parser stampa il tag <BR> dell'ultima riga di un gruppo di righe allo stesso livello di indentazione fuori dallo <span> che colora il gruppo.
		*/
		public boolean floatedBr = false;

		/*
		Il vecchio parser ha un bug per cui lo "Scritto da:" del primo livello di quote viene stampato solo e soltanto se e` la prima cosa nel file. Questo flag aiuta a replicare il bug.
		*/
		public boolean firstChunkPrinted = false;

		public int autolinkDisableCount;

		public Status(final RenderOptions rops) {
			this.shitCodeComp = shitCodeComp;
			this.rops = rops;
			this.autolinkDisableCount = 0;
		}
	}

	/*
	Un messaggio e` esattamente la stessa cosa di un QuoteGroup.
	Il tokenizer genera sempre all'inizio dell'output un token QUTOE_GROUP_BEGIN(level=0) e alla fine un QUOTE_GROUP_END(level=0)
	<Message> ::= <QuoteGroup>
	*/
	public static ParserNode.QuoteGroup parseMessage(final RTokenizer tokv) throws ParseException {
		final ParserNode.QuoteGroup qg = parseQuoteGroup(tokv);
		final Token t = tokv.peek(0);
		if (t.tokenType != Token.Type.END) {
			throw new ParseException("Unexpected token: " + t);
		}
		return qg;
	}

	/*
	Un QuoteGroup e` un token SCRITTO_DA, seguito da un QUOTE_GROUP_BEGIN, una sequenza di Chunks e/o QuoteGroups e un QUOTE_GROUP_END
	<QuoteGroup> ::= (SCRITTO_DA)? QUOTE_GROUP_BEGIN <QuoteGroupBody>* QUOTE_GROUP_END
	<QuoteGroup> ::= SCRITTO_DA
	*/
	public static ParserNode.QuoteGroup parseQuoteGroup(final RTokenizer tokv) throws ParseException {
		if (debug) {
			System.out.println("parseQuoteGroup at token: " + tokv.pos);
		}

		final ParserNode.QuoteGroup qg = new ParserNode.QuoteGroup();
		Token t = tokv.peek(0);
		int advance = 1;
		boolean hasScrittoDa = false;
		if (t.tokenType == Token.Type.SCRITTO_DA) {
			hasScrittoDa = true;
			qg.setScrittoDa(t);
			qg.level = t.quoteLevel + 1;
			++advance;
			t = tokv.peek(1);
		}

		if (t.tokenType != Token.Type.QUOTE_GROUP_BEGIN) {
			if (hasScrittoDa) {
				tokv.advance(1);
				return qg;
			}
			if (debug) {
				System.out.println("\tparseQuoteGroup failed");
			}
			return null;
		}

		qg.level = t.quoteLevel;

		tokv.advance(advance);

		for(;;) {
			final ParserNode child = parseQuoteGroupBody(tokv);
			if (child != null) {
				qg.childs.add(child);
				continue;
			}

			t = tokv.advance(1);
			if (t.tokenType == Token.Type.QUOTE_GROUP_END) {
				break;
			}
			throw new ParseException("Parsing stuck on token: " + t);
		}
		return qg;
	}

	/*
	<QuoteGroupBody> ::= (<QuoteGroup> | <Chunk> | CLOSE_TAG)
	I CLOSE_TAG accettati da questa regola sono quelli sbilanciati, vanno accettati qui perche' non si ammettono errori di sintassi.
	Gli OPEN_TAG non bilanciati non sono un problema, verranno riconosciuti come TagDelimited, il loro corpo si estendera` fino al successivo QUOTE_GROUP_END.
	*/
	public static ParserNode parseQuoteGroupBody(final RTokenizer tokv) throws ParseException {
		/*
		if (tokv.pos >= tokv.tokv.size()) {
			return null;
		}*/

		if (debug) {
			System.out.println("parseQuoteGroupBody at token: " + tokv.pos);
		}

		final ParserNode.QuoteGroup childQg = parseQuoteGroup(tokv);
		if (childQg != null) {
			return childQg;
		}

		final ParserNode.Chunk childChunk = parseChunk(tokv);
		if (childChunk != null) {
			return childChunk;
		}

		Token 	t = tokv.peek(0);
		if (t.tokenType == Token.Type.CLOSE_TAG) {
			final ParserNode.Chunk ctChunk = new ParserNode.Chunk();
			ctChunk.childs.add(new ParserNode.TokenNode(t));
			tokv.advance(1);
			return ctChunk;
		}

		return null;
	}

	/*
	Un chunk e` un link, una emoticon, una sequenza di testo semplice, un newline, una sequenza di &gt; (QUOTE_RUN)  o un testo delimitato da tags
	<Chunk> ::= ( LINK | EMOTICON | TEXT | NL | QUOTE_RUN | PUTTANATA_MICIDIALE | <TagDelimited> )+
	*/
	public static ParserNode.Chunk parseChunk(final RTokenizer tokv) throws ParseException {
		if (debug) {
			System.out.println("parseChunk at token: " + tokv.pos);
		}

		final ParserNode.Chunk c = new ParserNode.Chunk();

		for (;;) {
			final Token t = tokv.peek(0);
			switch (t.tokenType) {
			case LINK:
			case EMOTICON:
			case TEXT:
			case NL:
			case QUOTE_RUN:
			case PUTTANATA_MICIDIALE:
				c.childs.add(new ParserNode.TokenNode(t));
				tokv.advance(1);
				break;

			default:
				final ParserNode.TagDelimited td = parseTagDelimited(tokv);
				if (td != null) {
					c.childs.add(td);
				} else {
					if (c.childs.size() == 0) {
						return null;
					} else {
						return c;
					}
				}
			}
		}
	}

	/*
	Un TagDelimited e` un token OPEN_TAG seguito da una sequenza di Chunks opzionalmente terminato da un CLOSE_TAG che abbia lo stesso tipo del tag di apertura
	<TagDelimited> ::= OPEN_TAG <Chunk>* (CLOSE_TAG)?
	*/
	public static ParserNode.TagDelimited parseTagDelimited(final RTokenizer tokv) throws ParseException {
		if (debug) {
			System.out.println("parseTagDelimited at token: " + tokv.pos);
		}

		final ParserNode.TagDelimited td = new ParserNode.TagDelimited();
		Token t = tokv.peek(0);
		if (t.tokenType != Token.Type.OPEN_TAG) {
			return null;
		}
		td.setOpenTag(t);
		tokv.advance(1);
		td.body = parseChunk(tokv);
		if (td.body == null) {
			td.body = new ParserNode.Chunk();
		}
		t = tokv.peek(0);
		if (t.tokenType == Token.Type.CLOSE_TAG) {
			final String s = t.text.toString().toLowerCase();
			final String ct = s.substring(2, s.length()-1);
			if (ct.equals(td.ot)) {
				td.isClosed = true;
				tokv.advance(1);
			}
		}
		return td;
	}
}
