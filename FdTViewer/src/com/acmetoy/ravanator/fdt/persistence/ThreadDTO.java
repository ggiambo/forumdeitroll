package com.acmetoy.ravanator.fdt.persistence;

import java.util.Date;

public class ThreadDTO {

	private long id = -1;
	private Date date = null;
	private String subject = null;
	private AuthorDTO author = new AuthorDTO();
	private String forum = null;
	private int setNumberOfMessages = -1;
	
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

	public AuthorDTO getAuthor() {
		return author;
	}

	public void setAuthor(AuthorDTO author) {
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