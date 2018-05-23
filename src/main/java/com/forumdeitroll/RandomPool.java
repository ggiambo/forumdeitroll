package com.forumdeitroll;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class RandomPool {

	private static final ThreadLocal<SecureRandom> secureRandom = ThreadLocal.withInitial(SecureRandom::new);
	private static final ThreadLocal<Random> insecureRandom = ThreadLocal.withInitial(Random::new);

	public static String getString(final int entropyBytes) {
		final byte[] bytes = new byte[entropyBytes];
		secureRandom.get().nextBytes(bytes);
		return hex(bytes, false);
	}

	static String hex(final byte[] input, final boolean padding) {
		BigInteger hash = new BigInteger(1, input);
		String result = hash.toString(16);
		if (padding) {
			while (result.length() < 32) {
				result = "0" + result;
			}
		}

		return result;
	}

	public static int insecureInt(int n) {
		return insecureRandom.get().nextInt(n);
	}
}