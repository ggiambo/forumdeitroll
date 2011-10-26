package com.acmetoy.ravanator.fdt.fetcher;

import java.util.List;

import org.apache.log4j.Logger;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;

import com.acmetoy.ravanator.fdt.WebUtilities;

public class AuthorFetcherAvatar extends Thread {

	private static final Logger LOG = Logger.getLogger(MessageFetcherMetadata.class);
	private static final String AVATAR_URL_PI = "http://punto-informatico.it/community/avatar/";
	private static final String AVATAR_URL_FDT = "http://www.forumdeitroll.it/community/avatar/";

	private Element authorContainer;

	private byte[] avatar;

	AuthorFetcherAvatar(Element authorContainer) {
		this.authorContainer = authorContainer;
	}

	@Override
	public void run() {
		List<Element> elements = authorContainer.getAllElements(HTMLElementName.IMG);
		for (Element elem : elements) {
			String src = elem.getAttributeValue("src");
			if (src != null && (src.startsWith(AVATAR_URL_PI) || src.startsWith(AVATAR_URL_FDT))) {
				try {
					avatar = WebUtilities.getPageAsBytes(src);
				} catch (Exception e) {
					LOG.error("Cannot get avatar for " + src);
				}
				break;
			}
		}
	}

	public byte[] getAvatar() {
		return avatar;
	}

}
