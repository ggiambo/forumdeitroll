package com.forumdeitroll.profiler2;

import java.io.Serializable;

public class ProfilerRule implements Serializable {
	private String uuid;
	private String label;
	private String code;
	public void setCode(String code) {
		this.code = code;
	}
	public String getCode() {
		return code;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getLabel() {
		return label;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getUuid() {
		return uuid;
	}
}
