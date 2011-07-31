package com.acmetoy.ravanator.fdt;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.datafetcher.PageFetcher;
import com.acmetoy.ravanator.fdt.persistence.StatusPersistence;

public class FullUpdater extends TimerTask {

	private static final Logger LOG = Logger.getLogger(FullUpdater.class);

	@Override
	public void run() {
		LOG.info("Start full update");
		for (int i = 1; i < 6; i++) {
			new PageFetcher(i).run();
		}
		LOG.info("End full update");
		try {
			StatusPersistence.getInstance().updateScanDate();
		} catch (Exception e) {
			LOG.error("Cannot update scandate", e);
		}
	}

}
