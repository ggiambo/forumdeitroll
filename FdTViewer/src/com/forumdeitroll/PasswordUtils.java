package com.forumdeitroll;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.forumdeitroll.persistence.AuthorDTO;

public class PasswordUtils {


	private static SecretKeyFactory secretKeyFactory = null;

	// che cosa brutta brutta brutta !
	static {
		Security.addProvider(new BouncyCastleProvider());
		try {
			secretKeyFactory = SecretKeyFactory.getInstance("PBEWithSHAAnd3-KeyTripleDES-CBC");
		} catch(NoSuchAlgorithmException e) {
			throw new RuntimeException("Algoritmo di hashing non supportato?!", e);
		}
	}

	/* Cambia la password dell'utente posto che l'utente non sia bannato.
	Se password e` null l'utente viene bannato.
	*/
	public static void changePassword(AuthorDTO user, final String password) {
		if (user.isBanned()) return;
		if (password != null) {
			if (StringUtils.isEmpty(user.getSalt())) {
				user.setSalt(RandomPool.getString(8));
			}
			user.setHash(passwordHash(password, user.getSalt()));
		} else {
			user.setSalt(AuthorDTO.BANNED_TAG);
			user.setHash(AuthorDTO.BANNED_TAG);
		}
	}

	/**
	 * Restituisce true se la password corretta e` uguale a quella salvata sul db per opportune definizioni di "uguale".
	 * @param user
	 * @param password
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static boolean hasUserPassword(AuthorDTO user, final String password) throws NoSuchAlgorithmException {
		if (password == null) return false;
		if (user.isBanned()) return false;
		if (!StringUtils.isEmpty(user.getSalt())) {
			// nuova modalita` di hashing
			if (user.getHash() == null) return false;
			return user.getHash().equals(passwordHash(password, user.getSalt()));
		}
		// vecchia modalita` di hashing
		if (user.getOldPassword() == null) return false;
		return user.getOldPassword().equals(md5(password));
	}

    /**
	 * Calcola l'MD5 della password, stesso valore di MD5() di MySQL
	 * @param input
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static String md5(String input) {
		try {
			String result = input;
			if (input != null) {
				MessageDigest md = MessageDigest.getInstance("MD5"); // or "SHA-1"
				md.update(input.getBytes());
				result = RandomPool.hex(md.digest(), true);
			}
			return result;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("La tua virtual machine non esiste", e);
		}
	}

	private static String passwordHash(final String password, final String salt) {
		final KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 1024, 192);
		try {
			byte[] hash = secretKeyFactory.generateSecret(spec).getEncoded();
			return RandomPool.hex(hash, true);
		} catch(InvalidKeySpecException e) {
			throw new RuntimeException("Algoritmo di hashing fallito per strane ragioni", e);
		}
	}

}
