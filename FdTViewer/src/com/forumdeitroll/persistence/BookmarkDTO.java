package com.forumdeitroll.persistence;

import java.io.Serializable;

public class BookmarkDTO implements Serializable {
	private static final long serialVersionUID = 2189689509770938685L;
	private String nick, subject;
	private long msgId;

	@Override
	public String toString() {
		return "BookmarkDTO{nick="+nick+";threadId="+msgId+"subject="+subject+"}";
	}
	
	public long getMsgId() {
		return msgId;
	}

	public void setMsgId(long threadId) {
		this.msgId = threadId;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
}
