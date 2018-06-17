package com.forumdeitroll.markup;

public class RenderOptions {

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

	private String toSimpleString() {
		return tobin(renderImages, "i") + tobin(showImagesPlaceholder, "p") + tobin(renderYoutube, "y") + tobin(embedYoutube, "e") + tobin(collapseQuotes, "q");
	}

}
