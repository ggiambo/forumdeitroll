package com.acmetoy.ravanator.fdt.persistence;

import java.security.NoSuchAlgorithmException;

import java.security.MessageDigest;
import java.math.BigInteger;

public class AuthorDTO {

	private int ranking = -1;

	private int messages = -1;

	private String nick = null;

	private byte[] avatar = null;

	protected String oldPassword = null;

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

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
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

	/**
	Restituisce true se la password corretta e` uguale a quella salvata sul db per opportune definizioni di "uguale".
	*/
	public boolean passwordIs(final String password) throws NoSuchAlgorithmException {
		if (password == null) return false;
		if (this.oldPassword == null) return false;
		return this.oldPassword.equals(md5(password));
	}

	/**
	Metodo di transizione alle password salate, verra` rimosso in futuro -- sarrusofono, 20120115
	*/
	static public String makeOldPassword(final String password) {
		return md5(password);
	}

    /**
	 * Calcola l'MD5 della password, stesso valore di MD5() di MySQL
	 * @param input
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	static protected String md5(String input) {
		try {
			String result = input;
			if (input != null) {
				MessageDigest md = MessageDigest.getInstance("MD5"); // or "SHA-1"
				md.update(input.getBytes());
				BigInteger hash = new BigInteger(1, md.digest());
				result = hash.toString(16);
				while (result.length() < 32) {
					result = "0" + result;
				}
			}
			return result;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("La tua virtual machine non esiste", e);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("nick:").append(nick).append(",");
		sb.append("ranking:").append(ranking).append(",");
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
		return nick != null && ranking != -1 && messages != -1;
	}

}
