package com.forumdeitroll.persistence;

public class MessageDTO extends ThreadDTO {

	private long parentId = -1;
	private long threadId = -1;
	private long nextId = -1;
	private long prevId = -1;
	private String text = null;

	private double searchRelevance = -1.0;
	private int searchCount = -1;

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

	public long getNextId() {
		return nextId;
	}

	public void setNextId(final long nextId) {
		this.nextId = nextId;
	}

	public long getPrevId() {
		return prevId;
	}

	public void setPrevId(final long prevId) {
		this.prevId = prevId;
	}

	public String getText() {
		if (getVisibleReal() < 0) return "(messaggio bannato)";
		return text;
	}

	public String getTextReal() {
		return text;
	}

	public String getSubject() {
		if (getId() != getThreadId()) {
			return getSubjectReal();
		}
		return super.getSubject();
	}

	public void setText(String text) {
		this.text = text;
	}

	public double getSearchRelevance() {
		return searchRelevance;
	}

	public int getSearchCount() {
		return searchCount;
	}

	public void setSearchRelevance(final double searchRelevance) {
		this.searchRelevance = searchRelevance;
	}

	public void setSearchCount(final int searchCount) {
		this.searchCount = searchCount;
	}

	public boolean isValid() {
		boolean valid = super.isValid();
		valid &= parentId != -1;
		valid &= threadId != -1;
		valid &= text != null;
		return valid;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("parentId:").append(parentId).append(",");
		sb.append("threadId:").append(threadId).append(",");
		sb.append("text:");
		if (text.length() > 50) {
			sb.append(text, 0, 50);
		} else {
			sb.append(text);
		}
		sb.append(",");
		return sb.toString();
	}
}
