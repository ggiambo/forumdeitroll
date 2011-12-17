package com.acmetoy.ravanator.fdt;

import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.fetcher.MessageFetcher;
import com.acmetoy.ravanator.fdt.persistence.IPersistence;
import com.acmetoy.ravanator.fdt.persistence.PersistenceFactory;

public class SuckBack extends TimerTask {

	private static final Logger LOG = Logger.getLogger(SuckBack.class);
	private static final int FIRST_FDT_MSG = 753580;

	@Override
	public void run() {
		IPersistence pers = null;
		try {
			pers = PersistenceFactory.getInstance();
		} catch (Exception e) {
			LOG.error("Cannot get persistence", e);
			return;
		}

		BlockingQueue<String> threadQueue = new ArrayBlockingQueue<String>(4);
		long msgIdTop = pers.getLastMessageId();
		long msgIdBottom = FIRST_FDT_MSG;
		while (msgIdTop > msgIdBottom) {
			fetch(msgIdTop--, threadQueue, pers);
			fetch(msgIdBottom++, threadQueue, pers);
		}
	}
	
	private void fetch(long msgId, BlockingQueue<String> threadQueue, IPersistence pers) {
		if (msgId % 100 == 0) {
			LOG.info("Status: " + msgId);
		}
		Thread t;
		if (pers.hasMessage(msgId)) {
			return;
		}
		LOG.info("Try to fetch: " + msgId);
		t = new MessageFetcher(msgId, threadQueue);
		try {
			threadQueue.put("dummy");
		} catch (InterruptedException e) {
			LOG.fatal("Cannot add thread to queue", e);
			return;
		}
		t.start();
	}

}
