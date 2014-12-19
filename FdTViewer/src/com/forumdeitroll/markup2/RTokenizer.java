package com.forumdeitroll.markup2;

import java.io.Reader;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

import com.forumdeitroll.markup.Emoticons;
import com.forumdeitroll.markup.Emoticon;
import com.forumdeitroll.markup.Snippet;

/**
Effettua l'analisi lessicale di un messaggio del forumdeitroll.
Il tokenizzatore e` definito da una macchiana a stati.

=== STATO tsBOL ===

Stato iniziale del tokenizzatore. Il tokenizzatore ritorna in questo stato all'inizio di ogni linea, ovvero dopo aver riconosciuto un tag <BR>.

Vengono riconosciuti i seguenti token:

SCRITTO_DA ::= (&gt; ? ?)*(- )?Scritto da: (.*?)<BR>
QUOTE_RUN ::= (&gt; ? ?)*
QUOTE_GROUP_BEGIN ::= cambiamento nel livello di quote
QUOTE_GROUP_END ::= cambiamento nel livello di quote

Per livello di quote si intende il numero di &gt; riconosciuti all'inizio della linea.
Definiamo:
	n = livello di quote della riga precedente
	m = livello di quote della riga attuale

Se n > m il tokenizzatore emettera` n-m token QUOTE_GROUP_END
Se n < m il tokenizzatore emettera` m-n token QUOTE_GROUP_BEGIN

QUOTE_GROUP_BEGIN e QUOTE_GROUP_END verranno sempre bilanciati, se il livello di quote dell'ultima riga e` n verranno emessi n QUOTE_GROUP_END.

Il livello di quote iniziale (prima di esaminare la prima riga) viene settato a -1 quindi verra` sempre emesso almeno un QUOTE_GROUP_BEGIN all'inizio del messaggio.

Quando tsBOL non riesce a riconoscere nessun token si passa allo stato tsNormal

=== STATO tsNormal ===

Lo stato tsNormal riconosce la maggior parte della sintassi:

OPEN_TAG ::= '<' (u|i|s) '>'
OPEN_TAG ::= '[' (url|code|spoiler|color) .*? ']'
CLOSE_TAG ::= '</' (u|i|s) '>'
CLOSE_TAG ::= '[/' (url|code|spoiler|color) ']'
NL ::= '<br>'
EMOTICON ::= <una delle emoticon o uno snippet>
LINK ::= <un link>
PUTTANATA_MICIDIALE ::= '^'+<una parola>
TEXT ::= qualunque altra cosa

Se viene letto l'OPEN_TAG [code] si passa allo stato tsCode
Se viene letto l'OPEN_TAG [url] senza argomenti si passa allo stato tsUrl
Se viene letto il token NL si passa allo stato tsBOL

=== STATO tsUrl e tsCode ===

Accettano un po' tutto il testo finche' non trovano il loro tag di chiusura, emettono il testo letto come singolo token.
Lo stato tsCode trasforma i <BR> che incontra in caratteri '\n'. Se shitCodeComp non e` abilitato, inoltre, pulisce l'inizio di ogni riga dai caratteri di quote (&gt;).
*/
class RTokenizer {
	static final String[] linkInits = {
		"www.",
		"http://",
		"https://",
		"threads?action=",
		"polls?action=",
		"messages?action=",
		"misc?action=",
		"ftp://",
		"mailto:",
	};

	static final String[] tagOpens = { "[url", "[color ", "[code", "[spoiler", "[yt", "[img", "[code" };
	static final String[] tagCloses = { "[/url", "[/color", "[/code", "[/spoiler", "[/yt", "[/img" };
	static final String[] tlds = { "com", "net", "org", "it", "eu" };


	/* Stato a tokenizzazione finita */
	final List<Token> tokv = new ArrayList<Token>();
	int pos = 0;

	/* Stato durante la tokenizzazione */
	Substring ss = null;
	int level = -1;

	interface TokenizerState {
		public TokenizerState tokenize();
	}

	public static RTokenizer tokenize(final String in) {
		final RTokenizer r = new RTokenizer();
		r.tokenizeIntl(in);
		return r;
	}

	public Token peek(final int i) {
		if (pos + i >= tokv.size())
			return new Token(Token.Type.END, 0, new Substring(""));
		return tokv.get(pos + i);
	}

