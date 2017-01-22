package com.forumdeitroll;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.forumdeitroll.markup.InputSanitizer;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.DAOFactory;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.profiler2.ProfilerAPI;
import com.forumdeitroll.servlets.Messages;
import com.forumdeitroll.util.CacheTorExitNodes;
import com.forumdeitroll.util.IPMemStorage;

/**
Classe di supporto per l'inserimento dei messaggi nel database

TODO:
- Modificare ProfilerAPI per non avere come argomento HttpServletRequest in modo che anche JSonServlet.addMessage possa usarla
*/
public class MessagePenetrator {
	String forum;
	long parentId = -1;
	String subject;
	String text;
	private AuthorDTO author;
	String fakeAuthor;
	String type;
	String ip;
	HttpServletRequest req;
	boolean banned = false;
	boolean isBannedCalled = false;

	String error;

	public MessagePenetrator() {
	}

	void setError(final String error) {
		if (this.error == null) {
			this.error = error;
		}
	}

	public MessagePenetrator setForum(final String _forum) {
		if (error != null) return this;

		forum = InputSanitizer.sanitizeForum(StringUtils.defaultString(_forum));

		// qualcuno prova a creare un forum ;) ?
		if (!StringUtils.isEmpty(forum) && !DAOFactory.getMiscDAO().getForums().contains(forum)) {
			setError("Ma che cacchio di forum e' '" + forum + "' ?!?");
		}

		return this;
	}

	public MessagePenetrator setParentId(final String _parentId) {
		if (error != null) return this;

		try {
			parentId = Long.parseLong(_parentId);
		} catch (NumberFormatException e) {
			setError("Il valore " + _parentId + " assomiglia poco a un numero…");
			return this;
		}

		return this;
	}

	public MessagePenetrator setSubject(final String _subject) {
		if (error != null) return this;

		subject = InputSanitizer.sanitizeSubject(StringUtils.defaultString(_subject));

		if (StringUtils.isEmpty(subject) || subject.trim().length() < 3) {
			setError("Oggetto di almeno di 3 caratteri, cribbio !");
			return this;
		}

		if (subject.length() > Messages.MAX_SUBJECT_LENGTH) {
			setError("LOL oggetto piu' lungo di " + Messages.MAX_SUBJECT_LENGTH + " caratteri !");
			return this;
		}

		return this;
	}

	public MessagePenetrator setText(final String _text) {
		if (error != null) return this;

		text = InputSanitizer.sanitizeText(_text);

		// testo di almeno di 5 caratteri ...
		if (StringUtils.isEmpty(text) || text.length() < 5) {
			setError("Un po di fantasia, scrivi almeno 5 caratteri ...");
			return this;
		}

		// testo al massimo di 10000 caratteri ...
		if (text.length() > Messages.MAX_MESSAGE_LENGTH) {
			setError("Sei piu' logorroico di una Wakka, stai sotto i " + Messages.MAX_MESSAGE_LENGTH + " caratteri !");
			return this;
		}

		return this;
	}

	public MessagePenetrator setCredentials(final AuthorDTO loggedUser, final String nick, final String pass, final String captcha, final String correctAnswer) {
		if (error != null) return this;

		if (loggedUser != null && loggedUser.getNick() != null && loggedUser.getNick().equalsIgnoreCase(nick)) {
			// posta come utente loggato
			author = loggedUser;
			return this;
		}

		if (StringUtils.isEmpty(pass) && StringUtils.isNotEmpty(nick) && !DAOFactory.getMessagesDAO().getAuthor(nick).isValid() && ((loggedUser != null) || correctAnswer.equals(captcha))) {
			// post anonimo con fakeAuthor
			author = new AuthorDTO(loggedUser);
			fakeAuthor = nick;
			return this;
		}

		if ((loggedUser != null) && StringUtils.isEmpty(nick)) {
			// utente loggato che posta come anonimo
			author = new AuthorDTO(loggedUser);
			return this;
		}

		if (StringUtils.isNotEmpty(nick) && StringUtils.isNotEmpty(pass)) {
			AuthorDTO sockpuppet = DAOFactory.getMessagesDAO().getAuthor(nick);
			if (PasswordUtils.hasUserPassword(sockpuppet, pass)) {
				// posta come altro utente
				author = sockpuppet;
				return this;
			}
		}

		// se non e` stato inserito nome utente/password e l'utente non e` loggato
		if (StringUtils.isNotEmpty(correctAnswer) && correctAnswer.equals(captcha)) {
			// posta da anonimo
			author = new AuthorDTO(loggedUser);
			return this;
		}

		setError("Autenticazione/verifica captcha fallita");
		return this;
	}

	public MessagePenetrator setAnonymous() {
		if (error != null) return this;
		if (author == null) return this;
		author = new AuthorDTO(author);
		return this;
	}

	public MessagePenetrator setType(final String _type) {
		if (error != null) return this;
		if (type != null) {
			type = null;
			return this;
		}

		type = _type;

		if (type.equals("quote")) {
			type = "reply";
		}

		final boolean ok = type.equals("new") || type.equals("edit") || type.equals("reply");
		if (!ok) {
			setError("Tipo " + type + " non valido");
			return this;
		}
		return this;
	}

