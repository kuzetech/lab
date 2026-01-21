package com.kuzetech.bigdata.pulsar.eos.config;

import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigLoaderTest {

    @Test
    public void testLoadDefaultConfig() throws Exception {
        AppConfig config = ConfigLoader.loadConfig(null);
        assertNotNull("Config should not be null", config);
        assertNotNull("Pulsar config should not be null", config.getPulsar());
        assertNotNull("MySQL config should not be null", config.getMysql());
        assertNotNull("File config should not be null", config.getFile());
        assertNotNull("Retry config should not be null", config.getRetry());
    }

    @Test
    public void testCommandLineOverride() throws Exception {
        AppConfig config = ConfigLoader.loadConfig(null);
        String[] args = {"--file", "/custom/path/test.log", "--batch-size", "200"};

        ConfigLoader.overrideWithCommandLine(config, args);

        assertEquals("File path should be overridden", "/custom/path/test.log", config.getFile().getPath());
        assertEquals("Batch size should be overridden", Integer.valueOf(200), config.getFile().getBatchSize());
    }

    @Test
    public void testPulsarConfigDefaults() throws Exception {
        AppConfig config = ConfigLoader.loadConfig(null);

        assertEquals("Default Pulsar URL", "pulsar://localhost:6650", config.getPulsar().getServiceUrl());
        assertEquals("Default topic", "persistent://public/default/log-messages", config.getPulsar().getTopic());
        assertNotNull("Producer config should exist", config.getPulsar().getProducer());
    }

    @Test
    public void testMysqlConfigDefaults() throws Exception {
        AppConfig config = ConfigLoader.loadConfig(null);

        assertEquals("Default MySQL host", "localhost", config.getMysql().getHost());
        assertEquals("Default MySQL port", Integer.valueOf(3306), config.getMysql().getPort());
        assertEquals("Default database", "pulsar_offset", config.getMysql().getDatabase());

        String jdbcUrl = config.getMysql().getJdbcUrl();
        assertTrue("JDBC URL should contain host", jdbcUrl.contains("localhost"));
        assertTrue("JDBC URL should contain port", jdbcUrl.contains("3306"));
        assertTrue("JDBC URL should contain database", jdbcUrl.contains("pulsar_offset"));
    }

    @Test
    public void testRetryConfigDefaults() throws Exception {
        AppConfig config = ConfigLoader.loadConfig(null);

        assertNotNull("Max attempts should be set", config.getRetry().getMaxAttempts());
        assertNotNull("Delay should be set", config.getRetry().getDelayMs());
        assertNotNull("Multiplier should be set", config.getRetry().getMultiplier());
        assertNotNull("Max delay should be set", config.getRetry().getMaxDelayMs());
    }
}
