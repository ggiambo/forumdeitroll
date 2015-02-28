package com.forumdeitroll.markup3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import com.forumdeitroll.markup.InputSanitizer;
import com.forumdeitroll.markup.RenderOptions;
import com.forumdeitroll.persistence.DAOFactory;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.sql.mysql.Utf8Mb4Conv;

public class GreatRendereresShootout {
	public static void main(String[] args) throws Exception {
		try {
			preStart();
//			runFromUser("ok", new RenderOptions());
//			runFromUser("> ok", new RenderOptions());
//			runFromUser(">\n>", new RenderOptions());
//			runFromUser(">\n\n", new RenderOptions());
//			runFromUser("Scritto da: gs\n> text", new RenderOptions());
//			runFromUser("Scritto da: \n> text", new RenderOptions());
//			runFromUser("Scritto da: \n> Scritto da: \n> > text\n> text\nok", new RenderOptions());
//			runFromUser(":) ", new RenderOptions());
//			printTokens("https://www.youtube.com/watch?v=eDXnjIpLJFs");
//			runFromUser("https://www.youtube.com/watch?v=eDXnjIpLJFs", new RenderOptions());
//			testRange();
//			runFromUser("Scritto da: \n> :'(:'(:'(\n\ntesto :'(\n\ntesto", new RenderOptions());
//			printTokens("Scritto da: The Lich\n");
//			printTokensSingleMessage(2824049);
//			testSingleMessage(2823830);
//			printTokens("http://forumdeitroll.com/Threads?action=getByThread&threadId=2821608");
//			runFromUser("https://www.facebook.com/video.php?v=379424138884811", new RenderOptions());
//			runFromDb(0, "", RenderOptions.fromSimpleString("ipYeq"));
//			printTokensDb("Scritto da: <BR>&gt; Scritto da: <BR>&gt; &gt; Scritto da: altg<BR>&gt; &gt; &gt; &gt; è come negare che l'energia abbia inerzia<BR>&gt; &gt; &gt; <BR>&gt; &gt; &gt; Scusa ma... l'energia puo` trasmettere quantita` di moto sotto forma di pressione di radiazione e questo e` osservabile.<BR>&gt; &gt; <BR>&gt; &gt; <BR>&gt; &gt; interessante <BR>&gt; &gt; <BR>&gt; &gt; &gt; Il karma che minkia farebbe per farsi osservare? (newbie)<BR>&gt; &gt; <BR>&gt; &gt; ci devo pensare<BR>&gt; &gt; può portare alle famose mutazioni genetiche? (geek)(ghost)<BR>&gt; &gt; <BR>&gt; <BR>&gt; ..ahhh! ci risiamo..!! :D :D<BR>&gt; <BR>&gt; [img]http://img1.wikia.nocookie.net/__cb20080529095931/nonciclopedia/images/0/07/Urlo.jpg[/img]<BR><BR>:D :D");
//			printTokens("^test ^^ ok");
//			runFromUser("^test ^^ok", new RenderOptions());
//			printTokensSingleMessage(2823988);
//			runFromDb(2823988, "<BR><BR>Ne ^fap-fap-fap valeva ^fap-fap-fap  la pena ^fap-fap-fap :$ ", new RenderOptions());
//			printTokens("^a b ^c d");
//			runFromUser("^a b ^c d", new RenderOptions());
//			runCompetition();
//			testSingleMessage(2775233);
//			testSingleMessage(2798073);
//			printTokensDb("Cosa fai il 4 Dicembre (geek) ?<BR>http://www.cdt.ch/ticino/cronaca/118658/kalashnikov-e-colt-in-un-sol-colpo.html<BR>[url]http://www4.ti.ch/di/dg/uef/aste/dettaglio/?user_diasteonline_pi1[id]=99258[/url]<BR><BR><BR><b>**Modificato dall'autore il 07.11.2014 09:05**</b>");
			testFromUser("> quote<i>\nno quote\n>quote</i>\n> quote");
//			testFromUser("[url=Messages?action=getById&msgId=2779559]abcde fghil jkmno pqrst uvwyx zabcd efghi ljkmn opqrs tuwyx z[/url]");
//			testFromUser("[url=http://en.wikipedia.org/wiki/Richard_A._Gardner]abcde fghil jkmno pqrst uvwyx zabcd efghi ljkmn opqrs tuwyx z[/url]");
//			printTokens("[yt]https://www.youtube.com/watch?v=eDXnjIpLJFs[/yt]");
//			testFromUser("[yt]https://www.youtube.com/watch?v=eDXnjIpLJFs[/yt]");
//			runFromUser("\n\nbla bla bla^ritardato\n\n", new RenderOptions());
//			printTokensDb("[yt]2vjPBrBU-TM[/yt]<BR>[yt]YUTTn0KjHsY[/yt]<BR><BR>Maddie Ziegler, 12 anni.<BR>http://en.wikipedia.org/wiki/Maddie_Ziegler");
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		printRunTimes();
	}

	private static void runCompetition() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fdtsucker?useUnicode=yes&characterEncoding=UTF-8", "fdtsucker", "fdtsucker");
		PreparedStatement ps = connection.prepareStatement(
			"SELECT id, threadId, text "
			+ "FROM messages "
			//+ "WHERE id > 2787793 "
			+ "WHERE date > str_to_date('01-01-2014','%d-%m-%Y') "
			+ "LIMIT ?,?"
		);
		int count = -1;
		int pageNumber = 0;
		int objectsPerPage = 1000;
		long lastId = -1;
		long lastThreadId = -1;
		RenderOptions[] optss = com.forumdeitroll.markup2.Renderer.getAllRenderingOptions();
		while (count != 0) {
			count = 0;
			ps.setInt(1, pageNumber * objectsPerPage);
			ps.setInt(2, objectsPerPage);
			pageNumber++;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				lastId = rs.getLong(1);
				lastThreadId = rs.getLong(2);
				count++;
				ArrayList<String[]> output = new ArrayList<String[]>(optss.length);
				for (RenderOptions opts : optss) {
					try {
						output.add(runFromDb(rs.getLong(1), rs.getLong(2), rs.getString(3), opts));
					} catch (Exception e) {
						System.out.println("id = " + lastId);
						throw e;
					}
				}
				reportDelta(lastId, lastThreadId, optss, output);
			}
			rs.close();
			System.out.println(String.format("[%s] Elab %d", new Date().toString(), lastId));
			printRunTimes();
		}
		ps.close();
		connection.close();
	}

	private static HashMap<Long, String> commenti = new HashMap<Long, String>(){{
		put(2775337l, "migliore parse url");
		put(2778130l, "migliore parse url");
		put(2778130l, "migliore parse url");
		put(2779134l, "migliore parse url");
		put(2787294l, "migliore parse url");
		put(2792845l, "gestione emoticons");
		put(2795091l, "migliore parse url");
		put(2796653l, "limite buflen");
		put(2800339l, "migliore parse url");
		put(2800560l, "migliore parse url");
		put(2801548l, "TODO: parsare parametro t in url youtube");
		put(2805362l, "migliore parse url");
		put(2805425l, "migliore parse url");
		put(2801548l, "YODO: parsare parametro t in url youtube");
		put(2805362l, "migliore parse url");
		put(2807087l, "diverso parse url http://[IP esterno corrente]:44444");
		put(2815562l, "YODO: parsare parametro t in url youtube");

	}};

	private static void reportDelta(long id, long threadId, RenderOptions[] optss, ArrayList<String[]> output) throws Exception {
		StringBuilder catrow = new StringBuilder();
		for (int i = 0; i < optss.length; i++) {
			String[] results = output.get(i);
			boolean diff = !(results[0].equals(results[1]) && results[0].equals(results[2]));
			if (diff && catrow.length() == 0) {
				catrow.append(id).append(" ").append(threadId);
			}
			if (diff) {
				boolean pa = !results[0].equals(results[1]);
				boolean nopa = !results[0].equals(results[2]);
				if (output.size() == optss.length) {
					catrow.append(" ANY " + (pa ? "Pa" : "") + (nopa ? "NoPa" : ""));
					break;
				}
				catrow.append(" ").append(optss[i].toSimpleString()+":" + (pa ? "Pa" : "") + (nopa ? "NoPa" : ""));
			}
		}
		if (catrow.length() != 0) {
			FileReader sarrublacklist = new FileReader("/home/milky/fdt/webdev/FdtDueZero/FdTViewer/src/com/forumdeitroll/markup2/blacklist");
			BufferedReader br = new BufferedReader(sarrublacklist);
			String line;
			String sid = String.valueOf(id);
			while ((line = br.readLine()) != null) {
				if (line.startsWith(sid)) {
					catrow.append(" ").append(line, sid.length(), line.length());
				}
			}
			sarrublacklist.close();
			if (commenti.get(threadId) != null) {
				catrow.append(" # ").append(commenti.get(threadId));
			}
			FileWriter cat = new FileWriter("src/com/forumdeitroll/markup3/catalogo.txt", true);
			cat.write(catrow.append("\n").toString().toCharArray());
			cat.close();
		}
	}

	private static void printTokensSingleMessage(long id) throws Exception {
		String text = DAOFactory.getMessagesDAO().getMessage(id).getText();
		new Tokenizer().tokenize(text, TokenListener.PRINTER);
	}
	private static void testSingleMessage(long id) throws Exception {
		MessageDTO dto = DAOFactory.getMessagesDAO().getMessage(id);
		String text = dto.getText();
		testSingleMessage(id, dto.getThreadId(), text);
	}
	private static void testFromUser(String text) throws Exception {
		testSingleMessage(0, 0, Utf8Mb4Conv.mb4safe(InputSanitizer.sanitizeText(text)));
	}
	private static void testSingleMessage(long id, long threadId, String text) throws Exception {
		ArrayList<String[]> output = new ArrayList<String[]>();
		for (RenderOptions opts : com.forumdeitroll.markup2.Renderer.getAllRenderingOptions()) {
			String[] results = runFromDb(id, threadId, text, opts);
			output.add(results);

		}
		for (int i = 0; i < output.size(); i++) {
			String[] results = output.get(i);
			RenderOptions opts = com.forumdeitroll.markup2.Renderer.getAllRenderingOptions()[i];
			boolean diff = !(results[0].equals(results[1]) && results[0].equals(results[2]));
			if (diff) {
				printTokensDb(text);
				int c = 0;
				while (c<Math.min(results[0].length(), Math.min(results[1].length(), results[2].length()))) {
					if (results[0].charAt(c) != results[1].charAt(c)) {
						break;
					}
					if (results[0].charAt(c) != results[2].charAt(c)) {
						break;
					}
					c++;
				}
				if (output.size() == com.forumdeitroll.markup2.Renderer.getAllRenderingOptions().length) {
					System.out.println("Opts: ANY");
				} else {
					System.out.println("Opts: " + opts.toSimpleString());
				}
				System.out.println("Text: " + text);
				System.out.println("Ref : " + results[0]);
				if (!results[0].equals(results[1])) {
					System.out.println("Pa  : "+results[1]);
				}
				if (!results[0].equals(results[2])) {
					System.out.println("NoPa: "+results[2]);
				}
				StringBuilder o = new StringBuilder("Delt: ");
				while (c --> 0) {
					o.append(' ');
				}
				o.append("^^^^^^^^^^^^^^^^^^^^^");
				System.out.println(o);
				System.out.flush();
				if (output.size() == com.forumdeitroll.markup2.Renderer.getAllRenderingOptions().length) {
					break;
				}
			}
		}
	}
	private static void testRange() throws Exception {
		for (MessageDTO messageDTO : DAOFactory.getMessagesDAO().getMessages(null, null, 1000, 1, null).getMessages()) {
			for (RenderOptions opts : com.forumdeitroll.markup2.Renderer.getAllRenderingOptions()) {
				runFromDb(messageDTO.getId(), messageDTO.getThreadId(), messageDTO.getText(), opts);
			}
		}
	}
	private static void runAllFromUser(String markup) throws Exception {
		for (RenderOptions opts : com.forumdeitroll.markup2.Renderer.getAllRenderingOptions()) {
			runFromUser(markup, opts);
		}
	}
	private static void runFromUser(String markup, RenderOptions opts) throws Exception {
		runFromDb(0, 0, Utf8Mb4Conv.mb4safe(InputSanitizer.sanitizeText(markup)), opts);
	}
	private static long[] runTimes = new long[3];
	private static void printRunTimes() {
		System.out.println(String.format(
			"Tot Ref:\t%d\tPa:\t%d\tNoPa:\t%d"
			, runTimes[0]/1000000, runTimes[1]/1000000, runTimes[2]/1000000));
	}
	private static void preStart() throws Exception {
		renderReference("", new RenderOptions());
		renderParser("", new RenderOptions());
		renderNoParser("", new RenderOptions());
		com.mysql.jdbc.Driver.class.getName();
	}
	private static String[] runFromDb(long id, long threadId, String dbmarkup, RenderOptions opts) throws Exception {
		ArrayList<String> order = new ArrayList<String>(Arrays.asList(new String[] {"1","2","3"}));
		Collections.shuffle(order);
		long start = 0, end = 0;
		String[] output = new String[3];
		for (int i = 0; i < order.size(); i++) {
			switch (Integer.parseInt(order.get(i))) {
			case 1:
				start = System.nanoTime();
				output[0] = renderReference(dbmarkup, opts);
				end = System.nanoTime();
				runTimes[0] += (end - start);
				break;
			case 2:
				start = System.nanoTime();
				output[1] = renderParser(dbmarkup, opts);
				end = System.nanoTime();
				runTimes[1] += (end - start);
				break;
			case 3:
				start = System.nanoTime();
				output[2] = renderNoParser(dbmarkup, opts);
				end = System.nanoTime();
				runTimes[2] += (end - start);
				break;
			}
		}
		return output;
	}
	private static String renderReference(String dbmarkup, RenderOptions opts) throws Exception {
		StringWriter out = new StringWriter();
		com.forumdeitroll.markup.Renderer.render(new StringReader(dbmarkup), out, opts);
		return out.toString();
	}
	private static String renderParser(String dbmarkup, RenderOptions opts) throws Exception {
		StringWriter out = new StringWriter();
		com.forumdeitroll.markup2.Renderer.render(dbmarkup, out, opts);
		return out.toString();
	}
	private static String renderNoParser(String dbmarkup, RenderOptions opts) throws Exception {
		StringWriter out = new StringWriter();
		com.forumdeitroll.markup3.Renderer.render(dbmarkup, out, opts);
		return out.toString();
	}
	private static void printTokens(String markup) throws Exception {
		markup = Utf8Mb4Conv.mb4safe(InputSanitizer.sanitizeText(markup));
		new Tokenizer().tokenize(markup, TokenListener.PRINTER);
	}
	private static void printTokensDb(String dbmarkup) throws Exception {
		new Tokenizer().tokenize(dbmarkup, TokenListener.PRINTER);
	}
}
