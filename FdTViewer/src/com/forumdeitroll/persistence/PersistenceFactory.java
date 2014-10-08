package com.forumdeitroll.persistence;

import org.apache.log4j.Logger;

import com.forumdeitroll.FdTConfig;
import com.forumdeitroll.persistence.dao.DAOFacade;

public abstract class PersistenceFactory {

	private static final Logger LOG = Logger.getLogger(PersistenceFactory.class);

	private static IPersistence instance;

	public static synchronized IPersistence getInstance() throws Exception {
		if (instance == null) {
			try {
				instance = new DAOFacade();
				String persistenceName = FdTConfig.getProperty("persistence.name");
				instance.init(FdTConfig.getDatabaseConfig(persistenceName));
			} catch (Exception e) {
				LOG.error("Cannot instantiate Persistence " + FdTConfig.getProperty("persistence.name"), e);
				throw e;
			}
		}
		return instance;
	}

}
