package com.forumdeitroll.profiler2;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.util.CacheTorExitNodes;
import com.forumdeitroll.util.GeoIP;
import com.forumdeitroll.util.IPMemStorage;
import com.google.gson.Gson;

public class ReqInfo implements Serializable {
	private String permr, etag, plugins, ua, screenres;
	private String ipAddress, nick;
	private boolean tor;
	private Map<String, String> geoip;
	public ReqInfo(HttpServletRequest req, AuthorDTO author) {
		ReqInfo that = new Gson().fromJson(
			req.getParameter("jsonProfileData"), ReqInfo.class);
		this.permr = that.permr;
		this.etag = that.etag;
		this.plugins = that.plugins;
		this.ua = that.ua;
		this.screenres = that.screenres;
		this.ipAddress = IPMemStorage.requestToIP(req);
		this.nick = author.getNick();
		this.tor = CacheTorExitNodes.check(this.ipAddress);
		this.geoip = GeoIP.lookup(this.ipAddress);
	}
	public String getPermr() {
		return permr;
	}
	public void setPermr(String permr) {
		this.permr = permr;
	}
	public String getEtag() {
		return etag;
	}
	public void setEtag(String etag) {
		this.etag = etag;
	}
	public String getPlugins() {
		return plugins;
	}
	public void setPlugins(String plugins) {
		this.plugins = plugins;
	}
	public String getUa() {
		return ua;
	}
	public void setUa(String ua) {
		this.ua = ua;
	}
	public String getScreenres() {
		return screenres;
	}
	public void setScreenres(String screenres) {
		this.screenres = screenres;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public boolean isTor() {
		return tor;
	}
	public void setTor(boolean tor) {
		this.tor = tor;
	}
	public Map<String, String> getGeoip() {
		return geoip;
	}
	public void setGeoip(Map<String, String> geoip) {
		this.geoip = geoip;
	}
}
