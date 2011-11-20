package com.acmetoy.ravanator.fdt.fetcher;

import net.htmlparser.jericho.Element;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.PersistenceFactory;

public class AuthorFetcher extends Thread {

	private static final Logger LOG = Logger.getLogger(AuthorFetcher.class);

	private Element authorContainer;

	private String nick;

	public AuthorFetcher(Element authorContainer, String nick) {
		this.authorContainer = authorContainer;
		this.nick = nick;
	}

	@Override
	public void run() {
		AuthorFetcherAvatar avatar = new AuthorFetcherAvatar(authorContainer);
		avatar.start();
		AuthorFetcherData data = new AuthorFetcherData(authorContainer);
		data.start();
		// wait for threads to end
		try {
			avatar.join();
		} catch (Exception e) {
			LOG.error("avatar()", e);
			return;
		}
		try {
			data.join();
		} catch (Exception e) {
			LOG.error("data.join()", e);
			return;
		}

		AuthorDTO author = new AuthorDTO();
		author.setNick(nick);
		author.setRanking(data.getRanking());
		author.setMessages(data.getMessages());
		author.setAvatar(avatar.getAvatar());
		
		StringBuilder log = new StringBuilder("Persisting author [");
		log.append(author.toString());
		log.append("]");
		LOG.info(log.toString());
		try {
			PersistenceFactory.getInstance().insertUpdateAuthor(author);
		} catch (Exception e) {
			LOG.error("Cannot persist author " + nick, e);
		}
	}

}
