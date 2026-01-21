package com.kuzetech.bigdata.pulsar.eos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;

public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    public static AppConfig loadConfig(String configPath) throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        AppConfig config;

        if (configPath != null && !configPath.isEmpty()) {
            // 从指定路径加载配置
            logger.info("Loading config from file: {}", configPath);
            File configFile = new File(configPath);
            if (!configFile.exists()) {
                throw new IllegalArgumentException("Config file not found: " + configPath);
            }
            config = mapper.readValue(configFile, AppConfig.class);
        } else {
            // 从 classpath 加载默认配置
            logger.info("Loading default config from classpath");
            InputStream is = ConfigLoader.class.getClassLoader().getResourceAsStream("config.yaml");
            if (is == null) {
                throw new IllegalStateException("Default config.yaml not found in classpath");
            }
            config = mapper.readValue(is, AppConfig.class);
        }

        logger.info("Configuration loaded successfully");
        return config;
    }

    public static void overrideWithCommandLine(AppConfig config, String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--file") && i + 1 < args.length) {
                config.getFile().setPath(args[++i]);
                logger.info("Overriding file path from command line: {}", config.getFile().getPath());
            } else if (args[i].equals("--topic") && i + 1 < args.length) {
                config.getPulsar().setTopic(args[++i]);
                logger.info("Overriding topic from command line: {}", config.getPulsar().getTopic());
            } else if (args[i].equals("--pulsar-url") && i + 1 < args.length) {
                config.getPulsar().setServiceUrl(args[++i]);
                logger.info("Overriding Pulsar URL from command line: {}", config.getPulsar().getServiceUrl());
            } else if (args[i].equals("--batch-size") && i + 1 < args.length) {
                config.getFile().setBatchSize(Integer.parseInt(args[++i]));
                logger.info("Overriding batch size from command line: {}", config.getFile().getBatchSize());
            }
        }
    }
}
