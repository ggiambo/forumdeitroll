package com.forumdeitroll.persistence;

import java.io.Serializable;

public class AdDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String title;
	private String visurl;
	private String content;

	public AdDTO() {

	}

	public AdDTO(final AdDTO ad) {
		this.id = ad.id;
		this.title = ad.title;
		this.visurl = ad.visurl;
		this.content = ad.content;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVisurl() {
		return visurl;
	}

	public void setVisurl(String visurl) {
		this.visurl = visurl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
