package com.forumdeitroll.profiler2;

import java.util.LinkedList;

public class ProfilerLogger {
	public static LinkedList<ProfileRecord> records =
		new LinkedList<ProfileRecord>();
	public static void log(ReqInfo reqInfo, String label) {
		ProfileRecord record = new ProfileRecord();
		record.setReqInfo(reqInfo);
		record.setLabel(label);
		record.setTstamp(System.currentTimeMillis());
		records.addFirst(record);
		while (records.size() > 1000) {
			records.removeLast();
		}
	}
	public static void logSimple(String label) {
		ProfileRecord record = new ProfileRecord();
		record.setTstamp(System.currentTimeMillis());
		record.setLabel(label);
		records.addFirst(record);
		while (records.size() > 1000) {
			records.removeLast();
		}
	}
}
