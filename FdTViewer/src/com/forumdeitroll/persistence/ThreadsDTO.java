package com.forumdeitroll.persistence;

import java.util.ArrayList;
import java.util.List;

public class ThreadsDTO {

	private List<ThreadDTO> messages;
	
	// numero totale di messaggi in tutte le pagine
	private int maxNrOfMessages;
	
	public ThreadsDTO() {
		this.messages = new ArrayList<ThreadDTO>();
		this.maxNrOfMessages = 0;
	}
	
	public ThreadsDTO(List<ThreadDTO> messages, int nrOfMessages) {
		this.messages = messages;
		this.maxNrOfMessages = nrOfMessages;
	}
	
	public List<ThreadDTO> getMessages() {
		return messages;
	}
	
	public int getMaxNrOfMessages() {
		return maxNrOfMessages;
	}

}
