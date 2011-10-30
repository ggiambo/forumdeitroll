package com.acmetoy.ravanator.fdt;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.fetcher.MessageFetcher;
import com.acmetoy.ravanator.fdt.persistence.Persistence;

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
			lastMessageInDb = Persistence.getInstance().getLastMessageId();
		} catch (Exception e) {
			LOG.error("Cannot get latest available message id database", e);
			return;
		}
		LOG.info("Latest available message id database:" + lastMessageInDb);
		
		BlockingQueue<String> threadQueue = new ArrayBlockingQueue<String>(2);
		// get all messages
		while (lastMessageInDb < lastMessageId) {
			MessageFetcher mf = new MessageFetcher(lastMessageId, threadQueue);
			LOG.debug("Created fetcher for message id " +  lastMessageId + ", queue size " + threadQueue.size());
			try {
				threadQueue.put("dummy");
			} catch (InterruptedException e) {
				LOG.fatal("Cannot add thread to queue", e);
				continue;
			}
			mf.start();
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
