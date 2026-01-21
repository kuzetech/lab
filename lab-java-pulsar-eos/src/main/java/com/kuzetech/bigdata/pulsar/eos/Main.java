package com.kuzetech.bigdata.pulsar.eos;

import com.kuzetech.bigdata.pulsar.eos.config.AppConfig;
import com.kuzetech.bigdata.pulsar.eos.config.ConfigLoader;
import com.kuzetech.bigdata.pulsar.eos.manager.OffsetManager;
import com.kuzetech.bigdata.pulsar.eos.manager.PulsarProducerManager;
import com.kuzetech.bigdata.pulsar.eos.processor.FileProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("Pulsar EOS File Processor Starting...");
        logger.info("========================================");

        String configPath = null;

        // 解析命令行参数获取配置文件路径
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--config") && i + 1 < args.length) {
                configPath = args[++i];
                break;
            }
        }

        try {
            // 加载配置
            logger.info("Loading configuration...");
            AppConfig config = ConfigLoader.loadConfig(configPath);

            // 命令行参数覆盖配置
            ConfigLoader.overrideWithCommandLine(config, args);

            // 验证配置
            validateConfig(config);

            // 打印配置信息
            printConfig(config);

            // 初始化资源
            logger.info("Initializing resources...");

            try (OffsetManager offsetManager = new OffsetManager(config.getMysql());
                 PulsarProducerManager pulsarManager = new PulsarProducerManager(config.getPulsar())) {

                // 创建文件处理器
                FileProcessor processor = new FileProcessor(config, pulsarManager, offsetManager);

                // 处理文件
                long startTime = System.currentTimeMillis();
                processor.processFile(config.getFile().getPath());
                long endTime = System.currentTimeMillis();

                logger.info("========================================");
                logger.info("Processing completed successfully!");
                logger.info("Total time: {} ms", endTime - startTime);
                logger.info("========================================");

            } catch (Exception e) {
                logger.error("Error during file processing", e);
                System.exit(1);
            }

        } catch (Exception e) {
            logger.error("Fatal error", e);
            printUsage();
            System.exit(1);
        }
    }

    private static void validateConfig(AppConfig config) {
        if (config.getFile() == null || config.getFile().getPath() == null || config.getFile().getPath().isEmpty()) {
            throw new IllegalArgumentException("File path is required");
        }
        if (config.getPulsar() == null || config.getPulsar().getServiceUrl() == null) {
            throw new IllegalArgumentException("Pulsar service URL is required");
        }
        if (config.getPulsar().getTopic() == null || config.getPulsar().getTopic().isEmpty()) {
            throw new IllegalArgumentException("Pulsar topic is required");
        }
        if (config.getMysql() == null || config.getMysql().getHost() == null) {
            throw new IllegalArgumentException("MySQL configuration is required");
        }
    }

    private static void printConfig(AppConfig config) {
        logger.info("Configuration:");
        logger.info("  File Path: {}", config.getFile().getPath());
        logger.info("  Batch Size: {}", config.getFile().getBatchSize());
        logger.info("  Pulsar URL: {}", config.getPulsar().getServiceUrl());
        logger.info("  Pulsar Topic: {}", config.getPulsar().getTopic());
        logger.info("  Transaction Enabled: {}", config.getPulsar().getProducer().getEnableTransaction());
        logger.info("  MySQL Host: {}:{}", config.getMysql().getHost(), config.getMysql().getPort());
        logger.info("  MySQL Database: {}", config.getMysql().getDatabase());
        logger.info("  Max Retry Attempts: {}", config.getRetry().getMaxAttempts());
    }

    private static void printUsage() {
        System.out.println("\nUsage: java -jar lab-java-pulsar-eos.jar [OPTIONS]");
        System.out.println("\nOptions:");
        System.out.println("  --config <path>       Path to config.yaml file (default: classpath:config.yaml)");
        System.out.println("  --file <path>         Path to log file to process (overrides config)");
        System.out.println("  --topic <topic>       Pulsar topic name (overrides config)");
        System.out.println("  --pulsar-url <url>    Pulsar service URL (overrides config)");
        System.out.println("  --batch-size <size>   Batch size for processing (overrides config)");
        System.out.println("\nExamples:");
        System.out.println("  java -jar lab-java-pulsar-eos.jar --file /tmp/test.log");
        System.out.println("  java -jar lab-java-pulsar-eos.jar --config /path/to/config.yaml --file /tmp/test.log");
        System.out.println("  java -jar lab-java-pulsar-eos.jar --file /tmp/test.log --topic my-topic --batch-size 50");
    }
}
