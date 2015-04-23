package com.forumdeitroll.markup3;

public class Tokenizer {
	private final TokenMatcher[][] matchers = TokenCatalog.get();

	public void tokenize(String text, TokenListener handler) throws Exception {
		reset(text);
		TokenMatcher token = null;
		int last = 0;
		while ((token = next()) != null) {
			if (token.start() != last) {
				throw new RuntimeException();
			}
			handler.on(token, additional);
			last = token.end();
			additional = null;
		}
	}

	private String text;
	private int length;
	private int position;
	private TokenizerMode mode;
	private TokenizerMode returnToMode;
	private TokenMatcher nextToken;
	private TokenMatcher additional;
	private int limit;
	private boolean skipBow;

	private void reset(String text) {
		this.text = text;
		this.length = text.length();
		this.position = 0;
		this.mode = TokenizerMode.BEGINNING_OF_LINE;
		this.returnToMode = TokenizerMode.NORMAL;
		this.nextToken = null;
		this.additional = null;
		this.limit = 50000; //id 915608: 251kb, ~47k tokens
		this.skipBow = false;
		for (int i = 0; i < matchers.length; i++) {
			for (int j = 0; j < matchers[i].length; j++) {
				matchers[i][j].reset(text);
			}
		}
	}

	private TokenMatcher next() {
		if (limit == 0) {
			throw new RuntimeException();
		}
		limit--;
		if (position == length) {
			return null;
		}
		if (nextToken != null) {
			TokenMatcher token = nextToken;
			nextToken = null;
			position = token.end();
			return token;
		}
		TokenMatcher token = tryMatch(mode, position, length);
		if (token != null) {
			if (mode != TokenizerMode.BEGINNING_OF_LINE) {
				if (token.name.equals("BR")) {
					returnToMode = mode;
					mode = TokenizerMode.BEGINNING_OF_LINE;
				}
			} else {
				if (!token.name.equals("QUOTES_SCRITTO_DA")) {
					mode = returnToMode;
					returnToMode = null;
				}
			}
			if (token.name.equals("CODE_OPEN") || token.name.equals("CODE_OPEN_WITH_LANG")) {
				if (existsEndCode()) {
					if (isMultiLineCode()) {
						token = new TokenMatcher.Wrapper(token.name  + "_MULTILINE", token);
					}
					mode = TokenizerMode.CODE;
				} else {
					int end = token.name.equals("CODE_OPEN_WITH_LANG") ? position + 5 : token.end();
					token = text(position, end);
				}
			}
			if (token.name.equals("URL")) {
				TokenMatcher link = tryMatch(TokenizerMode.LINK, position + 5, token.end() - 6);
				if (link == null) {
					token = text(position, position + 5);
				} else {
					additional = link;
				}
			}
			if (token.name.equals("IMG")) {
				TokenMatcher link = tryMatch(TokenizerMode.LINK, position + 5, token.end() - 6);
				if (link == null) {
					token = text(position, position + 5);
				} else {
					additional = link;
				}
			}
			if (token.name.equals("URL_OPEN_WITH_LINK")) {
				if (existsEndUrl()) {
					TokenMatcher link = tryMatch(TokenizerMode.LINK, position + 5, token.end() - 1);
					if (link != null) {
						additional = link;
						mode = TokenizerMode.URL;
					} else {
						token = text(position, token.end());
					}
				} else {
					token = text(position, token.end());
				}
			}
			if (token.name.equals("CODE_CLOSE") || token.name.equals("URL_CLOSE")) {
				mode = TokenizerMode.NORMAL;
			}
			if (token.name.equals("YT")) {
				TokenMatcher link = tryMatch(TokenizerMode.LINK, position + 4, token.end() - 5);
				if (link != null) {
					if (link.name.startsWith("LINK_YOUTUBE")) {
						additional = link;
					} else {
						token = text(position, token.end());
					}
				}
			}
			if (token.name.equals("TEXT")) {
				if (text.charAt(position) == ' ') {
					position++;
					skipBow = false;
					return text(position-1, position);
				}
				int textPosition = token.start();
				while (textPosition < token.end()) {
					TokenMatcher word = tryMatch(TokenizerMode.WORDSCAN, textPosition, token.end());
					textPosition = word.end();
					if (word.end(1) - word.start(1) > 4) {
						TokenMatcher link = tryMatch(TokenizerMode.LINK, word.start(1), word.end(1));
						boolean inParens = false;
						if (link == null) {
							if (text.charAt(word.start(1)) == '(' && text.charAt(word.end(1)-1) == ')') {
								link = tryMatch(TokenizerMode.LINK, word.start(1)+1, word.end(1)-1);
								inParens = true;
							}
						}
						if (link != null && !inParens) {
							if (link.end() != word.end(1)) {
								link = null;
							}
						}
						if (link != null) {
							if (link.start() > token.start()) {
								nextToken = link;
								token = text(token.start(), link.start());
							} else {
								token = link;
							}
							break;
						}
					}
					if (skipBow) {
						skipBow = false;
					} else {
						TokenMatcher sub = tryMatch(TokenizerMode.BEGINNING_OF_WORD, word.start(), word.end());
						if (sub != null) {
							if (sub.start() > token.start()) {
								nextToken = sub;
								token = text(token.start(), sub.start());
								break;
							} else {
								token = sub;
								break;
							}
						}
					}
					TokenMatcher sub = tryMatch(TokenizerMode.TEXT, word.start(), word.end());
					if (sub != null) {
						skipBow = sub.end() < token.end();
						if (sub.start() == token.start()) {
							token = sub;
							break;
						} else {
							token = text(token.start(), sub.start());
							nextToken = sub;
							break;
						}
					}
				}
			}
			position = token.end();
			return token;
		}
		if (mode == TokenizerMode.BEGINNING_OF_LINE) {
			mode = returnToMode;
			returnToMode = null;
			return section("QUOTES_EMPTY", position, position);
		}
		throw new RuntimeException("mode: " + mode.name() + ", position: " + position + ", text: " + text);
	}

