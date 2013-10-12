package com.forumdeitroll.profiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.TreeSet;
import java.util.UUID;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import org.apache.log4j.Logger;

import com.forumdeitroll.util.CacheTorExitNodes;

public class UserProfiler {
	private static UserProfiler me = null;
	public static UserProfiler getInstance() {
		synchronized (UserProfiler.class) {
			if (me == null) {
				me = new UserProfiler();
				try {
					me.preParse();
				} catch (Exception e) {
					Logger.getLogger(UserProfiler.class).error(e.getClass().getName()+": "+e.getMessage(), e);
				}
			}
		}
		return me;
	}
	
	public ArrayList<UserProfile> profiles = null;
	private UserProfiler() {
		profiles = UserProfilesStorage.load();
	}
	
	// cache di user agent gia' parsati
	private HashMap<String, ReadableUserAgent> knownUA = new HashMap<String, ReadableUserAgent>();
	private UserAgentStringParser uaParser = UADetectorServiceFactory.getCachingAndUpdatingParser();
	private ReadableUserAgent parseUA(String ua) {
		if (knownUA.containsKey(ua)) return knownUA.get(ua);
		ReadableUserAgent userAgent =uaParser.parse(ua);
		knownUA.put(ua, userAgent);
		return userAgent;
	}
	
	public void preParse() {
		Logger.getLogger(UserProfiler.class).info("Precaricamento UserAgents conosciuti");
		for (UserProfile profile : profiles) {
			for (String ua : profile.getUserAgents()) {
				Logger.getLogger(UserProfiler.class).info("Parse \""+ua+"\"");
				parseUA(ua); // parsa e carica in memoria gli oggetti
			}
		}
		Logger.getLogger(UserProfiler.class).info("Precaricamento terminato");
	}
	
	// per rilevare un aggiornamento del browser vedo se è abbastanza simile ad altri
	// UADetector prende i dati da http://user-agent-string.info/
	private boolean closeEnough(ReadableUserAgent ua1, ReadableUserAgent ua2) {
		return ua1.getFamily().equals(ua2.getFamily()) &&
				ua1.getOperatingSystem().getName().equals(ua2.getOperatingSystem().getName());
	}
	
	private void merge(UserProfile from, UserProfile to) {
		// cronologia pulita?
		to.setPermr(from.getPermr());
		to.setEtag(from.getEtag());
		to.setUltimoRiconoscimentoUtente(System.currentTimeMillis());
		
		if (to.getIpAddresses() == null)
			to.setIpAddresses(new TreeSet<String>());
		if (!CacheTorExitNodes.check(from.getIpAddress())) {
			to.getIpAddresses().add(from.getIpAddress());	
		}
		
		if (to.getNicknames() == null)
			to.setNicknames(new TreeSet<String>());
		if (from.getNick() != null)
			to.getNicknames().add(from.getNick());
		
		if (to.getUserAgents() == null)
			to.setUserAgents(new TreeSet<String>());
		if (from.getUa() != null)
			to.getUserAgents().add(from.getUa());
		
		if (to.getScreenResolutions() == null)
			to.setScreenResolutions(new TreeSet<String>());
		if (from.getScreenres() != null)
			to.getScreenResolutions().add(from.getScreenres());
		
		if (to.getPluginHashes() == null)
			to.setPluginHashes(new TreeSet<String>());
		if (from.getPlugins() != null)
			to.getPluginHashes().add(from.getPlugins());
	}
	
	public boolean isProfilerEnabled = true;
	
