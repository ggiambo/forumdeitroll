package com.forumdeitroll.servlets;

import java.awt.Color;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.PollDTO;
import com.forumdeitroll.persistence.PollQuestion;
import com.forumdeitroll.persistence.PollsDTO;
import com.forumdeitroll.servlets.Action.Method;

public class Polls extends MainServlet {

	private static final long serialVersionUID = 1L;

	private static final int MIN_TITLE_LENGTH = 5;
	
	private static int CHART_WIDTH = 500;
	
	public static final int MAX_TITLE_LENGTH = 40;
	
	private static final int MAX_QUESTIONS = 10;
	
	public static final int MAX_QUESTION_LENGTH = 65;

	public static final int MIN_TEXT_LENGTH = 5;

	public static final int MAX_TEXT_LENGTH = 1000;
	
	private Color[] pieSliceColors = new Color[] {
			new Color(248, 215, 83),
			new Color(92, 151, 70),
			new Color(62, 117, 167),
			new Color(122, 101, 62),
			new Color(225, 102, 42),
			new Color(116, 121, 111),
			new Color(196, 56, 79)
		};
	
	@Action
	@Override
	String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return getByPage(req, res);
	}
	
	/**
	 * I polls di questa pagina (Dimensione PAGE_SIZE) in ordine di data
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action(method=Method.GET)
	String getByPage(HttpServletRequest req, HttpServletResponse res) throws Exception {
		PollsDTO polls = getPersistence().getPollsByDate(PAGE_SIZE, getPageNr(req));
		req.setAttribute("polls", polls.getPolls());
		req.setAttribute("totalSize", polls.getMaxNrOfPolls()); // TODO
		setWebsiteTitle(req, "Sondaggi @ Forum dei Troll");
		return "polls.jsp";
	}
	
	/**
	 * I polls di questa pagina (Dimensione PAGE_SIZE) in ordine di ultimo voto
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@Action(method=Method.GET)
	String getByLastVote(HttpServletRequest req, HttpServletResponse res) throws Exception {
		PollsDTO polls = getPersistence().getPollsByLastVote(PAGE_SIZE, getPageNr(req));
		req.setAttribute("polls", polls.getPolls());
		req.setAttribute("totalSize", polls.getMaxNrOfPolls()); // TODO
		setWebsiteTitle(req, "Sondaggi @ Forum dei Troll");
		return "polls.jsp";
	}
	
	/**
	 * Vai alla pagina per creare un nuovo poll
	 */
	@Action(method=Method.GET)
	String createNewPoll(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO user = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (user == null || !user.isValid()) {
			setNavigationMessage(req, NavigationMessage.error("Solo gli utenti registrati possono creare un sondaggio."));
			return getByPage(req, res);
		}
		req.setAttribute("questions", getBaseQuestions());
		
		return "newPoll.jsp";
	}
	
	/**
	 * Inserisci il poll
	 */
	@Action(method=Method.POST)
	String insertPoll(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO user = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (user == null || !user.isValid()) {
			setNavigationMessage(req, NavigationMessage.error("No registrato ? No sondaggio !"));
			return getByPage(req, res);
		}
		
		// validazione
		boolean isInError = false;
		String title = req.getParameter("title");
		req.setAttribute("title", title);
		if (title == null || title.length() < MIN_TITLE_LENGTH || title.length() > MAX_TITLE_LENGTH) {
			setNavigationMessage(req, NavigationMessage.error("Un titolino tra i 5 e i 140 caratteri &egrave; ben accetto !"));
			isInError = true;
		}
		String text = req.getParameter("text");
		req.setAttribute("text", text);
		if (text == null || text.length() < MIN_TEXT_LENGTH || title.length() > MAX_TEXT_LENGTH) {
			setNavigationMessage(req, NavigationMessage.error("Riesci a scrivere una descrizione descrizione tra i 5 e i 1000 caratteri ?"));
			isInError = true;
		}
		List<PollQuestion> questions = new ArrayList<PollQuestion>(MAX_QUESTIONS);
		while (true) {
			String question = req.getParameter("question_" + questions.size());
			if (StringUtils.isEmpty(question)) {
				break;
			}
			if (question.length() > 255) {
				setNavigationMessage(req, NavigationMessage.error("La domanda " + (questions.size() + 1) + " dev'essere pi&ugrave; corta di 256 caratteri."));
			}
			PollQuestion pollQuestion = new PollQuestion();
			pollQuestion.setSequence(questions.size());
			pollQuestion.setText(question);
			questions.add(pollQuestion);
		}
		// minimo due risposte
		if (questions.size() < 2) {
			while (questions.size() < 2) {
				questions.add(new PollQuestion());
			}
			setNavigationMessage(req, NavigationMessage.error("Un sondaggio ha al minimo di due risposte ..."));
			isInError = true;
		}
		if (questions.size() > MAX_QUESTIONS) {
			setNavigationMessage(req, NavigationMessage.error("Non piu' di 10 domande, pliiis !"));
			isInError = true;
		}
		
		if (isInError) {
			req.setAttribute("title", title);
			req.setAttribute("text", text);
			req.setAttribute("questions", questions);
			return "newPoll.jsp";
		}

		PollDTO pollDTO = new PollDTO();
		pollDTO.setAuthor(user.getNick());
		pollDTO.setTitle(title);
		pollDTO.setText(text);
		pollDTO.setPollQuestions(questions);
		
		long pollId = getPersistence().createPoll(pollDTO);
		
		// comunichiamo al mondo l'esistenza di un nuovo poll =) !
		MessageDTO msg = new MessageDTO();
		msg.setDate(new Date());
		msg.setForum("Sondaggi trolleschi");
		msg.setSubject("Nuovo sondaggio proposto da " + user.getNick()) ;
		StringBuilder msgText = new StringBuilder();
		msgText.append(user.getNick()).append(" ha creato un nuovo sondaggio dal titolo '").append(title);
		msgText.append("', clicka [url=");
		msgText.append("Polls?action=getPollContent&pollId=").append(pollId);
		msgText.append("]qui[/url] per visualizzarlo");
		msg.setText(msgText.toString());
		getPersistence().insertMessage(msg);
		
		return getByPage(req, res);
	}

	/**
	 * Scrive la chart nella response
	 */
	@Action(method=Method.GET)
	String getChart(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		long pollId = Long.parseLong(req.getParameter("pollId"));
		PollDTO poll = getPersistence().getPoll(pollId);
		
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (PollQuestion q : poll.getPollQuestions()) {
			if (q.getVotes() != 0) {
				dataset.setValue(q.getText(), q.getVotes());
			}
		}
		
		JFreeChart chart = ChartFactory.createPieChart3D(null, dataset, true, false, false);
		PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setForegroundAlpha(0.60f);
        plot.setCircular(true);
        plot.setBackgroundPaint(new Color(214, 214, 214)); // #D6D6D6
        plot.setOutlinePaint(new Color(169, 169, 169)); // #A9A9A9
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("Voti: {1} ({2})", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()));
        
        // renderizza con i colori custom
        List datasetKeys = dataset.getKeys();
        for (int i = 0; i < datasetKeys.size(); i++) {
        	plot.setSectionPaint((Comparable)datasetKeys.get(i), pieSliceColors[(i % pieSliceColors.length)]);
        }
        
        // write chart
		res.setContentType("image/png");
		res.setHeader("Cache-Control", "no-cache");
		res.setHeader("Pragma", "no-cache");
		res.setHeader("Expires", "-1");
		OutputStream outputStream = res.getOutputStream();
		ChartUtilities.writeChartAsPNG(outputStream, chart, CHART_WIDTH, 300);

		return null;
	}
	
	/**
	 * Ritorna il contenuto del poll
	 */
	@Action(method=Method.GET)
	String getPollContent(HttpServletRequest req, HttpServletResponse res) throws Exception {
		long pollId = Long.parseLong(req.getParameter("pollId"));
		PollDTO poll = getPersistence().getPoll(pollId);
		req.setAttribute("poll", poll);
		
		// sanitized pollText
		String pollText = StringEscapeUtils.escapeXml(poll.getText()).replaceAll("\n", "<br/>");
		req.setAttribute("pollText", pollText);
		
		// can vote ?
		AuthorDTO author = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		boolean canVote = true;
		if (author != null && author.isValid()) {
			for (String nick : poll.getVoterNicks()) {
				if (nick.equals(author.getNick())) {
					canVote = false;
					break;
				}
			}
		} else {
			canVote = false;
		}
		req.setAttribute("canVote", canVote);

		return "pollContent.jsp";
	}
	
	/**
	 * Rsipondi al poll
	 */
	@Action(method=Method.POST)
	String answerPoll(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		AuthorDTO author = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (author == null || !author.isValid()) {
			setNavigationMessage(req, NavigationMessage.error("No registrato ? No sondaggio !"));
			return getPollContent(req, res);
		}
		
		long pollId = Long.parseLong(req.getParameter("pollId"));
		int pollSequence = Integer.parseInt(req.getParameter("pollSequence"));
		
		PollQuestion pollQuestion = new PollQuestion();
		pollQuestion.setPollId(pollId);
		pollQuestion.setSequence(pollSequence);
		
		if (!getPersistence().updatePollQuestion(pollQuestion, author)) {
			setNavigationMessage(req, NavigationMessage.error("Un troll, un voto !"));
		}
		
		return getPollContent(req, res);
	}
	
	private List<PollQuestion> getBaseQuestions() {
		// almeno due domande di default
		List<PollQuestion> questions = new ArrayList<PollQuestion>(2);
		questions.add(new PollQuestion());
		questions.add(new PollQuestion());
		return questions;
	}
	
}
