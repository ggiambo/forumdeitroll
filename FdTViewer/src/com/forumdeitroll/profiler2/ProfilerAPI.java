package com.forumdeitroll.profiler2;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.forumdeitroll.persistence.AuthorDTO;

public class ProfilerAPI {
	public static boolean enabled = true;
	static {
		ProfilerStorage.load();
	}
	public static boolean blockedByRules(HttpServletRequest req, AuthorDTO author) {
		if (!enabled) {
			return false;
		}
		ReqInfo reqInfo = new ReqInfo(req, author);
		String ruleLabel = ProfilerRules.checkRules(reqInfo);
		if (ruleLabel != null) {
			Logger.getLogger(ProfilerAPI.class).error("Request blocked by rule " + ruleLabel);
			ProfilerLogger.log(reqInfo, "blockedByRule-" + ruleLabel);
			return true;
		}
		return false;
	}
	public static void log(HttpServletRequest req, AuthorDTO author, String label) {
		ProfilerLogger.log(new ReqInfo(req, author), label);
		ProfilerStorage.save();
	}
	public static void logSimple(String label) {
		ProfilerLogger.logSimple(label);
		ProfilerStorage.save();
	}
}
