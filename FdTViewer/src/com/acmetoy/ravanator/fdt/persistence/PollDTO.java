package com.acmetoy.ravanator.fdt.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PollDTO {
	
	private long id;
	
	private String	title;
	
	private String text;
	
	private String author;
	
	private Date creationDate;
	
	private Date updateDate;
	
	private List<PollQuestion> pollQuestions = new ArrayList<PollQuestion>();
	
	private List<String> pollVoterNicks = new ArrayList<String>();
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public List<PollQuestion> getPollQuestions() {
		return pollQuestions;
	}

	public void setPollQuestions(List<PollQuestion> pollQuestions) {
		this.pollQuestions = pollQuestions;
	}
	
	public List<String> getVoterNicks() {
		return this.pollVoterNicks;
	}
	
	public void setVoterNicks(List<String> pollVoterNicks) {
		this.pollVoterNicks = pollVoterNicks;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id:").append(id).append(",");
		sb.append("title:").append(title).append(",");
		sb.append("text:").append(text).append(",");
		sb.append("author:").append(author).append(",");
		if (creationDate != null) {
			sb.append("creationDate:").append(creationDate).append(",");
		}
		if (updateDate != null) {
			sb.append("updateDate:").append(updateDate).append(",");
		}
		for (PollQuestion q : pollQuestions) {
			sb.append(q.toString());
		}
		return sb.toString();
	}

}
