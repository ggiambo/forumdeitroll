package com.forumdeitroll.persistence;

public class PollQuestion {
	
	private long pollId;
	
	private int sequence;
	
	private String text;
	
	private int votes;

	public long getPollId() {
		return pollId;
	}

	public void setPollId(long pollId) {
		this.pollId = pollId;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("pollId:").append(pollId).append(",");
		sb.append("sequence:").append(sequence).append(",");
		sb.append("text:").append(text).append(",");
		sb.append("votes:").append(votes).append(",");
		return sb.toString();
	}
}
