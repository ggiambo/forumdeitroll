package com.forumdeitroll.markup;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.forumdeitroll.markup.Emoticon;
import com.forumdeitroll.markup.Emoticons;
import com.forumdeitroll.markup.Snippet;

public class TokenCatalog {
	public static TokenMatcher[][] get() {
		TokenMatcher[][] matchers = new TokenMatcher[TokenizerMode.values().length][];
		matchers[TokenizerMode.BEGINNING_OF_LINE.ordinal()] = new TokenMatcher[] {
			new TokenMatcher.Regex("QUOTES_SCRITTO_DA", "^((&gt; )*(- )?Scritto da: (.*?)<BR>)"),
			new TokenMatcher.Regex("QUOTES", "^(&gt;( &gt;)* ?)"),
		};
		matchers[TokenizerMode.NORMAL.ordinal()] = new TokenMatcher[] {
			new TokenMatcher.Beginning("BR", "<BR>"),
			new TokenMatcher.Regex("TAG_OPEN", "^(<(b|i|s|u)>)", Pattern.CASE_INSENSITIVE),
			new TokenMatcher.Regex("TAG_CLOSE", "^(</(b|i|s|u)>)", Pattern.CASE_INSENSITIVE),
			new TokenMatcher.BBCodeOpen("CODE"),
			new TokenMatcher.Regex("CODE_OPEN_WITH_LANG", "^(\\[code ([a-z]{1,12})\\])", Pattern.CASE_INSENSITIVE),
			new TokenMatcher.Regex("URL", "^(\\[url\\]([^ ]+)\\[/url\\])", Pattern.CASE_INSENSITIVE),
			new TokenMatcher.Regex("URL_OPEN_WITH_LINK", "^(\\[url=([^ \\]]+)\\])", Pattern.CASE_INSENSITIVE),
			new TokenMatcher.Regex("IMG", "^(\\[img\\]([^ \\[]+)\\[/img\\])", Pattern.CASE_INSENSITIVE),
			new TokenMatcher.BBCodeOpen("SPOILER"),
			new TokenMatcher.BBCodeClose("SPOILER"),
			new TokenMatcher.Regex("YT", "^(\\[yt\\]([^ \\[]{6,50})\\[/yt])", Pattern.CASE_INSENSITIVE),
			new TokenMatcher.Regex("COLOR_OPEN", "^(\\[color (#[0-9a-f]{3}|#[0-9a-f]{6}|[a-z]{1,12})\\])", Pattern.CASE_INSENSITIVE),
			new TokenMatcher.BBCodeClose("COLOR"),
			new TokenMatcher.Beginning("TEXT", "["),
			new TokenMatcher.Beginning("TEXT", "<"),
			new TokenMatcher.Regex("TEXT", "^([^\\[<]+)"),
		};
		matchers[TokenizerMode.CODE.ordinal()] = new TokenMatcher[] {
			new TokenMatcher.BBCodeClose("CODE"),
			new TokenMatcher.Beginning("BR", "<BR>"),
			new TokenMatcher.Regex("TAG_OPEN", "^(<(b|i|s|u)>)", Pattern.CASE_INSENSITIVE),
			new TokenMatcher.Regex("TAG_CLOSE", "^(</(b|i|s|u)>)", Pattern.CASE_INSENSITIVE),
			new TokenMatcher.Beginning("CODE_CONTENT", "["),
			new TokenMatcher.Beginning("CODE_CONTENT", "<"),
			new TokenMatcher.Regex("CODE_CONTENT", "^([^\\[<]+)"),
		};
		matchers[TokenizerMode.URL.ordinal()] = new TokenMatcher[] {
			new TokenMatcher.BBCodeClose("URL"),
			new TokenMatcher.Beginning("BR", "<BR>"),
			new TokenMatcher.Regex("TAG_OPEN", "^(<(b|i|s|u)>)", Pattern.CASE_INSENSITIVE),
			new TokenMatcher.Regex("TAG_CLOSE", "^(</(b|i|s|u)>)", Pattern.CASE_INSENSITIVE),
			new TokenMatcher.Beginning("URL_CONTENT", "["),
			new TokenMatcher.Beginning("URL_CONTENT", "<"),
			new TokenMatcher.Regex("URL_CONTENT", "^([^\\[<]+)"),
		};
		matchers[TokenizerMode.LINK.ordinal()] = new TokenMatcher[] {
			new TokenMatcher.Regex("LINK_YOUTUBE_SHORTENED", "^(https?://youtu\\.be/([a-zA-Z0-9-_]{6,15})(.*)?)"),
			new TokenMatcher.Regex("LINK_YOUTUBE_REGULAR", "^((https?://)?([a-z]{1,3})\\.youtube\\.com/watch\\?v=([a-zA-Z0-9-_]{6,15})([#&].*)?)"),
			new TokenMatcher.Regex("LINK_YOUTUBE_REGULAR", "^((https?://)?([a-z]{1,3})\\.youtube\\.com/watch\\?.*&v=([a-zA-Z0-9-_]{6,15})([#&].*)?)"),
			new TokenMatcher.Regex("LINK_INTERNAL_MSGID", "^((https?://(www\\.)?forumdeitroll\\.com/)?Threads\\?action=getByThread&threadId=([0-9]+)(#msg([0-9]+))?)"),
			new TokenMatcher.BeginningIgnoreCaseKeepRegion("LINK_INTERNAL", "Threads?action="),
			new TokenMatcher.BeginningIgnoreCaseKeepRegion("LINK_INTERNAL", "Polls?action="),
			new TokenMatcher.BeginningIgnoreCaseKeepRegion("LINK_INTERNAL", "Messages?action="),
			new TokenMatcher.BeginningIgnoreCaseKeepRegion("LINK_INTERNAL", "Misc?action="),
			new TokenMatcher.BeginningIgnoreCaseKeepRegion("LINK_INTERNAL", "User?action="),
			new TokenMatcher.BeginningIgnoreCaseKeepRegion("LINK_INTERNAL_WITH_DOMAIN", "http://forumdeitroll.com/"),
			new TokenMatcher.BeginningIgnoreCaseKeepRegion("LINK_INTERNAL_WITH_DOMAIN", "https://forumdeitroll.com/"),
			new TokenMatcher.BeginningIgnoreCaseKeepRegion("LINK_INTERNAL_WITH_DOMAIN", "http://www.forumdeitroll.com/"),
			new TokenMatcher.BeginningIgnoreCaseKeepRegion("LINK_INTERNAL_WITH_DOMAIN", "https://www.forumdeitroll.com/"),
			new TokenMatcher.Regex("LINK_OLD_FORUM_MESSAGE", "^(http://www\\.forumdeitroll\\.com/m\\aspx\\?m_id=([0-9]+)).*"),
			new TokenMatcher.Regex("LINK_OLD_FORUM_THREAD", "^(http://www\\.forumdeitroll\\.com/ms\\aspx\\?m_id=([0-9]+)).*"),
			new TokenMatcher.BeginningIgnoreCaseKeepRegion("LINK_WWW", "www."),
			new TokenMatcher.BeginningIgnoreCaseKeepRegion("LINK_HTTP", "http://"),
			new TokenMatcher.BeginningIgnoreCaseKeepRegion("LINK_HTTPS", "https://"),
			new TokenMatcher.BeginningIgnoreCaseKeepRegion("LINK_MAILTO", "mailto:"),
			new TokenMatcher.BeginningIgnoreCaseKeepRegion("LINK_FTP", "ftp://"),
			new TokenMatcher.Regex("LINK_TLD", "^(([a-z][a-z0-9\\-]{1,61}[a-z0-9]\\.)+(it|com|org|net|info|de|fr|co\\.uk|es|eu|biz|name|edu|gov|mil)(/.*)?)", Pattern.CASE_INSENSITIVE),
		};
		ArrayList<TokenMatcher> tmText = new ArrayList<TokenMatcher>();
		ArrayList<TokenMatcher> tmBeginningWord = new ArrayList<TokenMatcher>();
		for (Emoticon emoticon : Emoticons.tutte) {
			if (emoticon.sequenceStartWithSpace) {
				tmBeginningWord.add(new TokenMatcher.BeginningIgnoreCase("TEXT_EMOTICON_" + emoticon.imgName, emoticon.initialSequence));
			} else {
				tmText.add(new TokenMatcher.InTheMiddleIgnoreCase("TEXT_EMOTICON_" + emoticon.imgName, emoticon.sequence));
			}
		}
		for (Snippet snippet : Snippet.list) {
			tmText.add(new TokenMatcher.InTheMiddleIgnoreCase("TEXT_SNIPPET_" + snippet.sequence, snippet.sequence));
		}
		tmText.toArray(matchers[TokenizerMode.TEXT.ordinal()] = new TokenMatcher[tmText.size()]);
		tmBeginningWord.toArray(matchers[TokenizerMode.BEGINNING_OF_WORD.ordinal()] = new TokenMatcher[tmBeginningWord.size()]);
		matchers[TokenizerMode.WORDSCAN.ordinal()] = new TokenMatcher[] {
			new TokenMatcher.Regex("INTERNAL_WORD", "([^ ]+)( *)"),
		};
		return matchers;
	}
}