	private TokenMatcher tryMatch(TokenizerMode mode, int start, int end) {
//		System.out.println("tryMatch("+mode+") of "+text.substring(start, end));
		TokenMatcher[] modeMatchers = matchers[mode.ordinal()];
		TokenMatcher tmp = null;
		for (int i = 0; i < modeMatchers.length; i++) {
			TokenMatcher currentMatch = modeMatchers[i];
			currentMatch.region(start, end);
			if (currentMatch.find()) {
				if (currentMatch.atBeginningOfRegion()) {
//					System.out.println("tryMatch: "+currentMatch);
					return currentMatch;
				} else {
					if (tmp == null || tmp.start() > currentMatch.start()) {
						tmp = currentMatch;
					}
				}
			}
		}
//		if (tmp != null) System.out.println("tryMatch: "+tmp);
		return tmp;
	}

	private TokenMatcher.Section text(int start, int end) {
		TokenMatcher.Section section = new TokenMatcher.Section("TEXT");
		section.reset(text);
		section.region(start, end);
		return section;
	}

	private TokenMatcher.Section section(String name, int start, int end) {
		TokenMatcher.Section section = new TokenMatcher.Section(name);
		section.reset(text);
		section.region(start, end);
		return section;
	}

	private boolean existsEndCode() {
		return
			TokenMatcher.Section.indexOf(
				text, position, length - position,
				"[/code]", 0, 7,
				0, true, false) != -1;
	}

	private boolean isMultiLineCode() {
		int endCodePosition = TokenMatcher.Section.indexOf(
			text, position, length - position,
			"[/code]", 0, 7,
			0, true, false);
		int brPosition = TokenMatcher.Section.indexOf(
			text, position, length - position,
			"<BR>", 0, 4,
			0, false, false);
		return brPosition != -1 && brPosition < endCodePosition;
	}

	private boolean existsEndUrl() {
		return
			TokenMatcher.Section.indexOf(
				text, position, length - position,
				"[/url]", 0, 6,
				0, true, false) != -1;
	}
}
