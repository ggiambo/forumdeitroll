package com.forumdeitroll.markup.util;

//DEBUG class
public class Section {
	private char[] c;
	private int offset, length;
	public Section(char[] c, int offset, int length) {
		this.c = c;
		this.offset = offset;
		this.length = length;
	}
	
	@Override
	public String toString() {
		return new String(c, offset, length);
	}
}
