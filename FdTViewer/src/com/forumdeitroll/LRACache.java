package com.forumdeitroll;

import java.util.Map;
import java.util.LinkedHashMap;

public abstract class LRACache<K, V> extends LinkedHashMap<K, V> {
	protected final int sz;

	protected static final float LOAD = 0.25f;

	public LRACache(final int size) {
		this.sz = size;
	}

	protected abstract V retrieve(K k);

	public V lookup(final K k) {
		V r = null;
		synchronized(this) {
			r = get(k);
		}

		if (r != null) return r;

		r = retrieve(k);
		synchronized(this) {
			put(k, r);
		}
		return r;
	}

	@Override protected boolean removeEldestEntry(final Map.Entry eldest) {
		return size() > sz;
	}

}
