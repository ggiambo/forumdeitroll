package com.acmetoy.ravanator.fdt;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.fetcher.MessageFetcher;
import com.acmetoy.ravanator.fdt.persistence.PersistenceFactory;

public class Sucker extends TimerTask {

	private static final Logger LOG = Logger.getLogger(Sucker.class);
	
	@Override
	public void run() {
		
		// get latest messageId
		long lastMessageId;
		try {
			lastMessageId = getLatestAvailableMessageId();
		} catch (Exception e) {
			LOG.error("Cannot get latest available message id in FdT", e);
			return;
		}
		LOG.info("Latest available message id in FdT:" + lastMessageId);
		
		// get latest messageId from local database
		long lastMessageInDb;
		try {
			lastMessageInDb = PersistenceFactory.getPersistence().getLastMessageId();
		} catch (Exception e) {
			LOG.error("Cannot get latest available message id database", e);
			return;
		}
		LOG.info("Latest available message id database:" + lastMessageInDb);
		
		ExecutorService exec = Executors.newFixedThreadPool(4);
		// get all messages
		while (lastMessageInDb < lastMessageId) {
			exec.execute(new MessageFetcher(lastMessageId));
			lastMessageId--;
		}
	}
	
	/**
	 * Piglia l'id dell'ultimo messaggio postato nel FdT
	 * @return
	 * @throws Exception
	 */
	private Long getLatestAvailableMessageId() throws Exception {
		Source source = WebUtilities.getPage("http://www.forumdeitroll.it/c.aspx?f_id=127");
		List<Element> linkElements = source.getAllElementsByClass("divthread");
		for (Element elem : linkElements) {
			for (Element e : elem.getChildElements()) {
				String elemClass = e.getAttributeValue("class");
				if ("textcapothread".equals(elemClass) || "nick".equals(elemClass)) {
					String href = e.getAttributeValue("href");
					return new Long(href.replaceAll("m.aspx\\?m_id=", "").replaceAll("&m_rid=0", ""));
				}
			}
		}
		return null;
	}
	
}