	public MessagePenetrator deduceType(final String _id) {
		if (error != null) return this;
		if (type != null) {
			type = null;
			return this;
		}

		if (parentId > 0) {
			Long id = null;
            try {
                id = Long.parseLong(_id);
            } catch (NumberFormatException e) {
                setError("Sono consufo, non riesco a interpretare l'id '" + _id + "'");
                return this;
            }
			if (id > -1) {
				parentId = id;
				type = "edit";
			} else {
				type = "reply";
			}
		} else {
			type = "new";
		}

		return this;
	}

	protected boolean authorIsBanned(final AuthorDTO author, final Object sessionIsBanned, final String ip, HttpServletRequest req) {
		if (author.isBanned()) return true;
		if (sessionIsBanned != null) return true;

		this.ip = ip;
		this.req = req;

		if (ip == null) {
			setError("No IP?!");
			return false;
		}

		if (Messages.BANNED_IPs.contains(ip)) return true;

		// check se ANOnimo usa TOR
		if (!author.isValid()) {
			if (DAOFactory.getAdminDAO().blockTorExitNodes()) {
				if (CacheTorExitNodes.check(ip)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isBanned(final Object sessionIsBanned, final String ip, final HttpServletRequest req) {
		if (error != null) return false;
		isBannedCalled = true;
		if (author == null) return false;
		if (authorIsBanned(author, sessionIsBanned, ip, req)) {
			banned = true;
			return true;
		}
		if (req != null) {
			try {
				if (ProfilerAPI.blockedByRules(req, author)) {
					banned = true;
					return true;
				}
			} catch (Exception e) {
				Logger.getLogger(MessagePenetrator.class).error("ERRORE IN PROFILAZIONE!! "+e.getClass().getName() + ": "+ e.getMessage(), e);
				setError("Errore durante l'inserimento del messaggio. La suora sa perché.");
				return false;
			}
		}
		return false;
	}

	public MessageDTO penetrate() {
		if (error != null) return null;
		if (forum == null) {
			setError("Errore interno: chiamare setForum");
			return null;
		}
		if (subject == null) {
			setError("Errore interno: chiamare setSubject");
			return null;
		}
		if (text == null) {
			setError("Errore interno: chiamare setText");
			return null;
		}
		if (author == null) {
			setError("Errore interno: chiamare setCredentials");
			return null;
		}
		if (type == null) {
			setError("Errore interno: chiamare setType o deduceType");
			return null;
		}
		if (!isBannedCalled) {
			setError("Errore interno: chiamare isBanned");
			return null;
		}

		if (!author.isEnabled()) {
			setError("Non sei ancora abilitato, abbi pazienza !");
			return null;
		}

		MessageDTO msg = null;

		if (type == "edit") {
			msg = DAOFactory.getMessagesDAO().getMessage(parentId);
			if (msg.getAuthor() == null || !msg.getAuthor().getNick().equals(author.getNick())) {
				setError("Imbroglione, non puoi modificare questo messaggio !");
				return null;
			}

			text += "<BR><BR><b>**Modificato dall'autore il " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()) + "**</b>";
			msg.setText(text);
			if (!banned) {
				msg.setSubject(subject);
			}
		} else {
			msg = new MessageDTO();
			msg.setAuthor(author);
			msg.setFakeAuthor(fakeAuthor);
			msg.setParentId(parentId);
			msg.setDate(new Date());
			msg.setText(text);
			if (banned) msg.setIsVisible(-1);
			msg.setSubject(subject);

			if (type == "reply") {
				MessageDTO replyMsg = DAOFactory.getMessagesDAO().getMessage(parentId);
				msg.setForum(replyMsg.getForum());
				msg.setThreadId(replyMsg.getThreadId());
				// incrementa il numero di messaggi scritti
				if (author.isValid()) {
					author.setMessages(author.getMessages() + 1);
					DAOFactory.getAuthorsDAO().updateAuthor(author);
				}

				if (banned) {
					msg.setSubject(replyMsg.getSubjectReal());
				}
			} else if (type == "new") {
				if (StringUtils.isEmpty(forum)) {
					forum = null;
				} else {
					forum = InputSanitizer.sanitizeForum(forum);
				}
				if (banned) {
					forum = DAOFactory.FORUM_ASHES;
				}
				msg.setForum(forum);
				msg.setThreadId(-1);
				// incrementa il numero di messaggi scritti
				if (author.isValid()) {
					author.setMessages(author.getMessages() + 1);
					DAOFactory.getAuthorsDAO().updateAuthor(author);
				}
			}
		}

		msg = DAOFactory.getMessagesDAO().insertMessage(msg);
		final String m_id = Long.toString(msg.getId());
		IPMemStorage.store(ip, m_id, author);
		if (req != null) {
			ProfilerAPI.log(req, author, "message-post-" + m_id);
		}

		return msg;
	}

	public String Error() {
		return error;
	}

	public boolean isAuthorDisabled() {
		if (author == null) {
			return false;
		}
		return !author.isEnabled();
	}
}
