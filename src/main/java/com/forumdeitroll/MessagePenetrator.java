package com.forumdeitroll;

import com.forumdeitroll.markup.InputSanitizer;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.DAOFactory;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.servlets.Messages;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
Classe di supporto per l'inserimento dei messaggi nel database

TODO:
- Modificare ProfilerAPI per non avere come argomento HttpServletRequest in modo che anche JSonServlet.addMessage possa usarla
*/
public class MessagePenetrator {
	private String forum;
	private long parentId = -1;
	private String subject;
	private String text;
	private AuthorDTO author;
	private String fakeAuthor;
	private String type;

	private String error;

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

	public MessagePenetrator setCredentials(final AuthorDTO loggedUser, final String nick, final String pass, final String captcha) {
		if (error != null) return this;

		if (!ReCaptchaUtils.verifyReCaptcha(captcha)) {
			setError("Verifica captcha fallita");
			return this;
		}

		if (loggedUser != null && loggedUser.getNick() != null && loggedUser.getNick().equalsIgnoreCase(nick)) {
			// posta come utente loggato
			author = loggedUser;
			return this;
		}

		if (StringUtils.isEmpty(pass) && StringUtils.isNotEmpty(nick) && !DAOFactory.getMessagesDAO().getAuthor(nick).isValid() && ((loggedUser != null))) {
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

		// posta da anonimo
		author = new AuthorDTO(loggedUser);
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
			Long id;
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

		if (!author.isEnabled()) {
			setError("Non sei ancora abilitato, abbi pazienza !");
			return null;
		}

		MessageDTO msg;

		if ("edit".equals(type)) {
			msg = DAOFactory.getMessagesDAO().getMessage(parentId);
			if (msg.getAuthor() == null || !msg.getAuthor().getNick().equals(author.getNick())) {
				setError("Imbroglione, non puoi modificare questo messaggio !");
				return null;
			}

			text += "<BR><BR><b>**Modificato dall'autore il " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()) + "**</b>";
			msg.setText(text);
			msg.setSubject(subject);
		} else {
			msg = new MessageDTO();
			msg.setAuthor(author);
			msg.setFakeAuthor(fakeAuthor);
			msg.setParentId(parentId);
			msg.setDate(new Date());
			msg.setText(text);
			msg.setSubject(subject);

			if ("reply".equals(type)) {
				MessageDTO replyMsg = DAOFactory.getMessagesDAO().getMessage(parentId);
				msg.setForum(replyMsg.getForum());
				msg.setThreadId(replyMsg.getThreadId());
				// incrementa il numero di messaggi scritti
				if (author.isValid()) {
					author.setMessages(author.getMessages() + 1);
					DAOFactory.getAuthorsDAO().updateAuthor(author);
				}

			} else if ("new".equals(type)) {
				if (StringUtils.isEmpty(forum)) {
					forum = null;
				} else {
					forum = InputSanitizer.sanitizeForum(forum);
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
