package com.acmetoy.ravanator.fdt;

import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;


public class Main {
	public static void main(String[] args) throws Exception {
		String threadId = "2593630";
		
		List<String[]> threadList = new ArrayList<String[]>();
		
		Source source = WebUtilities.getPage("http://www.forumdeitroll.it/HTM.aspx?m_id=2593639&m_rid=0");
		Element container = source.getAllElementsByClass("threaditem").get(0).getChildElements().get(2);
		boolean analyzeNext = false;
		String prevDottos = "";
		for (Element td : container.getAllElements("td")) {
			int dottos = 0;
			for (Element img : td.getChildElements()) {
				if ("img".equals(img.getName()) && "dotto".equals(img.getAttributeValue("class"))) {
					dottos++;
				}
			}
			if (dottos > 0) {
				prevDottos = ""+dottos;
				analyzeNext = true;
			} else {
				if (analyzeNext) {
					String href = td.getAllElements("a").get(0).getAttributeValue("href");
					int start = href.indexOf("=") + 1;
					int end = href.indexOf("&");
					String id = href.substring(start, end);
					if (threadId.equals(id)) {
						// search
						for (int i = threadList.size() - 1; i > -1; --i) {
							String[] elem = threadList.get(i);
							if (Integer.parseInt(elem[0]) < Integer.parseInt(prevDottos)) {
								System.out.println("Found: parent of " + threadId + " is " + elem[1]);
								return;
							}
						}
					} else {
						threadList.add(new String[] {prevDottos, id});
					}
				}
				analyzeNext = false;
			}
		}
	}
		
}
