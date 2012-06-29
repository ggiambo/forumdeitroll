package com.forumdeitroll;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RandomPool {
	public static final ThreadLocal<SecureRandom> secureRandom = new ThreadLocal<SecureRandom>() {
		@Override protected SecureRandom initialValue() { return new SecureRandom(); }
	};

	public static String getString(final int entropyBytes) {
		final byte[] bytes = new byte[entropyBytes];
		secureRandom.get().nextBytes(bytes);
		return hex(bytes, false);
	}

	public static String hex(final byte[] input, final boolean padding) {
		BigInteger hash = new BigInteger(1, input);
		String result = hash.toString(16);
		if (padding) {
			while (result.length() < 32) {
				result = "0" + result;
			}
		}

		return result;
	}
}