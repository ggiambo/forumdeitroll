package com.forumdeitroll.profiler2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

public class ProfilerStorage {
	//public static String baseDir = "/opt/fdt/";
	public static String baseDir = "/home/milky/fdt/webdev/";
	public static void load() {
		Object o = load(baseDir + "rules.javabean");
		if (o != null) {
			ProfilerRules.rules = (ArrayList<ProfilerRule>) o;
		}
		o = load(baseDir + "records.javabean");
		if (o != null) {
			ProfilerLogger.records = (LinkedList<ProfileRecord>) o;
		}
	}
	public static void save() {
		save(baseDir + "rules.javabean", ProfilerRules.rules);
		save(baseDir + "records.javabean", ProfilerLogger.records);
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
			e.printStackTrace();
		}
	}
}
