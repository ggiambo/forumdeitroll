package com.forumdeitroll.lucene;

import com.forumdeitroll.persistence.DAOFactory;
import com.forumdeitroll.persistence.MessageDTO;
import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class Test {

	private static final Logger LOG = Logger.getLogger(Test.class);

	public static void main(String[] args) throws Exception {
		Lucene lucene = new Lucene(DAOFactory.getMessagesDAO());

		SearchParams params = new SearchParams();
		params.author("Giambo");
//		params.subject("linux");
//		params.dateFrom(LocalDate.of(2015, Month.JANUARY, 1));
		params.dateTo(LocalDate.of(2016, Month.DECEMBER, 1));


		List<MessageDTO> messageDTOS = lucene.searchMessages(params);

		messageDTOS.forEach(LOG::error);
	}
}
