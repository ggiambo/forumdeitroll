package com.acmetoy.ravanator.fdt.persistence;

import java.lang.reflect.Constructor;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.FdTConfig;

public class PersistenceFactory {

	private static final Logger LOG = Logger.getLogger(PersistenceFactory.class);

	private static Persistence persistence;

	private static PersistenceFactory instance;

	private PersistenceFactory() {
	}

	public static Persistence getPersistence() throws Exception {
		if (instance == null) {
			instance = new PersistenceFactory();
			try {
				String persistenceNickName = FdTConfig.getProperty("persistence.nickName");
				String databaseClass = FdTConfig.getProperty(persistenceNickName + ".class");
				Class<? extends Persistence> c = Class.forName(databaseClass).asSubclass(Persistence.class);
				Constructor<? extends Persistence> cons = c.getConstructor(Properties.class);
				persistence = cons.newInstance(FdTConfig.getDatabaseConfig(persistenceNickName));
			} catch (Exception e) {
				LOG.error("Cannot instantiate Persistence " + FdTConfig.getProperty("persistence.nickName"), e);
				throw e;
			}
		}
		return persistence;
	}
}
