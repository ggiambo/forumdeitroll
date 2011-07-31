package com.acmetoy.ravanator.fdt.datafetcher;

import java.util.Date;

import com.acmetoy.ravanator.fdt.persistence.MessagePersistence;

public class MessageFetcherCallBack implements Runnable, CallBackClass {

	private long id;
	private int count = 0;

	private Long parentId;
	private Long threadId;
	private String text;
	private String subject;
	private String author;
	private Date date;

	public MessageFetcherCallBack(long id) {
		this.id = id;
	}

	@Override
	public void run() {
		new Thread(new MessageBodyFetcher(id, this)).start();
		new Thread(new MessageMetadataFetcher(id, this)).start();
	}

	@Override
	public void callBack(Object source) throws Exception {
		synchronized (this) {
			if (source instanceof MessageMetadataFetcher) {
				MessageMetadataFetcher f = (MessageMetadataFetcher) source;
				parentId = f.getParentId();
				threadId = f.getThreadId();
				author = f.getAuthor();
				date = f.getDate();
				count++; // expected 2 callback
			} else if (source instanceof MessageBodyFetcher) {
				MessageBodyFetcher f = (MessageBodyFetcher) source;
				subject = f.getSubject();
				text = f.getText();
				count++; // expected 2 callback
			} else {
				throw new Exception("Unknown class " + source.getClass().getName());
			}
			if (count == 2) {
				MessagePersistence.getInstance().insertMessage(id, parentId, threadId, text, subject, author, date);
			}
		}
	}

}
