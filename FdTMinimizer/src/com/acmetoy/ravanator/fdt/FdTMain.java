package com.acmetoy.ravanator.fdt;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.acmetoy.ravanator.fdt.datafetcher.PageFetcher;

public class FdTMain extends TimerTask {
	
	public static void main(String[] args) {
		FdTMain task = new FdTMain();
		Timer daemon = new Timer();
		daemon.schedule(task, new Date(), 10 * 60 * 1000); // repeat every 10 minutes;
	}

	@Override
	public void run() {
		for (int i = 1; i < 6; i++) {
			new PageFetcher(i).run();
		}
	}
}