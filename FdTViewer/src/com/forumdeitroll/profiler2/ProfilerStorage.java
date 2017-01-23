package com.forumdeitroll.profiler2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.forumdeitroll.servlets.Messages;

public class ProfilerStorage {
	public static String baseDir = "/opt/fdt/";
	public static void load() {
		Object o = load(baseDir + "rules.javabean");
		if (o != null) {
			ProfilerRules.rules = (ArrayList<ProfilerRule>) o;
		}
		o = load(baseDir + "records.javabean");
		if (o != null) {
			ProfilerLogger.records = (LinkedList<ProfileRecord>) o;
		}
		o = load(baseDir + "bannedIPs.javabean");
		if (o != null) {
			Messages.BANNED_IPs.clear();
			Messages.BANNED_IPs.addAll((Collection<String>) o);
		}
	}
	public static void save() {
		save(baseDir + "rules.javabean", ProfilerRules.rules);
		save(baseDir + "records.javabean", ProfilerLogger.records);
		save(baseDir + "bannedIPs.javabean", Messages.BANNED_IPs);
	}
	private static Object load(String path) {
		try {
			FileInputStream in = new FileInputStream(path);
			ObjectInputStream oin = new ObjectInputStream(in);
			Object o = oin.readObject();
			oin.close();
			in.close();
			return o;
		} catch (Exception e) {
			Logger.getLogger(ProfilerStorage.class).error(e);
			return null;
		}
	}
	private static void save(String path, Object o) {
		try {
			FileOutputStream out = new FileOutputStream(path);
			ObjectOutputStream oout = new ObjectOutputStream(out);
			oout.writeObject(o);
			oout.close();
			out.close();
		} catch (Exception e) {
			Logger.getLogger(ProfilerStorage.class).error(e);
		}
	}
}