	public UserProfile guess(UserProfile candidate) {
		if ( ! isProfilerEnabled ) return candidate;
		UserProfile maybe = null;
		boolean isMaybe = false;
		for (UserProfile profile : profiles) {
			// 100% sgamatrollato
			if (candidate.getNick() != null && profile.getNicknames().contains(candidate.getNick())) {
				merge(candidate, profile);
				return profile;
			}
			if (profile.getIpAddresses().contains(candidate.getIpAddress())
					&& !CacheTorExitNodes.check(candidate.getIpAddress())) {
				merge(candidate, profile);
				return profile;
			}
			if (profile.getPermr() != null && profile.getPermr().equals(candidate.getPermr())) {
				merge(candidate, profile);
				return profile;
			}
			if (profile.getEtag() != null && profile.getEtag().equals(candidate.getEtag())) {
				merge(candidate, profile);
				return profile;
			}
			// sia data l'univocita' al plugin hash, sia messo a verbale
			if (profile.getPluginHashes().contains(candidate.getPlugins())) {
				merge(candidate, profile);
				return profile;
			}
			
			// 50% sgamatrollato, maybe solo se unico
			if (candidate.getPlugins() != null && candidate.getUa() != null && candidate.getScreenres() != null) {
				if (	profile.getPluginHashes().contains(candidate.getPlugins()) &&
						profile.getUserAgents().contains(candidate.getUa()) &&
						profile.getScreenResolutions().contains(candidate.getScreenres())) {
					if (maybe == null) {
						maybe = profile;
						isMaybe = true;
					} else {
						isMaybe = false;
					}
				}	
			}
			try {
				for (String profileUA : profile.getUserAgents()) {
					if (closeEnough(parseUA(candidate.getUa()), parseUA(profileUA)) &&
							profile.getPluginHashes().contains(candidate.getPlugins())) {
						if (maybe == null) {
							maybe = profile;
							isMaybe = true;
						} else {
							isMaybe = false;
						}
						break;
					}
				}
				
			} catch (Exception e) {
				Logger.getLogger(UserProfiler.class).error("ERRORE NEL PARSE DELL'USER AGENT "+candidate.getUa()+" "+e.getClass().getName()+": "+e.getMessage(), e);
			}
		}
		if (maybe == null || !isMaybe) {
			merge(candidate, candidate);
			candidate.setUuid(UUID.randomUUID().toString());
			profiles.add(candidate);
			return candidate;	
		} else {
			merge(candidate, maybe);
			return maybe;
		}
	}
	
	public void bind(UserProfile userProfile, String m_id) {
		if (userProfile.getMsgIds() == null)
			userProfile.setMsgIds(new TreeSet<String>());
		userProfile.getMsgIds().add(m_id);
		if (userProfile.getMsgIds().size() >= 100) { // max 100 msg per utente
			Iterator<String> msgIt = userProfile.getMsgIds().iterator();
			msgIt.next();
			msgIt.remove();
		}
	}
	
	public UserProfile lookup(String uuid) {
		for (UserProfile profile: profiles) {
			if (profile.getUuid().equals(uuid))
				return profile;
		}
		return null;
	}
	
	private TreeSet<String> safeAddAll(TreeSet<String> to, TreeSet<String> from) {
		if (to == null)
			to = new TreeSet<String>();
		if (from != null && from.size() > 0) {
			to.addAll(from);
		}
		return to;
	}
	
	public UserProfile mergeKnownProfiles(String one, String two) {
		UserProfile onep = lookup(one), twop = lookup(two);
		UserProfile from, to;
		if (onep.getUltimoRiconoscimentoUtente() < twop.getUltimoRiconoscimentoUtente()) {
			from = onep;
			to = twop;
		} else {
			from = twop;
			to = onep;
		}
		to.setIpAddresses(safeAddAll(to.getIpAddresses(), from.getIpAddresses()));
		to.setMsgIds(safeAddAll(to.getMsgIds(), from.getMsgIds()));
		to.setNicknames(safeAddAll(to.getNicknames(), from.getNicknames()));
		to.setPluginHashes(safeAddAll(to.getPluginHashes(), from.getPluginHashes()));
		to.setScreenResolutions(safeAddAll(to.getScreenResolutions(), from.getScreenResolutions()));
		to.setUserAgents(safeAddAll(to.getUserAgents(), from.getUserAgents()));
		to.setBannato(to.isBannato() || from.isBannato());
		profiles.remove(from);
		return to;
	}
	
	public void cleanup() {
		synchronized (profiles) {
			for (ListIterator<UserProfile> pIt = profiles.listIterator(); pIt.hasNext();) {
				UserProfile profile = pIt.next();
				if (!profile.isBannato()) {
					pIt.remove();
				}
			}
		}
	}
}
