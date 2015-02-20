package com.forumdeitroll.profiler2;

import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

public class ProfilerRules {
	public static ArrayList<ProfilerRule> rules = new ArrayList<ProfilerRule>();
	private static String prefix = "(function(reqInfo) {";
	private static String suffix = "})(reqInfo)";

	public static String checkRules(ReqInfo reqInfo) {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		engine.put("reqInfo", reqInfo);
		for (ProfilerRule rule : rules) {
			try {
				Boolean result = (Boolean) engine.eval(prefix + rule.getCode() + suffix);
				if (result) {
					return rule.getLabel();
				}
			} catch (ScriptException e) {
				Logger.getLogger(ProfilerRules.class).error("Error executing rule " + rule.getLabel(), e);
			}
		}
		return null;
	}

	public static String checkRule(String code, ReqInfo reqInfo) {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		engine.put("reqInfo", reqInfo);
		try {
			Boolean result = (Boolean) engine.eval(prefix + code + suffix);
			return String.valueOf(result);
		} catch (ScriptException e) {
			return e.getMessage();
		}
	}
}
