package com.forumdeitroll.util;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;


public class UniqueVisitorsCounter {
	private ConcurrentHashMap<String, Date> data =
		new ConcurrentHashMap<String, Date>();
	
	private long timeout = 0;
	public UniqueVisitorsCounter(long timeout) {
		this.timeout = timeout;
	}
	
	private void cleanup() {
		Date then = new Date(new Date().getTime() - timeout);
		for (Iterator<Map.Entry<String, Date>> it = data.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Date> entry = it.next();
			if (entry.getValue().before(then)) {
				data.remove(entry.getKey());
			}
		}
	}
	
	public int get() {
		cleanup();
		return data.size();
	}
	
	private void add(String ip) {
		Date now = new Date();
		data.put(ip, now);
		cleanup();
	}
	
	public void add(HttpServletRequest req) {
		String ip = req.getHeader("X-Forwarded-For") != null
			? req.getHeader("X-Forwarded-For")
			: req.getRemoteAddr();
		add(ip);
	}
	
}
