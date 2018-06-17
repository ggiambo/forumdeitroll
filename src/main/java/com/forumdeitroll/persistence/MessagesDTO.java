package com.forumdeitroll.persistence;

import java.util.List;

public class MessagesDTO {

	private List<MessageDTO> messages;

	// numero totale di messaggi in tutte le pagine
	private int maxNrOfMessages;

	public MessagesDTO(List<MessageDTO> messages, int nrOfMessages) {
		this.messages = messages;
		this.maxNrOfMessages = nrOfMessages;
	}

	public List<MessageDTO> getMessages() {
		return messages;
	}

	public int getMaxNrOfMessages() {
		return maxNrOfMessages;
	}

}
