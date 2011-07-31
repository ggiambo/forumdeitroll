package com.acmetoy.ravanator.fdt.datafetcher;

import net.htmlparser.jericho.Element;

import com.acmetoy.ravanator.fdt.persistence.AuthorPersistence;

public class AuthorFetcherCallBack  implements Runnable, CallBackClass {
	
	private Element authorContainer;
	private int count = 0;
	private String nick;
	
	private int ranking;
	private int messages;
	private byte[] avatar;
	
	AuthorFetcherCallBack(Element authorContainer, String nick) {
		this.authorContainer = authorContainer;
		this.nick = nick;
	}

	@Override
	public void run() {
		new Thread(new AuthorFetcher(authorContainer, this)).run();
		new Thread(new AuthorAvatarFetcher(authorContainer, this)).run();
	}

	@Override
	public void callBack(Object source) throws Exception {
		synchronized (this) {
			if (source instanceof AuthorFetcher) {
				AuthorFetcher authorFetcher = (AuthorFetcher) source;
				ranking = authorFetcher.getRanking();
				messages = authorFetcher.getMessages();
				count++; // expected 2 callback				
			} else if (source instanceof AuthorAvatarFetcher) {
				AuthorAvatarFetcher authorAvatarFetcher = (AuthorAvatarFetcher)source;
				avatar = authorAvatarFetcher.getAvatar();
				count++; // expected 2 callback
			} else {
				throw new Exception("Unknown class " + source.getClass().getName());
			}
			if (count == 2) {
				AuthorPersistence.getInstance().insertAuthor(nick, ranking, messages, avatar);
			}
		}

	}

}
