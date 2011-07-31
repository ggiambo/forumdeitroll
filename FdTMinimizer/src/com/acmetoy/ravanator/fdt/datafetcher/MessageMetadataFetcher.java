package com.acmetoy.ravanator.fdt.datafetcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.WebUtilities;

public class MessageMetadataFetcher implements Runnable {
	
	private static final Logger LOG = Logger.getLogger(MessageMetadataFetcher.class);
	
	private long id;
	private CallBackClass callBackClass;
	
	private String author;
	private Date date;
	private long threadId;
	private long parentId;
	
	public MessageMetadataFetcher(long id, CallBackClass callBackClass) {
		this.id = id;
		this.callBackClass = callBackClass;
	}

	@Override
	public void run() {
		try {
			Source source = WebUtilities.getPage("http://www.forumdeitroll.it/HTM.aspx?m_id=" + id);
			
			for (Element elem : source.getAllElementsByClass("nick")) {
				if (elem.getAttributeValue("style") == null) {
					continue;
				}
				for (String styles : elem.getAttributeValue("style").split(";")) {
					String[] style = styles.trim().split(":");
					LOG.info("styles :" + styles);
					if (style.length == 2) {
						if ("background-color".equals(style[0].trim()) && "#a5ceff".equals(style[1].trim())) {
							// author
							String content = elem.getContent().toString();
							author = content.substring(0, content.indexOf("<")).trim();
							LOG.info("author :" + author);
							// date
							content = elem.getAllElementsByClass("data").get(0).getContent().toString();
							String stringDate = "";
							if (content.contains("alle")) {
								//  alle 11.31
								String firstPart = new SimpleDateFormat("MM/dd/yy").format(new Date());
								stringDate = firstPart + " " + content.replace("alle", "").trim();
							} else if (content.contains("il")) {
								// il 19/02/11 12.17
								stringDate = content.replace("il", "").trim();
							}
							LOG.info("stringDate :" + stringDate);
							date = new SimpleDateFormat("MM/dd/yy HH.mm").parse(stringDate);
							// parentId
						}
					}
				}
			}
			
			// threadId
			Element elem = source.getAllElementsByClass("textcapothread").get(0);
			String href = elem.getAttributeValue("href");
			int start = href.indexOf("=") + 1;
			int end = href.indexOf("&");
			threadId = Long.parseLong(href.substring(start, end));
			LOG.info("threadId :" + threadId);
			
			// parentId
			parentId = getParentId(id, threadId, source);	
			LOG.info("parentId :" + parentId);
			
			callBackClass.callBack(this);
			
		} catch (Exception e) {
			LOG.error(e);
		}
	}
	
	/**
	 * Complicato ! Non toccarlo o muori :) !
	 * @param id
	 * @param source
	 * @return
	 */
	private long getParentId(long id, long threadId, Source source) {
		List<Long[]> threadList = new ArrayList<Long[]>();
		
		Element container = source.getAllElementsByClass("threaditem").get(0).getChildElements().get(2);
		boolean analyzeNext = false;
		long prevDottos = -1;
		for (Element td : container.getAllElements("td")) {
			int dottos = 0;
			for (Element img : td.getChildElements()) {
				if ("img".equals(img.getName()) && "dotto".equals(img.getAttributeValue("class"))) {
					dottos++;
				}
			}
			if (dottos > 0) {
				prevDottos = dottos;
				analyzeNext = true;
			} else {
				if (analyzeNext) {
					String href = td.getAllElements("a").get(0).getAttributeValue("href");
					int start = href.indexOf("=") + 1;
					int end = href.indexOf("&");
					long thisId = Long.parseLong(href.substring(start, end));
					if (id == thisId) {
						// search
						for (int i = threadList.size() - 1; i > -1; --i) {
							Long[] elem = threadList.get(i);
							if (elem[0] < prevDottos) {
								return elem[1];
							}
						}
					} else {
						threadList.add(new Long[] {prevDottos, thisId});
					}
				}
				analyzeNext = false;
			}
		}
		return threadId;
	}
 	
	public String getAuthor() {
		return author;
	}

	public Date getDate() {
		return date;
	}

	public long getThreadId() {
		return threadId;
	}

	public long getParentId() {
		return parentId;
	}

}
