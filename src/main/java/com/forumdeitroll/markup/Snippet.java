package com.forumdeitroll.markup;

public class Snippet {
	public final String sequence;
	final String sequenceUpcase;
	public final String htmlReplacement;
	public Snippet(String sequence, String htmlReplacement) {
		this.sequence = sequence.toLowerCase();
		this.sequenceUpcase = sequence.toUpperCase();
		this.htmlReplacement = htmlReplacement;
	}

	public static Snippet[] list = new Snippet[] {
		new Snippet("%cn", "<span style='color:red'><sup>[</sup></span><sup>citazione necessaria</sup><span style='color:red'><sup>]</sup></span>"),
		new Snippet("%chi", "<span style='color:red'><sup>[</sup></span><sup>chi?</sup><span style='color:red'><sup>]</sup></span>"),
		new Snippet("%balle", "<span style='color:red'><sup>[</sup></span><sup>sorgente inaffidabile</sup><span style='color:red'><sup>]</sup></span>"),
		new Snippet("%senzafonte", "<span style='color:red'><sup>[</sup></span><sup>senza fonte</sup><span style='color:red'><sup>]</sup></span>"),
		new Snippet("%moar", "<span style='color:red'><sup>[</sup></span><sup>richiesto chiarimento</sup><span style='color:red'><sup>]</sup></span>"),
		new Snippet("%opinione", "<span style='color:red'><sup>[</sup></span><sup>opinione</sup><span style='color:red'><sup>]</sup></span>"),
	};
}
