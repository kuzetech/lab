package com.kuzetech.bigdata.pulsar.eos.config;

import lombok.Data;

@Data
public class AppConfig {
    private PulsarConfig pulsar;
    private MysqlConfig mysql;
    private FileConfig file;
    private RetryConfig retry;
    private LoggingConfig logging;

    @Data
    public static class PulsarConfig {
        private String serviceUrl;
        private String adminUrl;
        private String topic;
        private ProducerConfig producer;

        @Data
        public static class ProducerConfig {
            private String producerName;
            private Integer sendTimeoutMs;
            private Boolean blockIfQueueFull;
            private Boolean enableBatching;
            private Boolean enableTransaction;
            private Integer transactionTimeoutMs;
        }
    }

    @Data
    public static class MysqlConfig {
        private String host;
        private Integer port;
        private String database;
        private String username;
        private String password;
        private Integer maxConnections;
        private Integer connectionTimeoutMs;

        public String getJdbcUrl() {
            return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                    host, port, database);
        }
    }

    @Data
    public static class FileConfig {
        private String path;
        private Integer batchSize;
        private Integer bufferSize;
        private String encoding;
    }

    @Data
    public static class RetryConfig {
        private Integer maxAttempts;
        private Integer delayMs;
        private Double multiplier;
        private Integer maxDelayMs;
    }

    @Data
    public static class LoggingConfig {
        private String level;
        private Integer progressInterval;
    }
}
