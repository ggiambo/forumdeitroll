package com.forumdeitroll.markup2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.forumdeitroll.markup.RenderOptions;

/**
Il parsing della sintassi dei messaggi del forum dei troll pone alcuni interessanti problemi che lo rendono difficile da trattare con i comuni approcci utilizzati per il parsing dei linguaggi di programmazione:

1. Non ci sono stati di errore, tutti gli input sono validi
2. Come per python alcuni blocchi sono delimitati da cambiamenti nell'indentazione della riga invece che da comuni caratteri di delimitazione. Nel caso di python e` indentazione vera e propria, nel nostro caso e` la lunghezza della sequenza di &gt; all'inizio della riga.
3. mentre il parsing dei blocchi di testo quotato beneficierebbero di un approccio riga per riga il resto del linguaggio ignora i cambi di riga (ad esempio un tag [b] aperto continua sulla riga successiva)
4. L'interazione tra il tag code e i livelli di quote funziona a cazzo di cane

Ci sono due possibilita` (che io vedo) per la scrittura del parser:

a. Utilizzare due parser, uno che operi riga per riga per dividere i blocchi di testo quotato e un secondo che opera strettamente all'interno di un blocco di testo quotato e riconosce i tag
b. Utilizzare un tokenizer context-sensitive che trasformi i cambi di indentazione in token delimitatori

Dato che in (a) sarebbe comunque necessario avere un tokenizer intelligente ho deciso di andare con la soluzione (b).
Il parser e` un parser a discesa ricorsiva scritto a mano ma non ci sono trucchi sporchi ed ogni funzione e` annotata con la produzione backus naur che la descrive
*/
public class Renderer {
	/**
	Questa e` l'unica funzione importante di questa classe: chiama RTokenizer per fare l'analisi lessicale del messaggio e passa la lista di token ottenuta a parser per fare il parsing.
	Come gia` detto l'analisi lessicale e` fatta da un tokenizzatore intelligente ed e` importante leggerne la descrizione per capire il funzionamento del parser (che e` piuttosto semplice)

	RTokenizer.java contiene il codice del tokenizzatore
	Parser.java contiene il codice del parser a discesa ricorsiva
	*/
	public static void render(final String in, final Writer out, final RenderOptions rops) throws Exception {
		final RTokenizer tokv = RTokenizer.tokenize(in);
		final ParserNode.QuoteGroup msg = Parser.parseMessage(tokv);
		final Parser.Status status = new Parser.Status(rops);
		msg.render(out, status);
	}

	public static RenderOptions[] getAllRenderingOptions() {
		final List<RenderOptions> r = new ArrayList<RenderOptions>(64);

		final boolean[] falsetrue = { false, true };

		for (final boolean embedYoutube: falsetrue) {
			for (final boolean showImagesPlaceholder: falsetrue) {
				for (final boolean renderImages: falsetrue) {
					for (final boolean renderYoutube: falsetrue) {
						for (final boolean collapseQuotes : falsetrue) {
							final RenderOptions x = new RenderOptions();
							x.renderYoutube = renderYoutube;
							x.renderImages = renderImages;
							x.collapseQuotes = collapseQuotes;// fanculo sarru, mi hai fatto skippare dei test!
							x.embedYoutube = embedYoutube;
							x.showImagesPlaceholder = showImagesPlaceholder;
							r.add(x);
						}
					}
				}
			}
		}

		return r.toArray(new RenderOptions[0]);
	}

	static void writeToFile(final String s, final String path, final boolean convBr) throws IOException {
		final BufferedWriter w = new BufferedWriter(new FileWriter(path));
		String z = s;
		if (convBr) {
			z = z.replace("<BR>", "\n");
		}
		w.write(z);
		w.close();
	}

	static String cmpfix(String x) {
		x = x.replaceAll("\\s+", " ");
		x = x.replaceAll("&gt; <", "&gt;<");
		return x;
	}

	static class TestCounts {
		long t;
		int c;
	}

