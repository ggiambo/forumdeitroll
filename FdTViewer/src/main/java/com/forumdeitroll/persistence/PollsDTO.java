package com.forumdeitroll.persistence;

import java.util.ArrayList;
import java.util.List;

public class PollsDTO {

	private List<PollDTO> polls;
	
	// numero totale di polls in tutte le pagine
	private int maxNrOfPolls;
	
	public PollsDTO() {
		this.polls = new ArrayList<PollDTO>();
		this.maxNrOfPolls = 0;
	}
	
	public PollsDTO(List<PollDTO> polls, int maxNrOfPolls) {
		this.polls = polls;
		this.maxNrOfPolls = maxNrOfPolls;
	}
	
	public List<PollDTO> getPolls() {
		return polls;
	}
	
	public int getMaxNrOfPolls() {
		return maxNrOfPolls;
	}
	
	public void setMaxNrOfPolls(int maxNrOfPolls) {
		this.maxNrOfPolls = maxNrOfPolls;
	}

}
