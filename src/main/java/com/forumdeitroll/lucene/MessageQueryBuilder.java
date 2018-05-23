package com.forumdeitroll.lucene;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class MessageQueryBuilder {

	static Query build(SearchParams searchParams) {

		BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

		if (StringUtils.isNotEmpty(searchParams.author)) {
			Term authorTerm = new Term("author", searchParams.author.toLowerCase());
			TermQuery authorQuery = new TermQuery(authorTerm);
			queryBuilder.add(authorQuery, BooleanClause.Occur.MUST);
		}

		if (StringUtils.isNotEmpty(searchParams.subject)) {
			Term subjectTerm = new Term("subject", searchParams.subject.toLowerCase());
			TermQuery subjectQuery = new TermQuery(subjectTerm);
			queryBuilder.add(subjectQuery, BooleanClause.Occur.MUST);
		}

		if (searchParams.dateFrom != null || searchParams.dateTo != null) {
			TermRangeQuery dateQuery = TermRangeQuery.newStringRange("date", formatDate(searchParams.dateFrom), formatDate(searchParams.dateTo), true, true);
			queryBuilder.add(dateQuery, BooleanClause.Occur.MUST);
		}

		return queryBuilder.build();
	}

	private static String formatDate(TemporalAccessor date) {
		if (date == null) {
			return "*";
		}
		return DateTimeFormatter.ISO_LOCAL_DATE.format(date);
	}

}
