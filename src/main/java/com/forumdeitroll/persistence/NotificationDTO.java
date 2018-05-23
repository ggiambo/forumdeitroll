package com.forumdeitroll.persistence;

public class NotificationDTO {
	
	private long id;
	
	private String fromNick;
	
	private String toNick;
	
	private long msgId;

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

	public String getToNick() {
		return toNick;
	}

	public void setToNick(String toNick) {
		this.toNick = toNick;
	}

	public long getMsgId() {
		return msgId;
	}

	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id:").append(id).append(",");
		sb.append("fromNick:").append(fromNick).append(",");
		sb.append("toNick:").append(toNick).append(",");
		sb.append("msgId:").append(msgId);
		
		return sb.toString();
	}
}
