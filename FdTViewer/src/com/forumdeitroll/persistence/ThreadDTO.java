package com.forumdeitroll.persistence;

import java.util.Date;

public class ThreadDTO {

	private long id = -1;
	private long lastId = -1;
	private Date date = null;
	private String subject = null;
	private AuthorDTO author = new AuthorDTO(null);
	private String fakeAuthor;
	private String forum = null;
	private int setNumberOfMessages = -1;
	private int isVisible = 1;
	private int rank = 0;

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
	public long getLastId() {
		return lastId;
	}
	public void setLastId(long lastId) {
		this.lastId = lastId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSubject() {
		if (isVisible < 0) return "(messaggio bannato)";
		return subject;
	}

	public String getSubjectReal() {
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

	public String getFakeAuthor() {
		return fakeAuthor;
	}

	public void setFakeAuthor(String fakeAuthor) {
		this.fakeAuthor = fakeAuthor;
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
		return isVisible > 0;
	}

	public void setIsVisible(int isVisible) {
		this.isVisible = isVisible;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id:").append(id).append(",");
		sb.append("lastId:").append(lastId).append(",");
		sb.append("date:").append(date).append(",");
		sb.append("subject:").append(subject).append(",");
		sb.append("author:").append(author).append(",");
		sb.append("fakeAuthor:").append(fakeAuthor).append(",");
		sb.append("forum:").append(forum).append(",");
		sb.append("setNumberOfMessages:").append(setNumberOfMessages).append(",");
		sb.append("isVisible:").append(isVisible).append(",");
		sb.append("rank:").append(rank).append(",");
		return sb.toString();
	}

	public int getVisibleReal() {
		return isVisible;
	}

	public void modInfoException() {
		if (isVisible < 0) isVisible = 0;
	}
}
