package com.acmetoy.ravanator.fdt;

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
	
	public static Properties getDatabaseConfig(String persistenceNickName) {
		Properties ret = new Properties();
		for (String prop : properties.stringPropertyNames()) {
			if (prop.startsWith(persistenceNickName)) {
				ret.put( prop.substring(persistenceNickName.length() + 1), properties.getProperty(prop));
			}
		}
		return ret;
	}

}
