package com.forumdeitroll.markup3;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TokenMatcher implements MatchResult {

	public abstract boolean find();
	public abstract void region(int start, int end);
	public abstract boolean atBeginningOfRegion();
	public abstract void reset(String text);
	public final String name;
	protected TokenMatcher(String name) {
		this.name = name;
	}
	@Override public String toString() {
		return String.format("%s,%s(%s,%d,%d)"
			, name, getClass().getSimpleName(), group(), start(), end());
	}

	public static class Wrapper extends TokenMatcher {
		private TokenMatcher matcher;
		public Wrapper(String name, TokenMatcher matcher) {
			super(name);
			this.matcher = matcher;
		}
		@Override public int start() {
			return matcher.start();
		}
		@Override public int start(int group) {
			return matcher.start(group);
		}
		@Override public int end() {
			return matcher.end();
		}
		@Override public int end(int group) {
			return matcher.end(group);
		}
		@Override public String group() {
			return matcher.group();
		}
		@Override public String group(int group) {
			return matcher.group(group);
		}
		@Override public int groupCount() {
			return matcher.groupCount();
		}
		@Override public void region(int start, int end) {
			matcher.region(start, end);
		}
		@Override public boolean atBeginningOfRegion() {
			return matcher.atBeginningOfRegion();
		}
		@Override public void reset(String text) {
			throw new UnsupportedOperationException();
		}
		@Override public boolean find() {
			throw new UnsupportedOperationException();
		}
	}

	public static class Regex extends TokenMatcher {
		private final Pattern pattern;
		private final boolean atBeginning;
		private Matcher matcher;
		private int regionStart;
		public Regex(String name, Pattern pattern) {
			super(name);
			this.pattern = pattern;
			this.atBeginning = pattern.pattern().startsWith("^");
		}
		public Regex(String name, String pattern) {
			this(name, Pattern.compile(pattern));
		}
		public Regex(String name, String pattern, int flags) {
			this(name, Pattern.compile(pattern, flags));
		}
		@Override public boolean atBeginningOfRegion() {
			return atBeginning || regionStart == matcher.start();
		}
		@Override public boolean find() {
			return matcher.find();
		}
		@Override public int start() {
			return matcher.start();
		}
		@Override public int start(int group) {
			return matcher.start(group);
		}
		@Override public int end() {
			return matcher.end();
		}
		@Override public int end(int group) {
			return matcher.end(group);
		}
		@Override public String group() {
			return matcher.group();
		}
		@Override public String group(int group) {
			return matcher.group(group);
		}
		@Override public int groupCount() {
			return matcher.groupCount();
		}
		@Override public void region(int start, int end) {
			matcher.region(start, end);
		}
		@Override public void reset(String text) {
			matcher = pattern.matcher(text);
		}
	}

	public static class Section extends TokenMatcher {
		public Section(String name) {
			super(name);
		}
		protected String stringRef;
		protected int start, end;
		@Override public int end() {
			return end;
		}
		@Override public int end(int group) {
			return end;
		}
		@Override public String group() {
			return stringRef.substring(start, end);
		}
		@Override public String group(int group) {
			return stringRef.substring(start, end);
		}
		@Override public int groupCount() {
			return 1;
		}
		@Override public int start() {
			return start;
		}
		@Override public int start(int group) {
			return start;
		}
		protected static int indexOf(
		    	String source,
		    	int sourceOffset,
		    	int sourceCount,
		    	String target,
		    	int targetOffset,
		    	int targetCount,
		    	int fromIndex,
		    	boolean ignoreCase,
		    	boolean startWith
		    ) {
	        if (fromIndex >= sourceCount) {
	            return (targetCount == 0 ? sourceCount : -1);
	        }
	        if (fromIndex < 0) {
	            fromIndex = 0;
	        }
	        if (targetCount == 0) {
	            return fromIndex;
	        }

	        char first = target.charAt(targetOffset);
	        int max = sourceOffset + (sourceCount - targetCount);

	        for (int i = sourceOffset + fromIndex; i <= max; i++) {
	            /* Look for first character. */
	            if (source.charAt(i) != first) {
	                while (
	                	++i <= max &&
	                	(startWith ? i == sourceOffset : true) &&
	                	(ignoreCase
	                			? Character.toLowerCase(source.charAt(i)) != Character.toLowerCase(first)
				                : source.charAt(i) != first)
				    );
	            }

	            if (startWith && i > sourceOffset + fromIndex)
	            	return -1;

	            /* Found first character, now look at the rest of v2 */
	            if (i <= max) {
	                int j = i + 1;
	                int end = j + targetCount - 1;
	                for (
	                	int k = targetOffset + 1;
	                		j < end &&
	                		(ignoreCase
	                			? Character.toLowerCase(source.charAt(j)) == Character.toLowerCase(target.charAt(k))
	                			: source.charAt(j) == target.charAt(k));
	                	j++, k++
	                );

	                if (j == end) {
	                    /* Found whole string. */
	                    return i - sourceOffset;
	                }
	            }
	        }
	        return -1;
	    }
		@Override public void reset(String text) {
			this.stringRef = text;
			region(0, text.length());
		}
		@Override public void region(int start, int end) {
			this.start = start;
			this.end = end;
		}
		@Override public boolean find() {
			throw new UnsupportedOperationException();
		}
		@Override public boolean atBeginningOfRegion() {
			throw new UnsupportedOperationException();
		}
	}

	public static class Beginning extends Section {
		private String needle;
		public Beginning(String name, String needle) {
			super(name);
			this.needle = needle;
		}
		@Override public boolean find() {
			if ((end - start) < needle.length()) {
				return false;
			}
			super.end = start + needle.length();
			return stringRef.startsWith(needle, start);
		}
		@Override public boolean atBeginningOfRegion() {
			return true;
		}
	}

	public static class BeginningIgnoreCase extends Section {
		private String needle;
		public BeginningIgnoreCase(String name, String needle) {
			super(name);
			this.needle = needle;
		}
		@Override public boolean find() {
			if ((end - start) < needle.length()) {
				return false;
			}
			super.end = start + needle.length();
			return 0 == indexOf(
				stringRef, start, end - start,
				needle, 0, needle.length(),
				0, true, true);
		}
		@Override public boolean atBeginningOfRegion() {
			return true;
		}
	}

	public static class BBCodeOpen extends BeginningIgnoreCase {
		public BBCodeOpen(String name) {
			super(name.toUpperCase() + "_OPEN", "[" + name + "]");
		}
	}

	public static class BBCodeClose extends BeginningIgnoreCase {
		public BBCodeClose(String name) {
			super(name.toUpperCase() + "_CLOSE", "[/" + name + "]");
		}
	}

	public static class BeginningIgnoreCaseKeepRegion extends Section {
		private String needle;
		public BeginningIgnoreCaseKeepRegion(String name, String needle) {
			super(name);
			this.needle = needle;
		}
		@Override public boolean find() {
			if ((end - start) < needle.length()) {
				return false;
			}
			return 0 == indexOf(
				stringRef, start, end - start,
				needle, 0, needle.length(),
				0, true, true);
		}
		@Override public boolean atBeginningOfRegion() {
			return true;
		}
	}

	public static class InTheMiddle extends Section {
		private String needle;
		private boolean atBeginning;
		public InTheMiddle(String name, String needle) {
			super(name);
			this.needle = needle;
		}
		@Override public boolean find() {
			int index = stringRef.indexOf(needle, start);
			if (index != -1 && index <= (end - needle.length())) {
				atBeginning = index == start;
				this.start = index;
				this.end = index + needle.length();
				return true;
			}
			return false;
		}
		@Override public boolean atBeginningOfRegion() {
			return atBeginning;
		}
	}

	public static class InTheMiddleIgnoreCase extends Section {
		private String needle;
		private boolean atBeginning;
		public InTheMiddleIgnoreCase(String name, String needle) {
			super(name);
			this.needle = needle;
		}
		@Override public boolean find() {
			int index = indexOf(
				stringRef, start, end - start,
				needle, 0, needle.length(),
				0, true, false);
			if (index != -1 && index <= (end - needle.length())) {
				atBeginning = index == 0;
				this.start += index;
				this.end = this.start + needle.length();
				return true;
			}
			return false;
		}
		@Override public boolean atBeginningOfRegion() {
			return atBeginning;
		}
	}
}
