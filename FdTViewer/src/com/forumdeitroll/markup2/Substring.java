package com.forumdeitroll.markup2;

/**
Java 7 ha rotto String.substring di conseguenza questa classe e` necessaria.
Java e` una merda.
*/
class Substring implements CharSequence {
	final char[] buf;
	int start;
	int end;

	public Substring(final String s) {
		this(s, 0);
	}

	public Substring(final String s, final int start) {
		this.buf = s.toCharArray();
		this.start = start;
		this.end = this.buf.length;
	}

	public Substring(final char[] buf, final int start, final int end) {
		this.buf = buf;
		this.start = start;
		this.end = end;
	}

	public char charAt(int i) {
		return buf[start + i];
	}

	public int length() {
		return end - start;
	}

	public CharSequence subSequence(int start, int end) {
		return new Substring(buf, this.start + start, this.start + end);
	}

	public String toString() {
		return new String(buf, start, end-start);
	}

	public boolean startsWithAt(final String s, int z) {
		for (int i = 0; i < s.length(); i++) {
			if (z + i >= end) {
				return false;
			}

			if (buf[z + i] != s.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean startsWithIAt(final String s, int z) {
		for (int i = 0; i < s.length(); i++) {
			if (z + i >= end) {
				return false;
			}

			if (Character.toLowerCase(buf[z + i]) != s.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean startsWith(final String s) {
		return startsWithAt(s, start);
	}

	public boolean startsWithI(final String s) {
		return startsWithIAt(s, start);
	}

	public Substring startsWithAndAdvance(final String s) {
		if (startsWith(s)) {
			final Substring r = new Substring(buf, start, start + s.length());
			start += s.length();
			return r;
		}
		return null;
	}

	public Substring startsWithIAndAdvance(final String s) {
		if (startsWithI(s)) {
			final Substring r = new Substring(buf, start, start + s.length());
			start += s.length();
			return r;
		}
		return null;
	}

	public int advanceTo(final String s) {
		for (int i = start; i < end; ++i) {
			if (startsWithAt(s, i)) {
				start = i + s.length();
				return i;
			}
		}
		return start;
	}

	public void advanceToLinkEnd() {
		for (int i = start; i < end; ++i) {
			if (Character.isWhitespace(buf[i]) || (buf[i] == '[') || (buf[i] == '<')) {
				start = i;
				return;
			}
		}
		start = end;
	}
}
