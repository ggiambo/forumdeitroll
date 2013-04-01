package com.forumdeitroll.markup;

public class RenderState {
	
	public int[] tags = new int[4];
	
	public boolean codeTagOpen = false;
	
	public int spoilers = 0;
	
	public int colors = 0;
	
	public int emotiCount = 0;
	
	public int immyCount = 0;
	
	public int embedCount = 0;
	
	public boolean firstLine = true;
	
	public int quoteLevel = 0;
	public boolean multiLineQuoteStarted = false;
}
