package com.forumdeitroll.markup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.forumdeitroll.markup.util.Chars;

public class RenderIO {
	public Reader in;
	public Writer out;
	
	public char[] buffer;
	public int count;
	
	public RenderIO(Reader in, Writer out, int bufsize) {
		this.in = new BufferedReader(in);
		this.out = out;
		buffer = new char[bufsize];
	}
	
	public void write(String s) throws IOException {
		out.write(s);
	}
	public void write(char[] c) throws IOException {
		out.write(c);
	}
	public void write(char[] c, int offset, int length) throws IOException {
		out.write(c, offset, length);
	}
	
	public boolean load() throws IOException {
		in.mark(buffer.length);
		count = in.read(buffer);
		return count != -1;
	}
	
	public void skip(int n) throws IOException {
		in.reset();
		for (int i = 0; i < n; i++)
			in.read();
	}
	
	public void copy(int n) throws IOException {
		out.write(buffer, 0, n);
	}

	public void copy(int offset, int length) throws IOException {
		out.write(buffer, offset, length);
	}
	
	public int indexOf(char[] c, int fromIndex) {
		return Chars.indexOf(buffer, 0, count, c, 0, c.length, fromIndex, false, false);
	}
	
	public boolean startWith(char[] c) {
		return Chars.indexOf(buffer, 0, count, c, 0, c.length, 0, false, true) == 0;
	}
	public boolean startWith(char[] c, int offset, int length) {
		return Chars.indexOf(buffer, 0, count, c, offset, length, 0, false, true) == 0;
	}
	
	
	public int indexOfICase(char[] c, int fromIndex) {
		return Chars.indexOf(buffer, 0, count, c, 0, c.length, fromIndex, true, false);
	}
	
	public boolean startWithICase(char[] c) {
		return Chars.indexOf(buffer, 0, count, c, 0, c.length, 0, true, true) == 0;
	}
}
