package com.forumdeitroll;

public abstract class SingleValueCache<V> {

	protected volatile V v = null;
	private final long interval;
	protected volatile long timestamp = -1;

	protected SingleValueCache(final long interval) {
		this.interval = interval;
	}

	protected abstract V update();

	private boolean expired() {
		return (v == null) || ((System.currentTimeMillis() - timestamp) > interval);
	}

	public void invalidate() {
		v = null;
	}

	public V get() {
		if (expired()) {
			synchronized (this) {
				if (expired()) {
					//LOG.info(getClass().getName() + ": Reloading");
					timestamp = System.currentTimeMillis();
					v = update();
				}
			}
		}

		return v;
	}
}