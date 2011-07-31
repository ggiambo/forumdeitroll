package com.acmetoy.ravanator.fdt;

import java.util.Date;
import java.util.Timer;

public class FdTMain  {
	
	public static void main(String[] args) {
		Timer daemon = new Timer();
		daemon.schedule(new FullUpdater(), new Date(), 10 * 60 * 1000); // full update every 10 minutes
		daemon.schedule( new SinglePageUpdater(), 1 * 60 * 1000, 1 * 60 * 1000); // single update every minute
	}

}