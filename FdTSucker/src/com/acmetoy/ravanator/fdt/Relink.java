package com.acmetoy.ravanator.fdt;

import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.fetcher.MessageFetcher;
import com.acmetoy.ravanator.fdt.fetcher.MessageFetcherMetadata;
import com.acmetoy.ravanator.fdt.persistence.IPersistence;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.acmetoy.ravanator.fdt.persistence.PersistenceFactory;

public class Relink extends TimerTask {

	private static final Logger LOG = Logger.getLogger(Relink.class);
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

		BlockingQueue<String> threadQueue = new ArrayBlockingQueue<String>(3);
		long msgId = pers.getLastMessageId();
		MessageDTO msgDTO;
		Thread t;
		while (msgId > FIRST_FDT_MSG) {
			msgId--;
			if (msgId % 100 == 0) {
				LOG.info("Status: " + msgId);
			}
			msgDTO = pers.getMessage(msgId);
			if (msgDTO.isValid()) {
				t = new MessageRelinker(msgId, msgDTO.getParentId(), threadQueue, pers);
			} else {
				if (pers.hasMessage(msgId)) {
					continue;
				}
				LOG.info("Try to fetch: " + msgId);
				t = new MessageFetcher(msgId, threadQueue);
			}
			try {
				threadQueue.put("dummy");
			} catch (InterruptedException e) {
				LOG.fatal("Cannot add thread to queue", e);
				continue;
			}
			t.start();
		}
	}

	/**
	 * Fetch & relink
	 * 
	 * @author giambo
	 * 
	 */
	private static class MessageRelinker extends MessageFetcherMetadata {

		private BlockingQueue<String> threadQueue;
		private IPersistence pers;
		private long previousParentId;

		public MessageRelinker(long id, long previousParentId, BlockingQueue<String> threadQueue, IPersistence pers) {
			super(id);
			this.previousParentId = previousParentId;
			this.threadQueue = threadQueue;
			this.pers = pers;
		}

		@Override
		public void run() {
			try {
				super.run();
				if (getParentId() != previousParentId) {
					pers.updateMessageParentId(getId(), getParentId());
					LOG.info("Relink: id=" + getId() + " parentId=" + getParentId());
				}
			} finally {
				try {
					threadQueue.take();
				} catch (InterruptedException e) {
					LOG.fatal("Cannot remove thread from queue", e);
				}
			}
		}

	}

}
