package com.kuzetech.bigdata.pulsar.eos.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * 应用配置类
 * 支持从 YAML 文件和命令行参数加载配置
 */
@Data
@Slf4j
public class AppConfig {

    // Pulsar 配置
    private String pulsarServiceUrl = "pulsar://localhost:6650";
    private String pulsarTopic = "persistent://public/default/eos-test-topic";
    private int transactionTimeout = 60;

    // 文件配置
    private String inputFilePath;
    private String offsetFilePath = "./offset.json";
    private String transactionLogPath = "./transaction.log";
    private long maxFileSize = 10 * 1024 * 1024; // 10MB

    // 批次配置
    private int batchSize = 100;

    // 重试配置
    private int maxRetryAttempts = 3;
    private long retryIntervalMs = 1000;

    /**
     * 从命令行参数和配置文件加载配置
     */
    public static AppConfig load(String[] args) {
        AppConfig config = new AppConfig();

        // 首先从默认配置文件加载
        config.loadFromYaml("config.yaml");

        // 解析命令行参数
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            // 如果指定了配置文件，从该文件加载
            if (cmd.hasOption("config")) {
                config.loadFromYamlFile(cmd.getOptionValue("config"));
            }

            // 命令行参数覆盖配置文件
            if (cmd.hasOption("service-url")) {
                config.setPulsarServiceUrl(cmd.getOptionValue("service-url"));
            }
            if (cmd.hasOption("topic")) {
                config.setPulsarTopic(cmd.getOptionValue("topic"));
            }
            if (cmd.hasOption("input")) {
                config.setInputFilePath(cmd.getOptionValue("input"));
            }
            if (cmd.hasOption("offset")) {
                config.setOffsetFilePath(cmd.getOptionValue("offset"));
            }
            if (cmd.hasOption("batch-size")) {
                config.setBatchSize(Integer.parseInt(cmd.getOptionValue("batch-size")));
            }
            if (cmd.hasOption("help")) {
                printHelp(options);
                System.exit(0);
            }

        } catch (ParseException e) {
            log.error("解析命令行参数失败: {}", e.getMessage());
            printHelp(options);
            System.exit(1);
        }

        config.validate();
        return config;
    }

    /**
     * 从类路径加载 YAML 配置
     */
    @SuppressWarnings("unchecked")
    private void loadFromYaml(String resourceName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream != null) {
                Yaml yaml = new Yaml();
                Map<String, Object> yamlMap = yaml.load(inputStream);
                parseYamlConfig(yamlMap);
                log.info("从类路径加载配置文件: {}", resourceName);
            }
        } catch (Exception e) {
            log.warn("加载默认配置文件失败: {}", e.getMessage());
        }
    }

    /**
     * 从文件路径加载 YAML 配置
     */
    @SuppressWarnings("unchecked")
    private void loadFromYamlFile(String filePath) {
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlMap = yaml.load(inputStream);
            parseYamlConfig(yamlMap);
            log.info("从文件加载配置: {}", filePath);
        } catch (Exception e) {
            log.error("加载配置文件失败: {}", e.getMessage());
        }
    }

    /**
     * 解析 YAML 配置
     */
    @SuppressWarnings("unchecked")
    private void parseYamlConfig(Map<String, Object> yamlMap) {
        if (yamlMap == null) return;

        // Pulsar 配置
        Map<String, Object> pulsarConfig = (Map<String, Object>) yamlMap.get("pulsar");
        if (pulsarConfig != null) {
            if (pulsarConfig.containsKey("serviceUrl")) {
                this.pulsarServiceUrl = (String) pulsarConfig.get("serviceUrl");
            }
            if (pulsarConfig.containsKey("topic")) {
                this.pulsarTopic = (String) pulsarConfig.get("topic");
            }
            if (pulsarConfig.containsKey("transactionTimeout")) {
                this.transactionTimeout = (Integer) pulsarConfig.get("transactionTimeout");
            }
        }

        // 文件配置
        Map<String, Object> fileConfig = (Map<String, Object>) yamlMap.get("file");
        if (fileConfig != null) {
            if (fileConfig.containsKey("inputPath")) {
                this.inputFilePath = (String) fileConfig.get("inputPath");
            }
            if (fileConfig.containsKey("offsetPath")) {
                this.offsetFilePath = (String) fileConfig.get("offsetPath");
            }
            if (fileConfig.containsKey("transactionLogPath")) {
                this.transactionLogPath = (String) fileConfig.get("transactionLogPath");
            }
            if (fileConfig.containsKey("maxFileSize")) {
                this.maxFileSize = ((Number) fileConfig.get("maxFileSize")).longValue();
            }
        }

        // 批次配置
        Map<String, Object> batchConfig = (Map<String, Object>) yamlMap.get("batch");
        if (batchConfig != null) {
            if (batchConfig.containsKey("size")) {
                this.batchSize = (Integer) batchConfig.get("size");
            }
        }

        // 重试配置
        Map<String, Object> retryConfig = (Map<String, Object>) yamlMap.get("retry");
        if (retryConfig != null) {
            if (retryConfig.containsKey("maxAttempts")) {
                this.maxRetryAttempts = (Integer) retryConfig.get("maxAttempts");
            }
            if (retryConfig.containsKey("intervalMs")) {
                this.retryIntervalMs = ((Number) retryConfig.get("intervalMs")).longValue();
            }
        }
    }

    /**
     * 创建命令行选项
     */
    private static Options createOptions() {
        Options options = new Options();
        options.addOption(Option.builder("c")
                .longOpt("config")
                .hasArg()
                .desc("配置文件路径")
                .build());
        options.addOption(Option.builder("s")
                .longOpt("service-url")
                .hasArg()
                .desc("Pulsar 服务地址")
                .build());
        options.addOption(Option.builder("t")
                .longOpt("topic")
                .hasArg()
                .desc("Pulsar 主题")
                .build());
        options.addOption(Option.builder("i")
                .longOpt("input")
                .hasArg()
                .desc("输入文件路径")
                .build());
        options.addOption(Option.builder("o")
                .longOpt("offset")
                .hasArg()
                .desc("偏移量文件路径")
                .build());
        options.addOption(Option.builder("b")
                .longOpt("batch-size")
                .hasArg()
                .desc("批次大小")
                .build());
        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("显示帮助信息")
                .build());
        return options;
    }

    /**
     * 打印帮助信息
     */
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("pulsar-eos", options);
    }

    /**
     * 验证配置
     */
    private void validate() {
        if (inputFilePath == null || inputFilePath.isEmpty()) {
            log.error("输入文件路径不能为空，请使用 -i 参数指定");
            System.exit(1);
        }
        if (!inputFilePath.endsWith(".log")) {
            log.error("输入文件必须是 .log 格式");
            System.exit(1);
        }
        log.info("配置验证通过");
        log.info("Pulsar Service URL: {}", pulsarServiceUrl);
        log.info("Pulsar Topic: {}", pulsarTopic);
        log.info("Input File: {}", inputFilePath);
        log.info("Offset File: {}", offsetFilePath);
        log.info("Batch Size: {}", batchSize);
    }
}
