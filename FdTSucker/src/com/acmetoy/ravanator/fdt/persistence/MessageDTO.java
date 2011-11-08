package com.acmetoy.ravanator.fdt.persistence;


public class MessageDTO extends ThreadDTO {

	private long parentId;
	private long threadId;
	private String text;

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
			sb.append(text.substring(0, 50));
		} else {
			sb.append(text);
		}
		sb.append(",");
		return sb.toString();
	}
}
