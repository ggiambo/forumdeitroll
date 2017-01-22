package com.forumdeitroll.util;

import javax.servlet.http.HttpServletRequest;

public class VisitorCounters {
	public static UniqueVisitorsCounter count15min = new UniqueVisitorsCounter(15*60*1000);
	public static UniqueVisitorsCounter count5min = new UniqueVisitorsCounter(5*60*1000);
	public static UniqueVisitorsCounter count1min = new UniqueVisitorsCounter(1*60*1000);
	public static void add(HttpServletRequest request) {
		count15min.add(request);
		count5min.add(request);
		count1min.add(request);
	}
}
