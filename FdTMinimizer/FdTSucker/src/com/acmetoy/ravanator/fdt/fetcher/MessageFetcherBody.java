package com.acmetoy.ravanator.fdt.fetcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.WebUtilities;

public class MessageFetcherBody extends Thread {

	private static final Logger LOG = Logger.getLogger(MessageFetcherMetadata.class);

	private long id;

	private String subject;
	private String author;
	private Date date;
	private String text;
	private String forum;

	public MessageFetcherBody(long id) {
		this.id = id;
	}

	@Override
	public void run() {
		Source source;
		try {
			source = WebUtilities.getPage("http://www.forumdeitroll.it/m.aspx?m_id=" + id);
		} catch (Exception e) {
			LOG.error("Cannot get page http://www.forumdeitroll.it/m.aspx?m_id=" + id, e);
			return;
		}
		// set text
		List<Element> elements = source.getAllElementsByClass("funcbarthread");
		if (elements.isEmpty()) {
			LOG.warn("Cannot fetch body of message " + id + ": no funcbarthread");
			return;
		}
		Element elem = elements.get(0);
		text = elem.getAllElements("div").get(0).getContent().toString();

		// set subject
		elem = source.getAllElementsByClass("topbarthread").get(1);
		elem = elem.getAllElements("td").get(2);
		subject = elem.getAllElements("b").get(0).getContent().toString();
		
		// set date
		String d = elem.getContent().toString();
		d = d.substring(d.lastIndexOf("&nbsp") + 6).trim();
		try {
			date = new SimpleDateFormat("dd MMMM yyyy HH.mm", Locale.ITALIAN).parse(d);
		} catch (ParseException e) {
			LOG.error("Cannot parse " + d, e);
		}
		
		// set author
		Element authorContainer = source.getAllElementsByClass("fh4").get(0);
		elements = authorContainer.getAllElementsByClass("nick");
		if (elements.size() == 1) {
			author = elements.get(0).getAllElements("a").get(0).getContent().toString();
			// insert / update author
			new AuthorFetcher(authorContainer, author).run();
		}
		
		// set forum
		elem = source.getAllElementsByClass("fh2").get(1);
		elements = elem.getAllElements("a");
		if (elements.size() == 1) {
			forum = elements.get(0).getContent().toString();
		}

	}

	public String getText() {
		return text;
	}

	public String getAuthor() {
		return author;
	}

	public Date getDate() {
		return date;
	}

	public String getSubject() {
		return subject;
	}

	public String getForum() {
		return forum;
	}

}