	public Token advance(final int i) {
		final Token r = tokv.get(pos);
		pos += i;
		return r;
	}

	protected void tokenizeIntl(final String in) {
		ss = new Substring(in, 0);
		TokenizerState ts = tsBOL;

		while (ts != null) {
			ts = ts.tokenize();
		}

		changeLevel(-1);
	}

	public void printDebug(final PrintWriter w) {
		for (int i = 0; i < tokv.size(); ++i) {
			w.println("" + i + ": " + tokv.get(i).toString());
		}
	}

	void emit(Token.Type tt, final Substring cs) {
		if ((tt == Token.Type.TEXT) && (cs.length() == 0)) {
			return;
		}
		tokv.add(new Token(tt, level, cs));
	}

	void changeLevel(int newLevel) {
		while (level > newLevel) {
			emit(Token.Type.QUOTE_GROUP_END, new Substring(""));
			--level;
		}
		while (level < newLevel) {
			++level;
			emit(Token.Type.QUOTE_GROUP_BEGIN, new Substring(""));
		}
	}

	int countGt(int max) {
		int n = 0;
		boolean old = true;
		while (true) {
			if (n == max) {
				return n;
			}
			if (ss.startsWithAndAdvance("&gt;") == null) {
				break;
			}
			ss.startsWithAndAdvance(" ");
			ss.startsWithAndAdvance(" ");
			++n;
		}
		return n;
	}

	Substring isDomain() {
		int i = 0;
		for (;;) {
			if (i >= ss.length()) {
				return null;
			}
			final char c = ss.charAt(i);
			if (!Character.isAlphabetic(c) && !Character.isDigit(c) && (c != '-')) {
				break;
			}
			++i;
		}
		if (ss.charAt(i) != '.') {
			return null;
		}

		++i;

		boolean found = false;
		for (final String tld: tlds) {
			if (ss.startsWithIAt(tld, i+ss.start)) {
				found = true;
				i += tld.length();
				break;
			}
		}

		if (!found) {
			return null;
		}

		int end = ss.start + i;

		if (i < ss.length()) {
			char c = ss.charAt(i);
			if ((c == '.') || (c == ',') || (c == ':') || (c == ';') || (c == '\"') || (c == '\'') || (c == '?') || (c == '!')) {
				++i;
				if (i < ss.length()) {
					c = ss.charAt(i);
				} else {
					c = 0x00;
				}
			}
			if (!Character.isWhitespace(c) && (c != '<') && (c != '[') && (c != 0x00)) {
				return null;
			}
		}

		final Substring r = new Substring(ss.buf, ss.start, end);
		ss.start = end;
		return r;
	}

	/*
	Stato del tokenizzatore all'inizio della linea, riconosce sequenze di &gt; e la sequenza "Scritto da:", null'altro
	*/
	final TokenizerState tsBOL = new TokenizerState() {
		public TokenizerState tokenize() {
			final int qrs = ss.start;
			changeLevel(countGt(-1));

			Substring qrss = null;
			if (ss.start > qrs) {
				qrss = new Substring(ss.buf, qrs, ss.start);
			}

			Substring t = null;

			if ((t = ss.startsWithAndAdvance("Scritto da: ")) != null) {
				t.start = t.end;
				t.end = ss.advanceTo("<BR>");
				final Token tok = new Token(Token.Type.SCRITTO_DA, level, t);
				tok.scrittoDaQuoteRun = qrss;
				tokv.add(tok);
				return tsBOL;
			}

			if ((t =ss.startsWithAndAdvance("- Scritto da: ")) != null) {
				t.start = t.end;
				t.end = ss.advanceTo("<BR>");
				final Token tok = new Token(Token.Type.SCRITTO_DA, level, t);
				tok.oldStyle = true;
				tok.scrittoDaQuoteRun = qrss;
				tokv.add(tok);
				return tsBOL;
			}

			if (qrss != null) {
				emit(Token.Type.QUOTE_RUN, qrss);
			}

			return tsNormal;
		}
	};

