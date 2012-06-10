package com.acmetoy.ravanator.fdt.util;

import java.util.Set;
import java.util.HashSet;
import java.net.InetAddress;
import java.net.InetAddress;
import java.net.Socket;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.apache.log4j.Logger;
import com.acmetoy.ravanator.fdt.SingleValueCache;

public class CacheTorExitNodes extends SingleValueCache<Set<String>> {
	private static final Logger LOG = Logger.getLogger(CacheTorExitNodes.class);
	protected static final CacheTorExitNodes INSTANCE = new CacheTorExitNodes(60 * 60 * 1000);

	public CacheTorExitNodes(final long interval) {
		super(interval);
	}

	public static boolean check(final String ip) {
		return INSTANCE.get().contains(ip);
	}

	@Override protected Set<String> update() {
		Set<String> res = new HashSet<String>();
		try {
		    InetAddress addr = InetAddress.getByName("torstatus.blutmagie.de");
		    Socket socket = new Socket(addr, 80);
		    BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
		    wr.write("GET /ip_list_exit.php/Tor_ip_list_EXIT.csv HTTP/1.0\r\n");
		    wr.write("Content-Type: application/x-www-form-urlencoded\r\n");
		    wr.write("\r\n");
		    wr.flush();
		    // get response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    String line;
		    // si si, lo so, non stressatemi :@ !
		    String simpleIPv4 = "\\d+\\.\\d+\\.\\d+\\.\\d+";
		    while ((line = rd.readLine()) != null) {
		    	if (line.matches(simpleIPv4)) {
		        	res.add(line.trim());
		        }
		    }
		    wr.close();
		    rd.close();
		} catch (Exception e) {
			LOG.error("Cannot read tor exit list ", e);
		}
		return res;
	}
}
