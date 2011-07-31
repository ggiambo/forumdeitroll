package com.acmetoy.ravanator.fdt.datafetcher;

import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.WebUtilities;
import com.acmetoy.ravanator.fdt.persistence.MessagePersistence;

public class PageFetcher implements Runnable {

	private static final String MAINPAGE = "http://www.forumdeitroll.it/c.aspx?f_id=127&Page=";
	
	private static final Logger LOG = Logger.getLogger(PageFetcher.class);

	private int pageNr;

	public PageFetcher(int pageNr) {
		this.pageNr = pageNr;
	}

	@Override
	public void run() {

		try {
			Source source = WebUtilities.getPage(MAINPAGE + pageNr);
			List<Element> linkElements = source.getAllElements(HTMLElementName.DIV);
			for (Element elem : linkElements) {
				if ("divthread".equals(elem.getAttributeValue("class"))) {
					LOG.info("Found 'divthread' element");
					for (Element e : elem.getChildElements()) {
						String elemClass = e.getAttributeValue("class");
						LOG.info("'elemClass' = '" + elemClass);
						if ("textcapothread".equals(elemClass) || "nick".equals(elemClass)) {
							String href = e.getAttributeValue("href");
							LOG.info("'href' = '" + href);
							Long id = new Long(href.replaceAll("m.aspx\\?m_id=", "").replaceAll("&m_rid=0", ""));
							if (!MessagePersistence.getInstance().hasMessage(id)) {
								new Thread(new MessageFetcherCallBack(id)).start();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			LOG.error(e);
		}

	}
}