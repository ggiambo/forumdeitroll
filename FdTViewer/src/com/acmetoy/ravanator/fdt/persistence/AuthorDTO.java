package com.acmetoy.ravanator.fdt.persistence;

public class AuthorDTO {

	private int ranking = -1;

	private int messages = -1;

	private String nick = null;

	private byte[] avatar = null;

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
		return nick != null && ranking != -1 &&	messages != -1;
	}

}
