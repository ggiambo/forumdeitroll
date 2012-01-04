package com.acmetoy.ravanator.fdt.persistence;

import java.util.Date;

public class ThreadDTO {

	private long id;
	private Date date;
	private String subject;
	private String author;
	private String forum;
	private int setNumberOfMessages;
	
	public boolean isValid() {
		boolean valid = true;
		valid &= subject != null;
		valid &= date != null;
		return valid;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getForum() {
		return forum;
	}

	public void setForum(String forum) {
		this.forum = forum;
	}
	
	public int getNumberOfMessages() {
		return setNumberOfMessages;
	}
	
	public void setNumberOfMessages(int setNumberOfMessages) {
		this.setNumberOfMessages = setNumberOfMessages;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id:").append(id).append(",");
		sb.append("date:").append(date).append(",");
		sb.append("subject:").append(subject).append(",");
		sb.append("author:").append(author).append(",");
		sb.append("forum:").append(forum).append(",");
		sb.append("setNumberOfMessages:").append(setNumberOfMessages).append(",");
		return sb.toString();
	}

}
