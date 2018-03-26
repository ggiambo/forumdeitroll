package com.forumdeitroll.profiler2;

import java.io.Serializable;

public class ProfileRecord implements Serializable {
	private static final long serialVersionUID = -1290457865531419544l;
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
	public String getLabelLink() {
		if (label.startsWith("message-post-")) {
			String id = getMsgId();
			return String.format(
					"<a name=\"%s\" href=\"Messages?action=getById&amp;msgId=%s\">%s</a>"
					, label, id, label);
		}
		return String.format("<a name=\"%s\">%s</a>", label, label);
	}
	public String getMsgId() {
		return label.substring("message-post-".length());
	}
}