	/*
	Stato normale del tokenizzatore, ovunque tranne che all'inizio della riga.
	Riconosce tag aperti e chiusi, emoticons e link.
	Nella variabile nt viene accumulato il testo non riconosciuto, viene emesso tutto assieme in un singolo token quando uno degli altri token viene riconosciuto.
	Quando incontra la sequenza "<BR>" emette il token NL e ritorna allo stato tsBOL.
	I blocchi [code] vengono letti da tsCode.
	*/
	final TokenizerState tsNormal = new TokenizerState() {
		public TokenizerState tokenize() {
			Substring nt = new Substring(ss.buf, ss.start, ss.start);

			boolean isWordStart = true;
			boolean prevSpace = true;

			while (ss.start < ss.end) {
				Substring t = null;

				nt.end = ss.start;

				// HTML tags and <BR>
				if (ss.charAt(0) == '<') {
					emit(Token.Type.TEXT, nt);

					if (ss.length() >= 3) {
						switch (Character.toLowerCase(ss.charAt(1))) {
						case 'u':
						case 'i':
						case 's':
							if (ss.charAt(2) == '>') {
								ss.start += 3;
								emit(Token.Type.OPEN_TAG, new Substring(ss.buf, ss.start-3, ss.start));
								return tsNormal;
							}
							break;

						case 'b':
							switch (Character.toLowerCase(ss.charAt(2))) {
							case '>':
								ss.start += 3;
								emit(Token.Type.OPEN_TAG, new Substring(ss.buf, ss.start-3, ss.start));
								return tsNormal;

							case 'r':
								if ((ss.length() >= 4) && (ss.charAt(3) == '>')) {
									ss.start += 4;
									emit(Token.Type.NL, new Substring(ss.buf, ss.start-4, ss.start));
									return tsBOL;
								}

							}
							break;

						case '/':
							if (ss.length() >= 4) {
								switch (Character.toLowerCase(ss.charAt(2))) {
								case 'u':
								case 'i':
								case 's':
								case 'b':
									if (ss.charAt(3) == '>') {
										ss.start += 4;
										emit(Token.Type.CLOSE_TAG, new Substring(ss.buf, ss.start-4, ss.start));
									}
									return tsNormal;
								}
							}
							break;
						}
					}

					++ss.start;
					emit(Token.Type.TEXT, new Substring("&gt;"));
					return tsNormal;

				}

				// Emoticons e snippets
				for (final Emoticon emo: Emoticons.tutte) {
					final String seq = emo.initialSequence;
					if (emo.sequenceStartWithSpace) {
						if (!prevSpace) {
							continue;
						}
					}
					t = ss.startsWithIAndAdvance(seq);
					if (t != null) {
						emit(Token.Type.TEXT, nt);
						final Token tk = new Token(Token.Type.EMOTICON, level, t);
						tk.repl = emo.getHtmlReplacement();
						tokv.add(tk);
						return tsNormal;
					}
				}
				// Snippet e Emoticon sono due classi cosi` diverse e svolgono una funzione cosi` radicalmente diversa che vanno assolutamente tenute separate.
				for (final Snippet snippet: Snippet.list) {
					t = ss.startsWithAndAdvance(snippet.sequence);
					if (t != null) {
						emit(Token.Type.TEXT, nt);
						final Token tk = new Token(Token.Type.EMOTICON, level, t);
						tk.repl = snippet.htmlReplacement;
						tokv.add(tk);
						return tsNormal;
					}
				}

				// Links
				if (isWordStart) {
					for (final String linkInit: linkInits) {
						t = ss.startsWithIAndAdvance(linkInit);
						if (t != null) {
							ss.advanceToLinkEnd();
							t.end = ss.start;
							backToLinkEnd(t);
							emit(Token.Type.TEXT, nt);
							emit(Token.Type.LINK, t);
							return tsNormal;
						}
					}
					t = isDomain();
					if (t != null) {
						emit(Token.Type.TEXT, nt);
						emit(Token.Type.LINK, t);
						return tsNormal;
					}
				}

				// Tags

				if ((ss.charAt(0) == '[') && (ss.length() > 2)) {
					String[] searched = tagOpens;
					Token.Type tokenType = Token.Type.OPEN_TAG;
					if (ss.charAt(1) == '/') {
						searched = tagCloses;
						tokenType = Token.Type.CLOSE_TAG;
					}
					for (final String s: searched) {
						t = ss.startsWithIAndAdvance(s);
						if (t != null) {
							ss.advanceTo("]");
							if (t.end == ss.start) {
								emit(Token.Type.TEXT, nt);
								emit(Token.Type.TEXT, t);
								return tsNormal;
							} else {
								t.end = ss.start;
								emit(Token.Type.TEXT, nt);
								emit(tokenType, t);
								if ((tokenType == Token.Type.OPEN_TAG) && (s.equals("[url")) && (t.length() == 5)) {
									return tsUrl;
								}
								if ((tokenType == Token.Type.OPEN_TAG) && (s.equals("[code"))) {
									return tsCode;
								}
								return tsNormal;
							}
						}
					}
				}

				// Puttanata colossale che non avrebbe mai dovuto essere aggiunta alla sintassi
				/*
				Funziona male, per implementarla come e` implementata nel parser vecchio servirebbe una regola del parser apposta per questa minchiata
				*/
				if (ss.charAt(0) == '^') {
					final char stopch = ss.charAt(0);
					int i = 1;
					int depth = 1;
					for (; i < ss.length(); ++i) {
						if (ss.charAt(i) != '^')
							break;
						++depth;
					}
					int start = i;
					for (; i < ss.length(); ++i) {
						// solo 1024 caratteri massimo, perche' 1023 va bene ma 1024 e` chiaramente un errore, meglio limitare i danni
						if (i >= 1024) {
							break;
						}
						char c = ss.charAt(i);
						if ((c == '<') || (c == ' ') || (c == '\t') || (c == '[') || (c == '_') || (c == stopch)) {
							break;
						}
					}

					if (i > 1) {
						emit(Token.Type.TEXT, nt);
						final Token tk = new Token(Token.Type.PUTTANATA_MICIDIALE, level, new Substring(ss.buf, ss.start+start, ss.start+i));
						tk.puttanataMicidialeDepth = depth;
						tokv.add(tk);
						ss.start += i;
						return tsNormal;
					}
				}

				final char c = ss.charAt(0);
				prevSpace = Character.isWhitespace(c);
				isWordStart = Character.isWhitespace(c) || (c == '(');

				++ss.start;
			}

			nt.end = ss.start;
			emit(Token.Type.TEXT, nt);

			return null;
		}
	};

