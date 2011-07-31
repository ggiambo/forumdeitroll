package com.acmetoy.ravanator.fdt;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.datafetcher.PageFetcher;
import com.acmetoy.ravanator.fdt.persistence.StatusPersistence;

public class SinglePageUpdater extends TimerTask {

	private static final Logger LOG = Logger.getLogger(SinglePageUpdater.class);

	@Override
	public void run() {
		LOG.info("Start single update");
		new PageFetcher(1).run();
		LOG.info("End full update");
		try {
			StatusPersistence.getInstance().updateScanDate();
		} catch (Exception e) {
			LOG.error("Cannot update scandate", e);
		}
	}

}
