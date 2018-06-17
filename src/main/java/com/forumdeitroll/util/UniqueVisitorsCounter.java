package com.forumdeitroll.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class UniqueVisitorsCounter {
	private ConcurrentHashMap<String, Date> data = new ConcurrentHashMap<>();

	private long timeout;
	UniqueVisitorsCounter(long timeout) {
		this.timeout = timeout;
	}

	private void cleanup() {
		Date then = new Date(new Date().getTime() - timeout);
		for (Map.Entry<String, Date> entry : data.entrySet()) {
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
