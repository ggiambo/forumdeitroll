package com.acmetoy.ravanator.fdt.fetcher;

import net.htmlparser.jericho.Element;

public class AuthorFetcherData extends Thread {

	private static final String NUMBERS = "1234567890";

	private Element authorContainer;

	private int ranking;
	private int messages;

	AuthorFetcherData(Element authorContainer) {
		this.authorContainer = authorContainer;
	}

	@Override
	public void run() {
		Element elem = authorContainer.getAllElementsByClass("csxItem").get(2);
		// <!--msg scritti --> &nbsp;Msg scritti: 1568
		// <!--ranking--><div>&nbsp;(ranking: 15,68) </div>
		String html = elem.getContent().toString();
		String msg = extractNumber(html, html.indexOf(":") + 1);
		String rnk = extractNumber(html, html.lastIndexOf(":") + 1);
		messages = Integer.parseInt(msg);
		ranking = Integer.parseInt(rnk);
	}

	private String extractNumber(String source, int startingIndex) {
		StringBuilder ret = new StringBuilder();
		while (startingIndex < source.length()) {
			char c = source.charAt(startingIndex);
			if (NUMBERS.indexOf(c) != -1) {
				ret.append(c);
			} else if (c != ' ' && c != ',') {
				break;
			}
			startingIndex++;
		}
		return ret.toString().trim();
	}

	public int getRanking() {
		return ranking;
	}

	public int getMessages() {
		return messages;
	}

}
