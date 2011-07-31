package com.acmetoy.ravanator.fdt.datafetcher;

import java.util.List;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.WebUtilities;

import net.htmlparser.jericho.Element;

public class AuthorAvatarFetcher implements Runnable {
	
	private static final String AVATAR_URL_PI = "http://punto-informatico.it/community/avatar/";
	private static final String AVATAR_URL_FDT = "http://www.forumdeitroll.it/community/avatar/";

	private Element authorContainer;
	private CallBackClass callBackClass;

	private byte[] avatar;

	AuthorAvatarFetcher(Element authorContainer, CallBackClass callBackClass) {
		this.authorContainer = authorContainer;
		this.callBackClass = callBackClass;
	}

	@Override
	public void run() {
		try {
			List<Element> elements = authorContainer.getAllElements("img");
			for (Element elem : elements) {
				String src = elem.getAttributeValue("src");
				if (src != null && (src.startsWith(AVATAR_URL_PI) || src.startsWith(AVATAR_URL_FDT))) {
					avatar = WebUtilities.getPageAsBytes(src);
					break;
				}
			}
			callBackClass.callBack(this);
		} catch (Exception e) {
			Logger.getRootLogger().error(e);
		}
	}

	public byte[] getAvatar() {
		return avatar;
	}

}
