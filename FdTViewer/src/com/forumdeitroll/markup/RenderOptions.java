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
}
