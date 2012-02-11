package com.acmetoy.ravanator.fdt;

import org.apache.log4j.Logger;

public abstract class SingleValueCache<V> {
	protected volatile V v = null;
	protected final long interval;
	protected volatile long timestamp = -1;

	private static final Logger LOG = Logger.getLogger(SingleValueCache.class);

	public SingleValueCache(final long interval) {
		this.interval = interval;
	}

	protected abstract V update();

	protected boolean expired() {
		return (v == null) || ((System.currentTimeMillis() - timestamp) > interval);
	}

	public V get() {
		if (expired()) {
			synchronized (this) {
				if (expired()) {
					LOG.info(getClass().getName() + ": Reloading");
					timestamp = System.currentTimeMillis();
					V newv = update();
					v = newv;
				}
			}
		}

		return v;
	}
}