package com.forumdeitroll.persistence;

import com.forumdeitroll.FdTConfig;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;

public class DAOFactoryForTest extends DAOFactory {

	private static final Logger LOG = Logger.getLogger(DAOFactoryForTest.class);

	private DAOFactoryForTest() throws ClassNotFoundException {
        super();
    }

	public static final synchronized DAOFactoryForTest getInstance() {
		if (instance == null) {
            String persistenceName = "test";
			try {
				instance = new DAOFactoryForTest();
				instance.init(FdTConfig.getDatabaseConfig(persistenceName));
			} catch (Exception e) {
				LOG.fatal("Cannot instantiate Persistence '" + persistenceName + "'", e);
				return null;
			}
		}
		return (DAOFactoryForTest) instance;
	}

    public BasicDataSource getDataSource() {
        return dataSource;
    }


}
