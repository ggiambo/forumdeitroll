package com.forumdeitroll.markup;

import java.io.Reader;
import java.io.Writer;

import com.forumdeitroll.markup.RenderOptions;

public class Renderer {
	private static ThreadLocal<MarkupRenderer> mrTl = new ThreadLocal<MarkupRenderer>();
	private static ThreadLocal<char[]> bufTl = new ThreadLocal<char[]>();

	public static void render(Reader in, Writer out, RenderOptions opts) throws Exception {
		char[] buffer;
		if ((buffer = bufTl.get()) == null) {
			buffer = new char[40*1024];
			bufTl.set(buffer); // max message size, in db sono piu` lunghi pero`
		}
		int len;
		StringBuilder markup = new StringBuilder();
		while ((len = in.read(buffer)) != -1) {
			markup.append(buffer, 0, len);
		}
		MarkupRenderer renderer;
		if ((renderer = mrTl.get()) == null) {
			renderer = new MarkupRenderer();
			mrTl.set(renderer);
		}
		String html = renderer.render(markup.toString(), opts);
		out.write(html);
	}
	public static void render(String in, Writer out, RenderOptions opts) throws Exception {
		MarkupRenderer renderer;
		if ((renderer = mrTl.get()) == null) {
			renderer = new MarkupRenderer();
			mrTl.set(renderer);
		}
		out.write(renderer.render(in, opts));
	}
	public static String render(String in, RenderOptions opts) throws Exception {
		MarkupRenderer renderer;
		if ((renderer = mrTl.get()) == null) {
			renderer = new MarkupRenderer();
			mrTl.set(renderer);
		}
		return renderer.render(in, opts);
	}
}
