package com.forumdeitroll;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class FdTConfig {

	private static final Logger LOG = Logger.getLogger(FdTConfig.class);

	private static Properties properties;

	static {
		properties = new Properties();
		try {
			InputStream is = FdTConfig.class.getClassLoader().getResourceAsStream("config.properties");
			properties.load(is);
		} catch (Exception e) {
			LOG.error("Cannot read properties", e);
		}
	}

	public static String getProperty(String name) {
		return properties.getProperty(name);
	}

	public static Properties getDatabaseConfig(String persistenceName) {
		Properties ret = new Properties();
		String keyPrefix = "persistence." + persistenceName;
		for (String prop : properties.stringPropertyNames()) {
			if (prop.startsWith(keyPrefix)) {
				ret.put( prop.substring(keyPrefix.length() + 1), properties.getProperty(prop));
			}
		}
		return ret;
	}

}
