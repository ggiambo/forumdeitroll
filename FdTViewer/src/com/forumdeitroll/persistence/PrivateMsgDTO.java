package com.forumdeitroll.persistence;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class PrivateMsgDTO {
	
	public PrivateMsgDTO() {}
	
	private long id;
	private String fromNick;
	private List<String> toNick = new LinkedList<String>();
	private String subject;
	private Date date;
	private String text;
	private boolean read = false;
	private long replyTo;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFromNick() {
		return fromNick;
	}

	public void setFromNick(String fromNick) {
		this.fromNick = fromNick;
	}

	public List<String> getToNick() {
		return toNick;
	}

	public void setToNick(List<String> toNick) {
		this.toNick = toNick;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public long getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(long replyTo) {
		this.replyTo = replyTo;
	}
}
