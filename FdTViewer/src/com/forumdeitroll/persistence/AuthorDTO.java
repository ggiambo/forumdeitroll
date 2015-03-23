package com.forumdeitroll.persistence;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class AuthorDTO implements Serializable {
	private static final long serialVersionUID = 2L;

	public static final String BANNED_TAG = "*BANNED*";

	private int messages = -1;

	private String nick = null;

	private byte[] avatar = null;

	protected String oldPassword = null;

	protected String salt = null, hash = null;

	private Map<String, String> preferences = new ConcurrentHashMap<String, String>();

	protected final AuthorDTO shadowAuthor;

	private byte[] signatureImage = null;

	public byte[] getSignatureImage() {
		return signatureImage;
	}
	public void setSignatureImage(byte[] signatureImage) {
		this.signatureImage = signatureImage;
	}

	public AuthorDTO(final AuthorDTO shadowAuthor) {
		this.shadowAuthor = shadowAuthor;
	}

	public String getNick() {
		return nick;
	}

	public String getNickUrlsafe() {
		if (nick == null) return null;
		try {
			return URLEncoder.encode(nick, "UTF-8").replace(' ', '+');
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public String getAvatarUrl() {
		return "Misc?action=getAvatar&amp;nick=" + getNickUrlsafe();
	}

	public String getUserInfoUrl() {
		return "User?action=getUserInfo&amp;nick=" + getNickUrlsafe();
	}

	public String getMessagesUrl(String specificParam) {
		if (StringUtils.isNotEmpty(specificParam)) {
			try {
				return "Messages?action=getByAuthor&amp;author=" + getNickUrlsafe() +
						"&amp;forum=" + URLEncoder.encode(specificParam, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		} else {
			return "Messages?action=getByAuthor&amp;author=" + getNickUrlsafe();
		}
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

	public boolean isBanned() {
		if (shadowAuthor != null) {
			if (shadowAuthor.isBanned()) return true;
		}
		if (!isValid()) return false;
		if (getSalt() == null) return false;
		if (getHash() == null) return false;
		return getSalt().equals(BANNED_TAG) && getHash().equals(BANNED_TAG);
	}

	public String realNickname() {
		return (shadowAuthor != null) ? shadowAuthor.getNick() : nick;
	}

	public String description() {
		return
			((nick != null) ? nick : "Non Autenticato") +
			((shadowAuthor != null) ?
				(" (" + ((shadowAuthor.getNick() != null) ? shadowAuthor.getNick() : "Non Autenticato") + ")")
				: "");
	}
}
