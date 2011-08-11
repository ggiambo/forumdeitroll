package com.acmetoy.ravanator.fdt.fetcher;

import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
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
		} catch (Exception e) {
			LOG.error("Cannot get page http://www.forumdeitroll.it/HTM.aspx?m_id=" + id, e);
			return;
		}

		// threadId
		List<Element> elements = source.getAllElementsByClass("textcapothread");
		if (elements.isEmpty()) {
			LOG.warn("Cannot fetch body of message " + id + ": no funcbarthread");
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
		List<Long[]> threadList = new ArrayList<Long[]>();

		Element container = source.getAllElementsByClass("threaditem").get(0).getChildElements().get(2);
		boolean analyzeNext = false;
		long prevDottos = -1;
		for (Element td : container.getAllElements("td")) {
			int dottos = 0;
			for (Element img : td.getChildElements()) {
				if ("img".equals(img.getName()) && "dotto".equals(img.getAttributeValue("class"))) {
					dottos++;
				}
			}
			if (dottos > 0) {
				prevDottos = dottos;
				analyzeNext = true;
			} else {
				if (analyzeNext) {
					String href = td.getAllElements("a").get(0).getAttributeValue("href");
					int start = href.indexOf("=") + 1;
					int end = href.indexOf("&");
					long thisId = Long.parseLong(href.substring(start, end));
					if (id == thisId) {
						// search
						for (int i = threadList.size() - 1; i > -1; --i) {
							Long[] elem = threadList.get(i);
							if (elem[0] < prevDottos) {
								return elem[1];
							}
						}
					} else {
						threadList.add(new Long[] { prevDottos, thisId });
					}
				}
				analyzeNext = false;
			}
		}
		return threadId;
	}

	public long getThreadId() {
		return threadId;
	}

	public long getParentId() {
		return parentId;
	}

}
