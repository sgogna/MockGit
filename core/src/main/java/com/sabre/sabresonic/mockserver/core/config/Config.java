package com.sabre.sabresonic.mockserver.core.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public final class Config {

    private static final Logger LOG = LoggerFactory.getLogger(Config.class);
    private static Configuration configuration;

    static {
        try {
            configuration = new PropertiesConfiguration("config/core.properties");
        } catch (ConfigurationException ex) {
            LOG.error(null, ex);
            configuration = new PropertiesConfiguration();
        }
    }

    private Config() {
    }

    public static String getServicePath() {
        return configuration.getString("servicePath", "services");
    }

    public static String getStorePath() {
        return configuration.getString("storePath", "store");
    }

    public static boolean getCheckinDate() {
        return configuration.getBoolean("replaceCheckinDate");
    }
}

