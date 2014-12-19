package com.forumdeitroll.markup;

public class RenderOptions {

	/**
	 * opzioni di rendering di default (usate per utenti anonimi)
	 */

	public int buffersize = 1024; // determina anche la massima dimensione di un link

	public boolean renderImages = true; // false per signature
	public boolean showImagesPlaceholder = false;

	public boolean renderYoutube = true; // false per signature
	public boolean embedYoutube = true;

	public boolean collapseQuotes = false;

	public boolean authorIsAnonymous = true;

	public String toString() {
		return "<renderImages=" + renderImages + " showImagesPlaceholder=" + showImagesPlaceholder + " renderYoutube=" + renderYoutube + " embedYoutube=" + embedYoutube + " collapseQuotes=" + collapseQuotes + " authorIsAnonymous=" + authorIsAnonymous + " " + toSimpleString() + ">";
	}

	private static String tobin(final boolean f, final String s) {
		return f ? s.toUpperCase() : s;
	}

	private static boolean frombin(final char ch, final char t) {
		return (Character.isUpperCase(ch));
	}

	public String toSimpleString() {
		return tobin(renderImages, "i") + tobin(showImagesPlaceholder, "p") + tobin(renderYoutube, "y") + tobin(embedYoutube, "e") + tobin(collapseQuotes, "q");
	}

	public static RenderOptions fromSimpleString(final String s) {
		final RenderOptions r = new RenderOptions();
		r.renderImages = frombin(s.charAt(0), 'i');
		r.showImagesPlaceholder = frombin(s.charAt(1), 'p');
		r.renderYoutube = frombin(s.charAt(2), 'y');
		r.embedYoutube = frombin(s.charAt(3), 'e');
		r.collapseQuotes = frombin(s.charAt(4), 'q');
		return r;
	}
}
