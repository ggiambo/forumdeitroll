package com.forumdeitroll.lucene;

import java.time.temporal.TemporalAccessor;

class SearchParams {

	TemporalAccessor dateFrom;
	TemporalAccessor dateTo;
	String author;
	String subject;
	String text;

	void dateFrom(TemporalAccessor dateFrom) {
		this.dateFrom = dateFrom;
	}

	void dateTo(TemporalAccessor dateTo) {
		this.dateTo = dateTo;
	}

	void author(String author) {
		this.author = author;
	}

	void subject(String subject) {
		this.subject = subject;
	}

	void text(String text) {
		this.text = text;
	}

}