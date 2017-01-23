package com.forumdeitroll.markup;

public class Emoticon {
	public final String imgName;
	public final String sequence;
	public final String sequenceUpcase;
	public final String initialSequence;
	public final String initialSequenceUpcase;
	public final String altText;
	public final String htmlReplacement;
	public final boolean sequenceStartWithSpace;

	private Emoticon(final String imgName, final String sequence, final String altText, final String htmlReplacement) {
		this.imgName = imgName;
		this.sequence = sequence;
		this.sequenceUpcase = sequence.toUpperCase();
		this.sequenceStartWithSpace = sequence.charAt(0) == ' ';
		this.initialSequence = sequenceStartWithSpace ? sequence.substring(1) : sequence;
		this.initialSequenceUpcase = initialSequence.toUpperCase();
		this.altText = altText;
		this.htmlReplacement = htmlReplacement;
	}

	public static Emoticon make(String imgName, String sequence, String altText) {
		final String htmlReplacement = String.format("<img alt='%s' title='%s' class='emoticon' src='images/emo/%s.gif'>", altText, altText, imgName);
		return new Emoticon(imgName, sequence, altText, htmlReplacement);
	}

	public static Emoticon makeExt(String imgName, String sequence, String altText) {
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
	public String getInitialSequence() {
		return initialSequence;
	}
	public String getAltText() {
		return altText;
	}
	public String getHtmlReplacement() {
		return htmlReplacement;
	}
	public boolean isSequenceStartWithSpace() {
		return sequenceStartWithSpace;
	}
}