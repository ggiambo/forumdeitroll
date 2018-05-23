package com.forumdeitroll;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class LRACache<K, V> extends LinkedHashMap<K, V> {

	private final int sz;

	protected LRACache(final int size) {
		this.sz = size;
	}

	protected abstract V retrieve(K k);

	public V lookup(final K k) {
		V r;
		synchronized (this) {
			r = get(k);
		}

		if (r != null) return r;

		r = retrieve(k);
		synchronized (this) {
			put(k, r);
		}
		return r;
	}

	@Override
	protected boolean removeEldestEntry(final Map.Entry eldest) {
		return size() > sz;
	}

}
