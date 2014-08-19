package com.forumdeitroll.test.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.PollDTO;
import com.forumdeitroll.persistence.PollQuestion;
import com.forumdeitroll.persistence.PollsDTO;

public class PollTest extends BaseTest {

	@Test
	public void test_createPoll() {

		PollDTO newPoll = new PollDTO();
		newPoll.setAuthor("Sfigato");
		newPoll.setText("Proviamo a creare un nuovo poll, OK ?");
		newPoll.setTitle("Nuovo poll ?");

		List<PollQuestion> pollQuestions = new ArrayList<PollQuestion>();
		PollQuestion question = new PollQuestion();
		question.setSequence(0);
		question.setText("Si");
		pollQuestions.add(question);
		question = new PollQuestion();
		question.setSequence(1);
		question.setText("No");
		pollQuestions.add(question);

		newPoll.setPollQuestions(pollQuestions);

		persistence.createPoll(newPoll);

		PollsDTO polls = persistence.getPollsByDate(1, 0);
		assertNotNull(polls);
		List<PollDTO> res = polls.getPolls();
		assertNotNull(res);
		assertEquals(1, res.size());

		PollDTO poll = polls.getPolls().get(0);
		assertEquals(newPoll.getAuthor(), poll.getAuthor());
		assertEquals(newPoll.getText(), poll.getText());
		assertEquals(newPoll.getTitle(), poll.getTitle());
		assertEquals(3, poll.getId());
		assertNotNull(poll.getCreationDate());
		assertNotNull(poll.getUpdateDate());
		assertNotNull(poll.getVoterNicks());
		assertEquals(0, poll.getVoterNicks().size());


		List<PollQuestion> questions = poll.getPollQuestions();
		assertNotNull(questions);
		assertEquals(2, questions.size());

		question = questions.get(0);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(0, question.getSequence());
		assertEquals("Si", question.getText());
		assertEquals(0, question.getVotes());

		question = questions.get(1);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(1, question.getSequence());
		assertEquals("No", question.getText());
		assertEquals(0, question.getVotes());

	}

	@Test
	public void test_updatePollQuestion() {

		AuthorDTO author = new AuthorDTO(null);
		author.setNick("Sfigato");

		PollsDTO polls = persistence.getPollsByDate(1, 0);
		PollDTO poll = polls.getPolls().get(0);
		PollQuestion updatedQuestion = poll.getPollQuestions().get(0);
		assertTrue(persistence.updatePollQuestion(updatedQuestion, author));

		polls = persistence.getPollsByDate(1, 0);
		poll = polls.getPolls().get(0);
		poll.getAuthor().contains(author.getNick());
		PollQuestion question = poll.getPollQuestions().get(0);
		assertEquals(question.getPollId(), question.getPollId());
		assertEquals(updatedQuestion.getSequence(), question.getSequence());
		assertEquals(updatedQuestion.getText(), question.getText());
		assertEquals(2, question.getVotes());

		assertFalse(persistence.updatePollQuestion(updatedQuestion, author));

	}

	@Test
	public void test_getPollsByDate() throws Exception {

		PollsDTO polls = persistence.getPollsByDate(99, 0);
		assertNotNull(polls);
		List<PollDTO> res = polls.getPolls();
		assertNotNull(res);
		assertEquals(2, res.size());

		PollDTO poll = res.get(0);
		assertEquals("admin", poll.getAuthor());
		assertEquals(getDateFromDatabaseString("2014-08-19 15:00:25"), poll.getCreationDate());
		assertEquals(2, poll.getId());
		assertEquals("Eh (newbie) ?", poll.getText());
		assertEquals("Sei perseo ?", poll.getTitle());
		assertEquals(getDateFromDatabaseString("2014-08-19 15:00:49"), poll.getUpdateDate());

		List<PollQuestion> questions = poll.getPollQuestions();
		assertNotNull(questions);
		assertEquals(2, questions.size());

		PollQuestion question = questions.get(0);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(0, question.getSequence());
		assertEquals("Trentaseo", question.getText());
		assertEquals(1, question.getVotes());

		question = questions.get(1);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(1, question.getSequence());
		assertEquals("Puppa !", question.getText());
		assertEquals(0, question.getVotes());

		List<String> nicks = poll.getVoterNicks();
		assertNotNull(nicks);
		assertEquals(1, nicks.size());
		assertTrue(nicks.contains("admin"));

		poll = res.get(1);
		assertEquals("Sfigato", poll.getAuthor());
		assertEquals(getDateFromDatabaseString("2014-08-19 14:59:21"), poll.getCreationDate());
		assertEquals(1, poll.getId());
		assertEquals("Sono utili ?", poll.getText());
		assertEquals("I sondaggi", poll.getTitle());
		assertEquals(getDateFromDatabaseString("2014-08-19 15:00:29"), poll.getUpdateDate());

		questions = poll.getPollQuestions();
		assertNotNull(questions);
		assertEquals(3, questions.size());

		question = questions.get(0);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(0, question.getSequence());
		assertEquals("Si", question.getText());
		assertEquals(1, question.getVotes());

		question = questions.get(1);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(1, question.getSequence());
		assertEquals("No", question.getText());
		assertEquals(0, question.getVotes());

		question = questions.get(2);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(2, question.getSequence());
		assertEquals("Non lo so", question.getText());
		assertEquals(1, question.getVotes());

		nicks = poll.getVoterNicks();
		assertNotNull(nicks);
		assertEquals(2, nicks.size());
		assertTrue(nicks.contains("Sfigato"));
		assertTrue(nicks.contains("admin"));
	}

