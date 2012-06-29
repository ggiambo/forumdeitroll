package com.forumdeitroll.profiler;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeSet;

public class UserProfile implements Serializable {
	
	private static final long serialVersionUID = 6133184658596477756L;
	
	// dati forniti da esterno
	private String permr, etag, plugins, ua, screenres;
	// assegnati dal server
	private String ipAddress, nick;
	// dati noti del profilo
	private String uuid;
	private TreeSet<String> nicknames = null;
	private TreeSet<String> ipAddresses = null;
	private TreeSet<String> userAgents = null;
	private TreeSet<String> screenResolutions = null;
	private TreeSet<String> pluginHashes = null;
	private TreeSet<String> msgIds = null;
	// altri dati
	private long ultimoRiconoscimentoUtente = 0L;
	// IL booleano
	private boolean bannato = false;
	
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
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public TreeSet<String> getNicknames() {
		return nicknames;
	}
	public void setNicknames(TreeSet<String> nicknames) {
		this.nicknames = nicknames;
	}
	public TreeSet<String> getIpAddresses() {
		return ipAddresses;
	}
	public void setIpAddresses(TreeSet<String> ipAddresses) {
		this.ipAddresses = ipAddresses;
	}
	public TreeSet<String> getUserAgents() {
		return userAgents;
	}
	public void setUserAgents(TreeSet<String> userAgents) {
		this.userAgents = userAgents;
	}
	public TreeSet<String> getScreenResolutions() {
		return screenResolutions;
	}
	public void setScreenResolutions(TreeSet<String> screenResolutions) {
		this.screenResolutions = screenResolutions;
	}
	public TreeSet<String> getPluginHashes() {
		return pluginHashes;
	}
	public void setPluginHashes(TreeSet<String> pluginHashes) {
		this.pluginHashes = pluginHashes;
	}
	public long getUltimoRiconoscimentoUtente() {
		return ultimoRiconoscimentoUtente;
	}
	public Date getUltimoRiconoscimentoUtenteDate() {
		return new Date(ultimoRiconoscimentoUtente);
	}
	public void setUltimoRiconoscimentoUtente(long ultimoRiconoscimentoUtente) {
		this.ultimoRiconoscimentoUtente = ultimoRiconoscimentoUtente;
	}
	public TreeSet<String> getMsgIds() {
		return msgIds;
	}
	public void setMsgIds(TreeSet<String> msgIds) {
		this.msgIds = msgIds;
	}
	public boolean isBannato() {
		return bannato;
	}
	public void setBannato(boolean bannato) {
		this.bannato = bannato;
	}
}
