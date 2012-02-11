package com.acmetoy.ravanator.fdt;

public abstract class SingleValueCache<V> {
	protected volatile V v = null;
	protected final long interval;
	protected volatile long timestamp = -1;

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
					timestamp = System.currentTimeMillis();
					V newv = update();
					v = newv;
				}
			}
		}

		return v;
	}
}