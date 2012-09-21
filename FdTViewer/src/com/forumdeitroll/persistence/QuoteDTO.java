package com.forumdeitroll.persistence;

public class QuoteDTO {
	private long id;
	private String nick;
	private String content;

	public QuoteDTO() { }

	public QuoteDTO(final QuoteDTO quote) {
		this.id = quote.id;
		this.nick = quote.nick;
		this.content = quote.content;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
