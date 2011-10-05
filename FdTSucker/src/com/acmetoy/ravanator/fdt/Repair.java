package com.acmetoy.ravanator.fdt;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.fetcher.MessageFetcher;
import com.acmetoy.ravanator.fdt.persistence.Persistence;

public class Repair extends TimerTask {

	private static final int PAGESIZE = 100;
	private static final Logger LOG = Logger.getLogger(Repair.class);
	
	public static void main(String[] args) {
		new Repair().run();
	}

	@Override
	public void run() {
		Persistence pers = null;
		try {
			pers = Persistence.getInstance();
		} catch (Exception e) {
			LOG.error("Cannot get latest available message id database", e);
			return;
		}
		
		DecimalFormat df = new DecimalFormat("00000");
		long totalMessages = pers.countMessages();
		Long numberOfPages = (pers.countMessages() / PAGESIZE) + 1;
		LOG.info("Checking " + totalMessages + " messages.");

		StringBuilder logMessage = new StringBuilder();
		Set<Long> missingIds = new HashSet<Long>();
		int i = 0;
		for (int page = 0; page < numberOfPages; page++) {
			for (Long messageId : pers.getParentIds(PAGESIZE, page)) {
				if (!pers.hasMessage(messageId)) {
					missingIds.add(messageId);
				}
			}
			
			if (i == 0) {
				logMessage = new StringBuilder("[").append(df.format(page*PAGESIZE)).append("/").append(df.format(totalMessages)).append("] ");
			} else if (i < 79) {
				logMessage.append(".");
			} else {
				LOG.info(logMessage.toString());
				i = -1;
			}
			i++;
		}
		
		while (missingIds.size() > 0) {
			LOG.info("Found  " + missingIds.size() + " missing messages");
			List<Long> ids = new ArrayList<Long>(missingIds);
			fetchMessages(ids);
			// check if also these new messages don't have a parent
			missingIds.clear();
			for (Long messageId : ids) {
				Long parentId = pers.getMessage(messageId).getParentId();
				if (parentId != null && parentId != 0) {
					if (!pers.hasMessage(parentId)) {
						missingIds.add(parentId);
					}
				}
			}
		}

	}
	
	private void fetchMessages(List<Long> ids) {
		BlockingQueue<String> threadQueue = new ArrayBlockingQueue<String>(5);
		for (Long id : ids) {
			MessageFetcher mf = new MessageFetcher(id, threadQueue);
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