	public static int test(final long mid, final String author, final String body, final RenderOptions[] allRenderingOptions, final boolean writeOnError, final TestCounts oldCount, final TestCounts newCount) throws Exception {
		final StringReader in = new StringReader(body);

		int ecount = 0;

		for (final RenderOptions rops: allRenderingOptions) {
			rops.authorIsAnonymous = ((author != null) && StringUtils.isEmpty(author));

			in.reset();
			final StringWriter outOld = new StringWriter();
			final StringWriter outNew = new StringWriter();

			long start, end;

			start = System.currentTimeMillis();
			final RTokenizer tokv = RTokenizer.tokenize(body);
			//tokv.printDebug(System.out);
			final ParserNode.QuoteGroup msg = Parser.parseMessage(tokv);
			final Parser.Status status = new Parser.Status(rops);
			//msg.printDebug(System.out, 0);
			msg.render(outNew, status);
			end = System.currentTimeMillis();

			if (newCount != null) {
				newCount.t += (end - start);
				++newCount.c;
			}

			start = System.currentTimeMillis();
			com.forumdeitroll.markup.Renderer.render(in, outOld, rops);
			end = System.currentTimeMillis();

			if (oldCount != null) {
				oldCount.t += (end - start);
				++oldCount.c;
			}

			String oldo = outOld.toString();
			String newo = outNew.toString();

			oldo = cmpfix(oldo);
			newo = cmpfix(newo);

			if (!oldo.equals(newo)) {
				if (!writeOnError) {
					System.out.println("=======================\nError at message: " + mid);
					System.out.println("http://127.0.0.1:8080/FdTViewer/Messages?action=getById&msgId=" + mid + "&parseComp=yes");
					System.out.println("Options: " + rops);

					++ecount;
					break;
				} else {
					writeToFile(body, "/tmp/render-in", true);
					writeToFile(oldo, "/tmp/render-tgt", true);
					writeToFile(newo, "/tmp/render-out", true);

					final BufferedWriter w = new BufferedWriter(new FileWriter("/tmp/render-dbg"));
					final PrintWriter pw = new PrintWriter(w);
					pw.println("Tokens:");
					tokv.printDebug(pw);
					pw.println("\nTree:");
					w.write("\nTree:\n");
					msg.printDebug(pw, 0);
					pw.close();

					System.out.println("ERROR\nInput: /tmp/render-in\nTarget: /tmp/render-tgt\nOutput: /tmp/render-out\nDebug: /tmp/render-dbg");
				}
			} else {
				if (writeOnError) {
					System.out.println("OK");
				}
			}
		}
		return ecount;
	}

	static Set<Long> readBlacklist() throws Exception {
		final Set<Long> r = new HashSet<Long>();
		final BufferedReader br = new BufferedReader(new FileReader("/tmp/blacklist"));

		for (;;) {
			final String line = br.readLine();
			if (line == null) break;
			if (line.equals("")) continue;
			final String[] v = line.split("//");
			r.add(Long.parseLong(v[0]));
		}

		br.close();

		return r;
	}

	static void fullTest(final Connection conn, final String statement) throws Exception {
		final PreparedStatement stmt = conn.prepareStatement(statement, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		final RenderOptions[] allRenderingOptions = getAllRenderingOptions();

		final Set<Long> blacklist = readBlacklist();

		final ResultSet rs = stmt.executeQuery();

		final TestCounts oldCount = new TestCounts();
		final TestCounts newCount = new TestCounts();

		try {
			int ecount = 0;

			while (rs.next()) {
				final long tid = rs.getLong(1);
				final long mid = rs.getLong(2);
				final String author = rs.getString(3);
				final String body = rs.getString(4);
				if (blacklist.contains(mid)) {
					continue;
				}
				if (blacklist.contains(tid)) {
					continue;
				}
				System.out.println("" + mid + " thread: " + tid + " author: " + author);
				ecount += test(mid, author, body, allRenderingOptions, false, oldCount, newCount);
				if (ecount >= 1) {
					break;
				}
			}
		} finally {
			rs.close();
			stmt.close();

			float oldAvg = (float)oldCount.t / (float)oldCount.c;
			float newAvg = (float)newCount.t / (float)newCount.c;

			System.out.println("Old: " + oldAvg);
			System.out.println("New: " + newAvg);
		}
	}

	static void singleTest(final Connection conn, final long mid, final RenderOptions rops) throws Exception {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("select author, text from messages where id = ?");

			stmt.setLong(1, mid);
			rs = stmt.executeQuery();
			if (!rs.next()) {
				return;
			}

			final String author = rs.getString(1);
			final String body = rs.getString(2);

			final RenderOptions[] v = { rops };
			test(mid, author, body, v, true, null, null);
		} finally {
			rs.close();
			stmt.close();
		}
	}

	/**
	Utilizzo:
	- se si passa un message id come argomento verra stampato il rendering di quell'id
	- se non si passa alcun argomento tutti i messaggi nel database verranno renderizzati da questa classe e dal vecchio renderer per ogni possibile combinazione di RenderOptions e i risultati comparati, verranno stampati gli id dei messaggi con rendering differente
	*/
	public static void main(final String[] argv) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");

		final Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/fdtsucker","fdtsucker","fdtsucker");

		try {
			switch (argv.length) {
			case 0:
				fullTest(conn, "select threadId, id, author, text from messages order by id desc;");

				break;
			case 1:
			case 2:
				if (argv[0].equals("start")) {
					final long mid = Long.parseLong(argv[1]);
					fullTest(conn, "select threadId, id, author, text from messages where id <= " + mid + " order by id desc;");
					break;
				}

				final long mid = Long.parseLong(argv[0]);

				RenderOptions rops = new RenderOptions();
				if (argv.length > 1) {
					rops = RenderOptions.fromSimpleString(argv[1]);
				}

				singleTest(conn, mid, rops);
				break;

			default:
				System.err.println("Numero di argomenti errato");
				break;
			}

		} finally {
			conn.close();
		}
	}
}
