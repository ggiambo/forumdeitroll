package com.forumdeitroll.markup;

public interface TokenListener {
	public void on(TokenMatcher token, TokenMatcher additional) throws Exception;

	public static TokenListener PRINTER = new TokenListener() {
		@Override public void on(TokenMatcher token, TokenMatcher additional) throws Exception {
			System.out.println(token + (additional != null ? " additional="+additional : ""));
		}
	};
}
