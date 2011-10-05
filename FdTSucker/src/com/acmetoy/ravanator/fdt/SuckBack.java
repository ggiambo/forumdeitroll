package com.acmetoy.ravanator.fdt;

import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.fetcher.MessageFetcher;
import com.acmetoy.ravanator.fdt.persistence.Persistence;

public class SuckBack extends TimerTask {

	private static final Logger LOG = Logger.getLogger(SuckBack.class);

	private static final int FIRST_FDT_MSG = 753580;

	@Override
	public void run() {

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
		while (FIRST_FDT_MSG < lastMessageInDb) {
			lastMessageInDb--;
			try {
				if (Persistence.getInstance().hasMessage(lastMessageInDb)) {
					continue;
				}
			} catch (Exception e) {
				LOG.error("Cannot get existence of message " + lastMessageInDb, e);
				return;
			}
			MessageFetcher mf = new MessageFetcher(lastMessageInDb, threadQueue);
			LOG.debug("Created fetcher for message id " +  lastMessageInDb + ", queue size " + threadQueue.size());
			try {
				threadQueue.put("dummy");
			} catch (InterruptedException e) {
				LOG.fatal("Cannot add thread to queue", e);
				continue;
			}
			mf.start();
		}
	}

}
