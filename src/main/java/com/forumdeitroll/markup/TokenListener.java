package com.forumdeitroll.markup;

public interface TokenListener {
	void on(TokenMatcher token, TokenMatcher additional) throws Exception;
}
