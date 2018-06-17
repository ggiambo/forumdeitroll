package com.forumdeitroll.markup;

public class Emoticon {
	public final String imgName;
	public final String sequence;
	final String initialSequence;
	final String initialSequenceUpcase;
	private final String altText;
	final String htmlReplacement;
	final boolean sequenceStartWithSpace;

	private Emoticon(final String imgName, final String sequence, final String altText, final String htmlReplacement) {
		this.imgName = imgName;
		this.sequence = sequence;
		this.sequenceStartWithSpace = sequence.charAt(0) == ' ';
		this.initialSequence = sequenceStartWithSpace ? sequence.substring(1) : sequence;
		this.initialSequenceUpcase = initialSequence.toUpperCase();
		this.altText = altText;
		this.htmlReplacement = htmlReplacement;
	}

	static Emoticon make(String imgName, String sequence, String altText) {
		final String htmlReplacement = String.format("<img alt='%s' title='%s' class='emoticon' src='images/emo/%s.gif'>", altText, altText, imgName);
		return new Emoticon(imgName, sequence, altText, htmlReplacement);
	}

	static Emoticon makeExt(String imgName, String sequence, String altText) {
		final String htmlReplacement = String.format("<img alt='%s' title='%s' class='emoticon' src='images/emoextended/%s.gif'>", altText, altText, imgName);
		return new Emoticon(imgName, sequence, altText, htmlReplacement);
	}

	// getters per el/jstl
	public String getImgName() {
		return imgName;
	}
	public String getSequence() {
		return sequence;
	}
	public String getSafeSequence() {
		return sequence.replace("\\", "\\\\").replace("'", "\\'");
	}

	public String getAltText() {
		return altText;
	}
}
