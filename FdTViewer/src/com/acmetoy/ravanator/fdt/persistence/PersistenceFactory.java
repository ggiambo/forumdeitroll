package com.acmetoy.ravanator.fdt.persistence;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.FdTConfig;

public abstract class PersistenceFactory {

	private static final Logger LOG = Logger.getLogger(PersistenceFactory.class);

	private static IPersistence instance;

	public static synchronized IPersistence getInstance() throws Exception {
		if (instance == null) {
			try {
				String persistenceNickName = FdTConfig.getProperty("persistence.nickName");
				String databaseClass = FdTConfig.getProperty(persistenceNickName + ".class");
				Class<? extends IPersistence> c = Class.forName(databaseClass).asSubclass(IPersistence.class);
				Constructor<? extends IPersistence> cons = c.getConstructor();
				instance = cons.newInstance();
				instance.init(FdTConfig.getDatabaseConfig(persistenceNickName));
			} catch (Exception e) {
				LOG.error("Cannot instantiate Persistence " + FdTConfig.getProperty("persistence.nickName"), e);
				throw e;
			}
		}
		return instance;
	}

}
