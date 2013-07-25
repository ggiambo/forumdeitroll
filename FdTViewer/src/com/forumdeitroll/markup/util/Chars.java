package com.forumdeitroll.markup.util;

public class Chars {
	public static boolean isAlphanum(char ch) {
		int type = Character.getType(ch);
		return type == Character.UPPERCASE_LETTER
				|| type == Character.LOWERCASE_LETTER
				|| type == Character.TITLECASE_LETTER
				|| type == Character.MODIFIER_LETTER
				|| type == Character.OTHER_LETTER
				|| type == Character.LETTER_NUMBER
				|| type == Character.DECIMAL_DIGIT_NUMBER;
	}
	
	// preso da java.lang.String e modificato per ignoreCase e startWith
    public static int indexOf(
    	char[] source,
    	int sourceOffset,
    	int sourceCount,
    	char[] target,
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

        char first = target[targetOffset];
        int max = sourceOffset + (sourceCount - targetCount);

        for (int i = sourceOffset + fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source[i] != first) {
                while (
                	++i <= max &&
                	(startWith ? i == sourceOffset : true) &&
                	(ignoreCase
                			? Character.toLowerCase(source[i]) != Character.toLowerCase(first)
			                : source[i] != first)
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
                			? Character.toLowerCase(source[j]) == Character.toLowerCase(target[k])
                			: source[j] == target[k]);
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
    
    public static boolean containsWhitespaces(char[] buffer, int offset, int length) {
    	for (int i = offset; i < length; i++)
    		if (Character.isWhitespace(buffer[i]))
    			return true;
    	return false;
    }
    
    public static boolean equals(char[] buffer, int offset, int length, char[] buffer2, int offset2, int length2) {
    	if (length - length2 != 0) return false;
    	return indexOf(buffer, offset, length, buffer2, offset2, length2, 0, false, true) == 0;
    }
    
    public static boolean equals(char[] buffer, char[] buffer2) {
    	if (buffer.length != buffer2.length) return false;
    	return indexOf(buffer, 0, buffer.length, buffer2, 0, buffer2.length, 0, false, true) == 0;
    }
}
