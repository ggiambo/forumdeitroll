package com.acmetoy.ravanator.fdt.persistence;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Properties;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class AuthorDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	private transient SecretKeyFactory secretKeyFactory = null;
	
	private int messages = -1;

	private String nick = null;

	private byte[] avatar = null;

	protected String oldPassword = null;

	protected String salt = null, hash = null;
	
	private Properties preferences = new Properties();

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public byte[] getAvatar() {
		return avatar;
	}

	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}

	public int getMessages() {
		return messages;
	}

	public void setMessages(int messages) {
		this.messages = messages;
	}

	public void setOldPassword(final String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public void setSalt(final String salt) {
		this.salt = salt;
	}

	public String getSalt() {
		return this.salt;
	}

	public void setHash(final String hash) {
		this.hash = hash;
	}

	public String getHash() {
		return this.hash;
	}

	public Properties getPreferences() {
		return preferences;
	}

	public void setPreferences(Properties preferences) {
		this.preferences = preferences;
	}

	public void changePassword(final String password) {
		if (StringUtils.isEmpty(this.salt)) {
			byte[] saltBytes = new byte[8];
			new SecureRandom().nextBytes(saltBytes);
			this.salt = hex(saltBytes, false);
		}

		this.hash = passwordHash(password, this.salt);
	}

	/**
	Restituisce true se la password corretta e` uguale a quella salvata sul db per opportune definizioni di "uguale".
	*/
	public boolean passwordIs(final String password) throws NoSuchAlgorithmException {
		if (password == null) return false;
		if (!StringUtils.isEmpty(this.salt)) {
			// nuova modalita` di hashing
			if (this.hash == null) return false;

			return this.hash.equals(passwordHash(password, this.salt));
		} else {
			// vecchia modalita` di hashing
			if (this.oldPassword == null) return false;
			return this.oldPassword.equals(md5(password));
		}
	}

	public boolean newAuth() {
		return !(StringUtils.isEmpty(this.salt));
	}

	static protected String hex(final byte[] input, final boolean padding) {
		BigInteger hash = new BigInteger(1, input);
		String result = hash.toString(16);
		if (padding) {
			while (result.length() < 32) {
				result = "0" + result;
			}
		}

		return result;
	}

	private String passwordHash(final String password, final String salt) {
		final KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 1024, 192);
		try {
			byte[] hash = getSecretKeyFactory().generateSecret(spec).getEncoded();
			return hex(hash, true);
		} catch(InvalidKeySpecException e) {
			throw new RuntimeException("Algoritmo di hashing fallito per strane ragioni", e);
		}
	}

    /**
	 * Calcola l'MD5 della password, stesso valore di MD5() di MySQL
	 * @param input
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private String md5(String input) {
		try {
			String result = input;
			if (input != null) {
				MessageDigest md = MessageDigest.getInstance("MD5"); // or "SHA-1"
				md.update(input.getBytes());
				result = hex(md.digest(), true);
			}
			return result;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("La tua virtual machine non esiste", e);
		}
	}
	
	private SecretKeyFactory getSecretKeyFactory() {
		if (secretKeyFactory == null) {
			try {
				secretKeyFactory = SecretKeyFactory.getInstance("PBEWithSHAAnd3-KeyTripleDES-CBC");
			} catch(NoSuchAlgorithmException e) {
				throw new RuntimeException("Algoritmo di hashing non supportato?!", e);
			}
		}
		return secretKeyFactory;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("nick:").append(nick).append(",");
		sb.append("avatar:");
		if (avatar != null) {
			sb.append(avatar.length);
		} else {
			sb.append("0");
		}
		sb.append("bytes,");
		sb.append("messages:").append(messages).append(",");
		return sb.toString();
	}

	public boolean isValid() {
		return nick != null && messages != -1;
	}

}
