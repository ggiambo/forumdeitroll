package com.acmetoy.ravanator.fdt.util;

import javax.servlet.http.HttpServletRequest;

public class IPMemStorage {

	private static int STORE_SIZE = 1000;
	private static String[] DATA = new String[STORE_SIZE];

	public static void store(String ip, String m_id) {
		DATA[Integer.parseInt(m_id) % STORE_SIZE] = m_id + "|" + ip;
	}

	public static String requestToIP(final HttpServletRequest req) {
		final String forwarded = req.getHeader("X-Forwarded-For");
		return (forwarded != null) ? forwarded : req.getRemoteAddr();
	}

	public static void store(HttpServletRequest request, String m_id) {
		store(requestToIP(request), m_id);
	}

	public static String getIp(String m_id) {
		String value = DATA[ Integer.parseInt(m_id) % STORE_SIZE ];
		if (value == null) return null;
		if (m_id.equals(value.substring(0, value.indexOf('|')))) {
			return value.substring(value.indexOf('|')+1);
		}
		return null;
	}

	// test
	public static int getRndNum(int max) {
		return (int) (max * Math.random());
	}
	public static String getRndIp() {
		return getRndNum(255) + "." + getRndNum(255) + "." + getRndNum(255) + "." + getRndNum(255);
	}
	public static String getRndMid() {
		return String.valueOf(getRndNum(Integer.MAX_VALUE / 2));
	}

	public static void main(String[] args) throws Exception {
		for (int i=0;i<10000000;i++) {
			String ip = getRndIp();
			String m_id = getRndMid();
			store(ip, m_id);
			String ip_retrieved = getIp(m_id);
			if (!ip_retrieved.equals(ip)) {
				System.err.println("m_id: "+m_id+"\tip: "+ip);
				System.err.println("ip_retrieved: "+ip_retrieved);
				throw new RuntimeException();
			}
		}
	}
}