	final TokenizerState tsCode = new TokenizerState() {
		public TokenizerState tokenize() {
			boolean atBOL = false;
			final StringBuilder sb = new StringBuilder();
			boolean multiLine = false;

			while (ss.start < ss.end) {
				Substring t = null;

				if (atBOL && !Parser.shitCodeComp) {
					countGt(level);
				}

				if ((ss.startsWithAndAdvance("<BR>") != null) || (ss.startsWithAndAdvance("\n") != null)) {
					sb.append("\n");
					atBOL = true;
					multiLine = true;
					continue;
				}


				t = ss.startsWithAndAdvance("[/code]");
				if (t != null) {
					final Token tok = new Token(Token.Type.TEXT, level, new Substring(sb.toString()));
					tok.multiLine = multiLine;
					tokv.add(tok);
					emit(Token.Type.CLOSE_TAG, t);
					return tsNormal;
				}

				sb.append(ss.charAt(0));
				++ss.start;
			}

			final Token tok = new Token(Token.Type.TEXT, level, new Substring(sb.toString()));
			tok.multiLine = multiLine;
			tokv.add(tok);
			return null;
		}
	};

	final TokenizerState tsUrl = new TokenizerState() {
		public TokenizerState tokenize() {
			Substring nt = new Substring(ss.buf, ss.start, ss.start);
			TokenizerState next = null;
			while (ss.start < ss.end) {
				nt.end = ss.start;
				Substring t = ss.startsWithIAndAdvance("[/url]");
				if (t != null) {
					emit(Token.Type.TEXT, nt);
					emit(Token.Type.CLOSE_TAG, t);
					return tsNormal;
				}

				if (ss.charAt(0) == '<') {
					next = tsNormal;
					break;
				}

				++ss.start;
			}
			emit(Token.Type.TEXT, nt);
			return next;
		}
	};

	protected void backToLinkEnd(final Substring t) {
		boolean hasParen = false;
		for (int i = 0; i < t.length(); ++i) {
			if (t.charAt(i) == '(') {
				hasParen = true;
				break;
			}
		}

		for (int i = t.end-1; i > t.start; --i) {
			char c = t.buf[i];
			if (Character.isAlphabetic(c) || Character.isDigit(c) || (c == '_') || (c == '/') || (c == '-') || (c == '&') || (c == '=') || (c == '?') || (c == '#') || (hasParen && c == ')')) {
				t.end = i+1;
				ss.start = i+1;
				break;
			}
		}
	}
}
