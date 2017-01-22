package com.forumdeitroll.persistence;

import java.util.HashMap;
import java.util.Map;

public enum SearchMessagesSort {

	RELEVANCE("relevance DESC"), DATE_LEAST_RECENT("date ASC"), DATE_MOST_RECENT("date DESC");

	protected static Map<String, SearchMessagesSort> stringToSort = new HashMap<String, SearchMessagesSort>();

	static {
		stringToSort.put("rank", RELEVANCE);
		stringToSort.put("relevance", RELEVANCE);
		stringToSort.put("date", DATE_MOST_RECENT);
		stringToSort.put("rdate", DATE_LEAST_RECENT);
	}

	public static synchronized SearchMessagesSort parse(final String sortString) {
		final SearchMessagesSort r = stringToSort.get(sortString);
		return (r != null) ? r : RELEVANCE;
	}

	protected final String orderByString;

	SearchMessagesSort(final String orderByString) {
		this.orderByString = orderByString;
	}

	public String orderBy() {
		return orderByString;
	}
}