	@Test
	public void test_getPollsByLastVote() throws Exception {

		PollsDTO polls = persistence.getPollsByLastVote(99, 0);
		assertNotNull(polls);
		List<PollDTO> res = polls.getPolls();
		assertNotNull(res);
		assertEquals(2, res.size());

		PollDTO poll = res.get(0);
		assertEquals("admin", poll.getAuthor());
		assertEquals(getDateFromDatabaseString("2014-08-19 15:00:25"), poll.getCreationDate());
		assertEquals(2, poll.getId());
		assertEquals("Eh (newbie) ?", poll.getText());
		assertEquals("Sei perseo ?", poll.getTitle());
		assertEquals(getDateFromDatabaseString("2014-08-19 15:00:49"), poll.getUpdateDate());

		List<PollQuestion> questions = poll.getPollQuestions();
		assertNotNull(questions);
		assertEquals(2, questions.size());

		PollQuestion question = questions.get(0);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(0, question.getSequence());
		assertEquals("Trentaseo", question.getText());
		assertEquals(1, question.getVotes());

		question = questions.get(1);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(1, question.getSequence());
		assertEquals("Puppa !", question.getText());
		assertEquals(0, question.getVotes());

		List<String> nicks = poll.getVoterNicks();
		assertNotNull(nicks);
		assertEquals(1, nicks.size());
		assertTrue(nicks.contains("admin"));

		poll = res.get(1);
		assertEquals("Sfigato", poll.getAuthor());
		assertEquals(getDateFromDatabaseString("2014-08-19 14:59:21"), poll.getCreationDate());
		assertEquals(1, poll.getId());
		assertEquals("Sono utili ?", poll.getText());
		assertEquals("I sondaggi", poll.getTitle());
		assertEquals(getDateFromDatabaseString("2014-08-19 15:00:29"), poll.getUpdateDate());

		questions = poll.getPollQuestions();
		assertNotNull(questions);
		assertEquals(3, questions.size());

		question = questions.get(0);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(0, question.getSequence());
		assertEquals("Si", question.getText());
		assertEquals(1, question.getVotes());

		question = questions.get(1);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(1, question.getSequence());
		assertEquals("No", question.getText());
		assertEquals(0, question.getVotes());

		question = questions.get(2);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(2, question.getSequence());
		assertEquals("Non lo so", question.getText());
		assertEquals(1, question.getVotes());

		nicks = poll.getVoterNicks();
		assertNotNull(nicks);
		assertEquals(2, nicks.size());
		assertTrue(nicks.contains("Sfigato"));
		assertTrue(nicks.contains("admin"));
	}

	@Test
	public void test_getPoll() throws Exception {
		PollsDTO polls = persistence.getPollsByDate(99, 0);
		assertNotNull(polls);

		List<PollDTO> res = polls.getPolls();
		assertNotNull(res);
		assertEquals(2, res.size());

		PollDTO poll = res.get(0);
		assertEquals("admin", poll.getAuthor());
		assertEquals(getDateFromDatabaseString("2014-08-19 15:00:25"), poll.getCreationDate());
		assertEquals(2, poll.getId());
		assertEquals("Eh (newbie) ?", poll.getText());
		assertEquals("Sei perseo ?", poll.getTitle());
		assertEquals(getDateFromDatabaseString("2014-08-19 15:00:49"), poll.getUpdateDate());

		List<PollQuestion> questions = poll.getPollQuestions();
		assertNotNull(questions);
		assertEquals(2, questions.size());

		PollQuestion question = questions.get(0);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(0, question.getSequence());
		assertEquals("Trentaseo", question.getText());
		assertEquals(1, question.getVotes());

		question = questions.get(1);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(1, question.getSequence());
		assertEquals("Puppa !", question.getText());
		assertEquals(0, question.getVotes());

		List<String> nicks = poll.getVoterNicks();
		assertNotNull(nicks);
		assertEquals(1, nicks.size());
		assertTrue(nicks.contains("admin"));

		poll = res.get(1);
		assertEquals("Sfigato", poll.getAuthor());
		assertEquals(getDateFromDatabaseString("2014-08-19 14:59:21"), poll.getCreationDate());
		assertEquals(1, poll.getId());
		assertEquals("Sono utili ?", poll.getText());
		assertEquals("I sondaggi", poll.getTitle());
		assertEquals(getDateFromDatabaseString("2014-08-19 15:00:29"), poll.getUpdateDate());

		questions = poll.getPollQuestions();
		assertNotNull(questions);
		assertEquals(3, questions.size());

		question = questions.get(0);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(0, question.getSequence());
		assertEquals("Si", question.getText());
		assertEquals(1, question.getVotes());

		question = questions.get(1);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(1, question.getSequence());
		assertEquals("No", question.getText());
		assertEquals(0, question.getVotes());

		question = questions.get(2);
		assertEquals(poll.getId(), question.getPollId());
		assertEquals(2, question.getSequence());
		assertEquals("Non lo so", question.getText());
		assertEquals(1, question.getVotes());

		nicks = poll.getVoterNicks();
		assertNotNull(nicks);
		assertEquals(2, nicks.size());
		assertTrue(nicks.contains("Sfigato"));
		assertTrue(nicks.contains("admin"));

	}

}
