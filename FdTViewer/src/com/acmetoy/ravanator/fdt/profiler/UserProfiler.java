package com.acmetoy.ravanator.fdt.profiler;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.UUID;

public class UserProfiler {
	private static UserProfiler me = null;
	public static UserProfiler getInstance() {
		synchronized (UserProfiler.class) {
			if (me == null)
				me = new UserProfiler();	
		}
		return me;
	}
	
	public ArrayList<UserProfile> profiles = null;
	private UserProfiler() {
		profiles = UserProfilesStorage.load();
	}
	
	private void merge(UserProfile from, UserProfile to) {
		// cronologia pulita?
		to.setPermr(from.getPermr());
		to.setEtag(from.getEtag());
		to.setUltimoRiconoscimentoUtente(System.currentTimeMillis());
		
		if (to.getIpAddresses() == null)
			to.setIpAddresses(new TreeSet<String>());
		to.getIpAddresses().add(from.getIpAddress());
		
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
	
	public UserProfile guess(UserProfile candidate) {
		UserProfile maybe = null;
		boolean isMaybe = false;
		for (UserProfile profile : profiles) {
			// 100% sgamatrollato
			if (candidate.getNick() != null && profile.getNicknames().contains(candidate.getNick())) {
				merge(candidate, profile);
				return profile;
			}
			if (profile.getIpAddresses().contains(candidate.getIpAddress())) {
				merge(candidate, profile);
				return profile;
			}
			if (profile.getPermr().equals(candidate.getPermr())) {
				merge(candidate, profile);
				return profile;
			}
			if (profile.getEtag().equals(candidate.getEtag())) {
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
}
