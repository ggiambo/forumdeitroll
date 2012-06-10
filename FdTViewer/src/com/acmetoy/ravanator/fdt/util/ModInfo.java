package com.acmetoy.ravanator.fdt.util;

public class ModInfo {
	public final String m_id;
	public final String authorDescription;
	public final String ip;
	public final boolean tor;

	public ModInfo(final String m_id, final IPMemStorage.Record record) {
		this.m_id = m_id;
		if (record != null) {
			this.authorDescription = "" + record.authorAppearance() + "/" + record.authorNickname();
			this.ip = record.ip();
			this.tor = CacheTorExitNodes.check(ip);
		} else {
			this.authorDescription = "boh";
			this.ip = "mah";
			this.tor = false;
		}
	}

	public String getM_id() { return m_id; }
	public String getIp() { return ip; }
	public boolean getTor() { return tor; }
	public String getAuthorDescription() { return authorDescription; }
}