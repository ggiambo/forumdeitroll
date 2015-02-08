package com.forumdeitroll.test.persistence;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.QuoteDTO;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QuoteTest extends BaseTest {

	@Test
	public void test_getQuotes() {
		AuthorDTO author = new AuthorDTO(null);
		author.setNick("Sfigato");
		List<QuoteDTO> res = quotesDAO.getQuotes(author);

		assertNotNull(res);
		assertEquals(2, res.size());

		QuoteDTO quote = res.get(0);
		assertEquals(1, quote.getId());
		assertEquals("Sfigato", quote.getNick());
		assertEquals("Che la fortuna sia con me !", quote.getContent());

		quote = res.get(1);
		assertEquals(2, quote.getId());
		assertEquals("Sfigato", quote.getNick());
		assertEquals("Un quadrifoglio esplosivo ...", quote.getContent());

	}

	@Test
	public void test_getAllQuotes() {
		List<QuoteDTO> res = quotesDAO.getAllQuotes();

		// sort by id
		Collections.sort(res, new Comparator<QuoteDTO>() {
			public int compare(QuoteDTO q1, QuoteDTO q2) {
				double delta = q1.getId() - q2.getId();
				return (int) delta;
			}
		});

		assertNotNull(res);
		assertEquals(3, res.size());

		QuoteDTO quote = res.get(0);
		assertEquals(1, quote.getId());
		assertEquals("Sfigato", quote.getNick());
		assertEquals("Che la fortuna sia con me !", quote.getContent());

		quote = res.get(1);
		assertEquals(2, quote.getId());
		assertEquals("Sfigato", quote.getNick());
		assertEquals("Un quadrifoglio esplosivo ...", quote.getContent());

		quote = res.get(2);
		assertEquals(3, quote.getId());
		assertEquals("admin", quote.getNick());
		assertEquals("Il mio forum, il mio tessssoro !", quote.getContent());
	}

	@Test
	public void test_insertUpdateQuote() {

		AuthorDTO author = new AuthorDTO(null);
		author.setNick("Sfigato");

		QuoteDTO newQuote = new QuoteDTO();
		newQuote.setContent("A new funny quote");
		newQuote.setNick(author.getNick());

		quotesDAO.insertUpdateQuote(newQuote);

		List<QuoteDTO> res = quotesDAO.getQuotes(author);

		assertNotNull(res);
		assertEquals(3, res.size());

		QuoteDTO quote = res.get(0);
		assertEquals(1, quote.getId());
		assertEquals("Sfigato", quote.getNick());
		assertEquals("Che la fortuna sia con me !", quote.getContent());

		quote = res.get(1);
		assertEquals(2, quote.getId());
		assertEquals("Sfigato", quote.getNick());
		assertEquals("Un quadrifoglio esplosivo ...", quote.getContent());

		quote = res.get(2);
		assertEquals(4, quote.getId());
		assertEquals(newQuote.getNick(), quote.getNick());
		assertEquals(author.getNick(), quote.getNick());
		assertEquals(newQuote.getContent(), quote.getContent());

	}

	@Test
	public void test_removeQuote() {

		AuthorDTO author = new AuthorDTO(null);
		author.setNick("Sfigato");

		QuoteDTO existingQuote = new QuoteDTO();
		existingQuote.setId(2);
		existingQuote.setNick(author.getNick());

		quotesDAO.removeQuote(existingQuote);
		List<QuoteDTO> res = quotesDAO.getQuotes(author);

		assertNotNull(res);
		assertEquals(1, res.size());

		QuoteDTO quote = res.get(0);
		assertEquals(1, quote.getId());
		assertEquals("Sfigato", quote.getNick());
		assertEquals("Che la fortuna sia con me !", quote.getContent());

	}

}
