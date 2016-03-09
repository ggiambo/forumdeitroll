package com.forumdeitroll.util;

import com.forumdeitroll.persistence.MessageDTO;

public class MessageUtils {

	public static String getMessageDivCSS(MessageDTO messageDTO) {
		int rank = messageDTO.getRank();
		if (rank == 0) {
			return "message";
		}
		if (rank < 0) {
			rank = Math.abs(rank);
			return "message_m" + Math.min(4, rank);
		}
		return "message_p" + Math.min(4, rank);
	}

}
