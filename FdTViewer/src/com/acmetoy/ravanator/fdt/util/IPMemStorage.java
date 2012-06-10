package com.acmetoy.ravanator.fdt.util;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import javax.servlet.http.HttpServletRequest;

public class IPMemStorage {
	private static int STORE_SIZE = 1000;
	private static Record[] DATA = new Record[STORE_SIZE];

	public static class Record {
		protected final String m_id;
		protected final String ip;
		protected final String authorDescription;

		public Record(final String m_id, final String ip, final String authorDescription) {
			this.m_id = m_id;
			this.ip = ip;
			this.authorDescription = authorDescription;
		}

		public String m_id() { return m_id; }
		public String ip() { return ip; }
		public String authorDescription() { return authorDescription; };

		public String toString() {
			return "" + m_id + ": " + authorDescription + " (" + ip + ")";
		}
	}

	public static void store(String ip, String m_id, AuthorDTO author) {
		DATA[Integer.parseInt(m_id) % STORE_SIZE] = new Record(m_id, ip, author.description());
	}

	public static String requestToIP(final HttpServletRequest req) {
		final String forwarded = req.getHeader("X-Forwarded-For");
		return (forwarded != null) ? forwarded : req.getRemoteAddr();
	}

	public static void store(HttpServletRequest request, String m_id, AuthorDTO author) {
		store(requestToIP(request), m_id, author);
	}

	public static Record get(String m_id) {
		Record value = DATA[ Integer.parseInt(m_id) % STORE_SIZE ];
		if (value == null) return null;
		return (m_id.equals(value.m_id())) ? value : null;
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
			store(ip, m_id, new AuthorDTO(null));
			Record retrieved = get(m_id);
			if (!retrieved.ip().equals(ip)) {
				System.err.println("m_id: "+m_id+"\tip: "+ip);
				System.err.println("ip_retrieved: "+retrieved);
				throw new RuntimeException();
			}
		}
	}
}