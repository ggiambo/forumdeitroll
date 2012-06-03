package com.acmetoy.ravanator.fdt.persistence;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

public class AuthorDTO implements Serializable {

	private static final long serialVersionUID = 2L;

	private int messages = -1;

	private String nick = null;

	private byte[] avatar = null;

	protected String oldPassword = null;

	protected String salt = null, hash = null;

	private Map<String, String> preferences = new ConcurrentHashMap<String, String>();

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

	public String getOldPassword() {
		return this.oldPassword;
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

	public Map<String, String> getPreferences() {
		return preferences;
	}

	public void setPreferences(final Map<String, String> preferences) {
		this.preferences.clear();
		this.preferences.putAll(preferences);
	}

	public boolean newAuth() {
		return !(StringUtils.isEmpty(this.salt));
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
