package com.acmetoy.ravanator.fdt;

import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.fetcher.MessageFetcher;
import com.acmetoy.ravanator.fdt.fetcher.MessageFetcherMetadata;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.acmetoy.ravanator.fdt.persistence.Persistence;

public class Relink extends TimerTask {

	private static final Logger LOG = Logger.getLogger(Relink.class);
	private static final int FIRST_FDT_MSG = 753580;

	@Override
	public void run() {
		Persistence pers = null;
		try {
			pers = Persistence.getInstance();
		} catch (Exception e) {
			LOG.error("Cannot get persistence", e);
			return;
		}

		BlockingQueue<String> threadQueue = new ArrayBlockingQueue<String>(5);
		long msgId = pers.getLastMessageId();
		MessageDTO msgDTO;
		while (msgId > FIRST_FDT_MSG) {
//			LOG.info("threadQueue size: " + threadQueue.size());
			msgDTO = pers.getMessage(msgId);
			Thread t;
			if (msgDTO.isValid()) {
//				LOG.info("Check msgId: " + msgId);
				t = new MessageRelinker(msgId, msgDTO.getParentId(), threadQueue, pers);
			} else {
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
			msgId--;
		}
	}

	/**
	 * Fetch & relink
	 * 
	 * @author giambo
	 * 
	 */
	public class MessageRelinker extends MessageFetcherMetadata {

		private BlockingQueue<String> threadQueue;
		private Persistence pers;
		private long previousParentId;

		public MessageRelinker(long id, long previousParentId, BlockingQueue<String> threadQueue, Persistence pers) {
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
