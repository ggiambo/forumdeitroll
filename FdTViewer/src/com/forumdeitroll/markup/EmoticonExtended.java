package com.forumdeitroll.markup;

public class EmoticonExtended extends Emoticon {

	public EmoticonExtended(String imgName, String sequence, String altText) {
		super(imgName, sequence, altText);
		htmlReplacement = String.format("<img alt='%s' title='%s' class='emoticon' src='images/emoextended/%s.gif'>", altText, altText, imgName);
	}
	
}
