package com.acmetoy.ravanator.fdt.fetcher;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.acmetoy.ravanator.fdt.persistence.PersistenceFactory;

public class MessageFetcher extends Thread {

	private static final Logger LOG = Logger.getLogger(MessageFetcherMetadata.class);

	private long messageId;
	private BlockingQueue<String> threadQueue;

	public MessageFetcher(long messageId) {
		this.messageId = messageId;
	}
	
	public MessageFetcher(long messageId, BlockingQueue<String> threadQueue) {
		this.messageId = messageId;
		this.threadQueue = threadQueue;
	}

	@Override
	public void run() {
		try {
			MessageFetcherMetadata metadata = new MessageFetcherMetadata(messageId);
			metadata.start();
			MessageFetcherBody body = new MessageFetcherBody(messageId);
			body.start();
			// wait for threads to end
			try {
				metadata.join();
			} catch (Exception e) {
				LOG.error("metadata.join()", e);
				return;
			}
			try {
				body.join();
			} catch (Exception e) {
				LOG.error("body.join()", e);
				return;
			}
			LOG.info("Fetching message " + messageId);
			// persist the message
			MessageDTO message = new MessageDTO();
			message.setId(messageId);
			message.setParentId(metadata.getParentId());
			message.setThreadId(metadata.getThreadId());
			message.setAuthor(body.getAuthor());
			message.setDate(body.getDate());
			message.setSubject(body.getSubject());
			message.setText(body.getText());
			message.setForum(body.getForum());
			if (!message.isValid()) {
				return;
			}
			StringBuilder log = new StringBuilder("Persisting message [");
			log.append(message.toString());
			log.append("]");
			LOG.info(log.toString());
			try {
				PersistenceFactory.getPersistence().insertMessage(message);
			} catch (Exception e) {
				LOG.error("Cannot persist with messageId " + messageId, e);
			}
		} finally {
			try {
				if (threadQueue != null) {
					threadQueue.take();
				}
			} catch (InterruptedException e) {
				LOG.fatal("Cannot remove thread from queue", e);
			}
		}
	}
	
}