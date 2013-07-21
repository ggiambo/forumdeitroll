package com.forumdeitroll.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class DigestArticleDTO implements Serializable {
	
	private static final long serialVersionUID = 4623297248974367987L;
	
	private long threadId;
	private String author;
	private ArrayList<String> participants = new ArrayList<String>();
	private String subject;
	private String openerText;
	private String excerpt;
	private int nrOfMessages;
	private Date startDate;
	private Date lastDate;
	public long getThreadId() {
		return threadId;
	}
	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public ArrayList<String> getParticipants() {
		return participants;
	}
	public void setParticipants(ArrayList<String> participants) {
		this.participants = participants;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getOpenerText() {
		return openerText;
	}
	public void setOpenerText(String openerText) {
		this.openerText = openerText;
	}
	public String getExcerpt() {
		return excerpt;
	}
	public void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}
	public int getNrOfMessages() {
		return nrOfMessages;
	}
	public void setNrOfMessages(int nrOfMessages) {
		this.nrOfMessages = nrOfMessages;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getLastDate() {
		return lastDate;
	}
	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}
}
