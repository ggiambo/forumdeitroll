package com.forumdeitroll.util;

import java.util.Map;
import java.util.HashMap;

public class Ratelimiter<T> {
	protected final Map<T, Info> map = new HashMap<T, Info>();
	protected final int limitTime;
	protected final int limitNumber;

	public Ratelimiter(final int limitTime, final int limitNumber) {
		this.limitTime = limitTime;
		this.limitNumber = limitNumber;
	}

	public synchronized void increment(final T key) {
		cleanup();

		Info info = map.get(key);
		if (info == null) {
			info = new Info(System.currentTimeMillis());
			map.put(key, info);
		}

		// dato che abbiamo chiamato cleanup sopra siamo sicuri che questa entry e` fresca
		// ovvero: (System.currentTimeMillis() - info.time <= limitTime)
		++(info.count);
		info.time = System.currentTimeMillis();
	}

	public synchronized boolean limited(final T key) {
		final Info info = map.get(key);
		if (info == null) return false;
		if (info.count < limitNumber) return false; // troppi pochi tentativi
		if (System.currentTimeMillis() - info.time > limitTime) return false; // passato abbastanza tempo dall'ultimo tentativo
		return true;
	}

	protected synchronized void cleanup() {
		final long now = System.currentTimeMillis();
		for (final Map.Entry<T, Info> entry: map.entrySet()) {
			if (now - entry.getValue().time > limitTime) {
				map.remove(entry.getKey());
			}
		}
	}

	protected static class Info {
		protected long time;
		protected int count;

		public Info(final long time) {
			this.time = time;
			this.count = 0;
		}
	}
}