package com.forumdeitroll.markup.util;

import java.io.IOException;

import com.forumdeitroll.markup.RenderIO;
import com.forumdeitroll.markup.RenderOptions;
import com.forumdeitroll.markup.RenderState;

public class Images {
	
	private static final char[] IMG = "[img]".toCharArray();
	private static final char[] IMG_END = "[/img]".toCharArray();
	private static final char[] ANONIMG_START = "<a rel='nofollow noreferrer' target='_blank' href=\"".toCharArray();
	private static final char[] ANONIMG_END = "\">Immagine postata da ANOnimo</a>".toCharArray();
	private static final char[] EMBEDDED_IMAGE_START = "<a rel='nofollow noreferrer' target='_blank' class='preview' href=\"".toCharArray();
	private static final char[] EMBEDDED_IMAGE_MID = "\"><img class='userPostedImage' alt='Immagine postata dall&#39;utente' src=\"".toCharArray();
	private static final char[] EMBEDDED_IMAGE_END = "\"></a>".toCharArray();
	
	public static final int MAX_IMMYS = 15;
	
	// [img]$link[/img]
	public static boolean img(RenderIO io, RenderState state, RenderOptions opts) throws IOException {
		if (!opts.renderImages)
			return false;
		if (state.immyCount >= MAX_IMMYS)
			return false;
		if (io.startWith(IMG)) {
			int end = io.indexOf(IMG_END, IMG.length);
			if (end == -1)
				return false;
			if (!Links.isLink(io.buffer, IMG.length, end - IMG.length))
				return false;
			if (opts.authorIsAnonymous && opts.showImagesPlaceholder) {
				io.write(ANONIMG_START);
				Links.writeUrl(io.out, io.buffer, IMG.length, end - IMG.length);
				io.write(ANONIMG_END);
			} else {
				io.write(EMBEDDED_IMAGE_START);
				Links.writeUrl(io.out, io.buffer, IMG.length, end - IMG.length);
				io.write(EMBEDDED_IMAGE_MID);
				Links.writeUrl(io.out, io.buffer, IMG.length, end - IMG.length);
				io.write(EMBEDDED_IMAGE_END);
			}
			io.write("<a href=\"https://www.google.com/searchbyimage?&image_url=");
			Links.writeUrl(io.out, io.buffer, IMG.length, end - IMG.length);
			io.write("\" alt='Ricerca immagini simili' title='Ricerca immagini simili' rel='nofollow noreferrer' target='_blank'><img src=\"https://www.google.com/favicon.ico\" style='width: 16px; height: 16px;'></a>");
			io.skip(end + IMG_END.length);
			state.immyCount++;
			return true;
		}
		return false;
	}
}
