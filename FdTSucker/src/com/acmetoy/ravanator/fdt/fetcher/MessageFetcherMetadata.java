package com.acmetoy.ravanator.fdt.fetcher;

import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.WebUtilities;

public class MessageFetcherMetadata extends Thread {

	private static final Logger LOG = Logger.getLogger(MessageFetcherMetadata.class);

	private long id;
	private long threadId = -1;
	private long parentId = -1;

	public MessageFetcherMetadata(long id) {
		this.id = id;
	}

	@Override
	public void run() {
		Source source;
		try {
			source = WebUtilities.getPage("http://www.forumdeitroll.it/HTM.aspx?m_id=" + id);
			source.fullSequentialParse();
		} catch (Exception e) {
			LOG.error("Cannot get page http://www.forumdeitroll.it/HTM.aspx?m_id=" + id, e);
			return;
		}

		// threadId
		List<Element> elements = source.getAllElementsByClass("textcapothread");
		if (elements.isEmpty()) {
			LOG.warn("Cannot fetch metadata of message " + id + ": no textcapothread");
			return;
		}

		Element elem = elements.get(0);
		String href = elem.getAttributeValue("href");
		int start = href.indexOf("=") + 1;
		int end = href.indexOf("&");
		threadId = Long.parseLong(href.substring(start, end));

		// parentId
		parentId = getParentId(id, threadId, source);
	}

	/**
	 * Complicato ! Non toccarlo o muori :) !
	 *
	 * @param id
	 * @param source
	 * @return
	 */
	private long getParentId(long id, long threadId, Source source) {
		String thisHref = "m.aspx?m_id=" + id + "&m_rid=0";
		for (Element a : source.getAllElements("a")) {
			String href = a.getAttributeValue("href");
			if (href != null && href.equals(thisHref)) {
				Element thisTd = a.getParentElement();
				if (a.getAttributeValue("class").equals("textcapothread")) {
					// capo thread
					return threadId;
				}
				Element prevTd = thisTd.getParentElement().getChildElements().get(0);
				List<Element> imgs = prevTd.getAllElements("img");
				if (imgs == null || imgs.size() == 0) {
					LOG.error("TD has no images !");
					return threadId;
				}
				int thisIdent = imgs.size() - 1; // "ident" == numero images/trasp.gif
				Element lastImg = imgs.get(thisIdent);
				if (lastImg.getAttributeValue("src").endsWith("link.gif")) {
					// in tutti i div, cerca quello con ident uguale a questo - 1 che sia piu' vicino a questo
					Element candidateTd = null;
					for (Element tr : source.getAllElements(HTMLElementName.TR)) {
						Element td1 = tr.getChildElements().get(0);
						if (td1 == prevTd) {
							// siamo giunti fino a questo
							break;
						}
						imgs = td1.getAllElements("img");
						if (imgs == null || imgs.size() == 0) {
							LOG.error("TD has no images !");
							return threadId;
						}
						if (imgs.size() - 1 == thisIdent -1) {
							candidateTd = tr.getChildElements().get(1);
						}
					}
					
					if (candidateTd == null) {
						return threadId;
					}
					
					String prevHref = candidateTd.getAllElements(HTMLElementName.A).get(0).getAttributeValue("href");
					return Long.parseLong(prevHref.substring(prevHref.indexOf('=') + 1, prevHref.lastIndexOf('&')));
				} else {
					// e' un discendente diretto del thread
					return threadId;
				}
			}
		}
		LOG.error("Cannot find parent id, set to threadId as fallback !");
		return threadId;
	}

	public long getId() {
		return id;
	}

	public long getThreadId() {
		return threadId;
	}

	public long getParentId() {
		return parentId;
	}

}
