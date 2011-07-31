package com.acmetoy.ravanator.fdt.datafetcher;

import java.util.List;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.WebUtilities;
import com.acmetoy.ravanator.fdt.persistence.AuthorPersistence;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

public class MessageBodyFetcher implements Runnable {
	
	private long id;
	private CallBackClass callBackClass;

	private String subject;
	private String text;
	
	public MessageBodyFetcher(long id, CallBackClass callBackClass) {
		this.id = id;
		this.callBackClass = callBackClass;
	}

	@Override
	public void run() {
		try {
			Source source;
			source = WebUtilities.getPage("http://www.forumdeitroll.it/m.aspx?m_id=" + id);
			// set text
			Element elem = source.getAllElementsByClass("funcbarthread").get(0);
			text = elem.getAllElements("div").get(0).getContent().toString();
			// set subject
			elem = source.getAllElementsByClass("fh1").get(0);
			List<Element> elements = elem.getAllElements("b");
			if (elements.size() == 1) {
				subject = elements.get(0).getContent().toString();
			} else {
				subject = null;
			}
			// if author is not in database, insert it
			Element authorContainer = source.getAllElementsByClass("fh4").get(0);
			elements = authorContainer.getAllElementsByClass("nick");
			if (elements.size() == 1) {
				String nick = elements.get(0).getAllElements("a").get(0).getContent().toString();
				if (!AuthorPersistence.getInstance().hasAuthor(nick)) {
					new AuthorFetcherCallBack(authorContainer, nick).run();
				}
			} 
			callBackClass.callBack(this);
		} catch (Exception e) {
			Logger.getRootLogger().error(e);
		}
	}
	
	public String getText() {
		return text;
	}
	
	public String getSubject() {
		return subject;
	}

}
