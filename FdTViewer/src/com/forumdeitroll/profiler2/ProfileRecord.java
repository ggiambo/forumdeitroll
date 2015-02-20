package com.forumdeitroll.profiler2;

import java.io.Serializable;

public class ProfileRecord implements Serializable {
	private ReqInfo reqInfo;
	private long tstamp;
	private String label;
	public ReqInfo getReqInfo() {
		return reqInfo;
	}
	public void setReqInfo(ReqInfo reqInfo) {
		this.reqInfo = reqInfo;
	}
	public long getTstamp() {
		return tstamp;
	}
	public void setTstamp(long tstamp) {
		this.tstamp = tstamp;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
