package com.acmetoy.ravanator.fdt.persistence;

import java.util.Date;

public class MessageDTO {

	private long id;
	private long parentId;
	private long threadId;
	private String text;
	private String subject;
	private String author;
	private String forum;
	private Date date;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public boolean isValid() {
		boolean valid = true;
		valid &= parentId != -1;
		valid &= threadId != -1;
		valid &= subject != null;
		valid &= text != null;
		valid &= date != null;
		return valid;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id:").append(id).append(",");
		sb.append("parentId:").append(parentId).append(",");
		sb.append("threadId:").append(threadId).append(",");
		sb.append("text:");
		if (text.length() > 100) {
			sb.append(text.substring(0, 100));
		} else {
			sb.append(text);
		}
		sb.append(",");
		sb.append("subject:").append(subject).append(",");
		sb.append("author:").append(author).append(",");
		sb.append("forum:").append(forum).append(",");
		sb.append("date:").append(date);
		return sb.toString();
	}
}
