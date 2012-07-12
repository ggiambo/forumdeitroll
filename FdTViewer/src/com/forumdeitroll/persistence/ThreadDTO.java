package com.forumdeitroll.persistence;

import java.util.Date;

public class ThreadDTO {

	private long id = -1;
	private Date date = null;
	private String subject = null;
	private AuthorDTO author = new AuthorDTO(null);
	private String forum = null;
	private int setNumberOfMessages = -1;
	private boolean isVisible = true;

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
		return ((forum != null) && (forum.equals(IPersistence.FORUM_ASHES))) ? "Cenere alla cenere, polvere alla polvere" :  subject;
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

	public boolean isVisible() {
		return isVisible;
	}

	public void setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
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
		sb.append("isVisible:").append(isVisible).append(",");
		return sb.toString();
	}

}
