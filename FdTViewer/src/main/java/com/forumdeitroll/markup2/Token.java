package com.forumdeitroll.markup2;


class Token {
	enum Type {
		QUOTE_GROUP_BEGIN,
		QUOTE_GROUP_END,
		SCRITTO_DA,
		OPEN_TAG,
		CLOSE_TAG,
		EMOTICON,
		LINK,
		NL,
		TEXT,
		QUOTE_RUN,
		PUTTANATA_MICIDIALE,
		END,
	}

	public final Type tokenType;
	public final int quoteLevel;
	public final Substring text;
	public CharSequence scrittoDaQuoteRun;
	public boolean oldStyle;
	public boolean multiLine;
	public String repl;

	public Token(final Type tokenType, final int quoteLevel, final Substring text) {
		this.tokenType = tokenType;
		this.quoteLevel = quoteLevel;
		this.text = text;
	}

	public String toString() {
		return "<" + tokenType.toString() + " level=" + quoteLevel + " text=" + text + ">";
	}
}
