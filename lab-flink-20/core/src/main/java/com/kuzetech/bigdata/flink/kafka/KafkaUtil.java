package com.kuzetech.bigdata.flink.kafka;

import com.google.common.collect.ImmutableMap;
import com.kuzetech.bigdata.flink.kafka.domain.KafkaSourceMessage;
import com.kuzetech.bigdata.flink.kafka.serialization.KafkaSourceMessageSerializationSchema;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.sink.KafkaSinkBuilder;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.KafkaSourceOptions;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.IsolationLevel;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class KafkaUtil {

    public static final long DEFAULT_MAX_REQUEST_SIZE = 10485760;
    public static final String DEFAULT_KAFKA_PARTITION_DISCOVERY_INTERVAL_MS = "30000";
    public static final Map<String, String> DEFAULT_PRODUCER_CONFIG = ImmutableMap.<String, String>builder()
            // 调整最大请求大小配置
            .put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, Long.toString(DEFAULT_MAX_REQUEST_SIZE))
            // 默认开启lz4压缩
            .put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4")
            // 默认ACK为1
            .put(ProducerConfig.ACKS_CONFIG, "1")
            // 默认批次大小配置
            .put(ProducerConfig.BATCH_SIZE_CONFIG, "32768")
            // 默认消息停留时长
            .put(ProducerConfig.LINGER_MS_CONFIG, "5")
            .build();


    public static <T> KafkaSourceBuilder<T> buildSourceBaseBuilder(KafkaConfig config, KafkaRecordDeserializationSchema<T> recordDeserializer) {
        return KafkaSource.<T>builder()
                .setProperty(KafkaSourceOptions.PARTITION_DISCOVERY_INTERVAL_MS.key(), DEFAULT_KAFKA_PARTITION_DISCOVERY_INTERVAL_MS)
                .setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.toString().toLowerCase(Locale.ROOT))
                .setProperty("commit.offsets.on.checkpoint", "true")
                .setBootstrapServers(config.getBootstrapServers())
                .setTopics(config.getSourceTopic())
                .setGroupId(config.getSubscriber())
                .setStartingOffsets(getJobStartingOffsets(config.getStartingOffsets()))
                .setDeserializer(recordDeserializer)
                .setClientIdPrefix(config.getClientIdPrefix());
    }

    public static KafkaSinkBuilder<KafkaSourceMessage> buildSinkBaseBuilder(KafkaConfig config) {
        Properties props = new Properties();
        props.putAll(DEFAULT_PRODUCER_CONFIG);
        return KafkaSink.<KafkaSourceMessage>builder()
                .setKafkaProducerConfig(props)
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .setTransactionalIdPrefix(config.getTransactionalIdPrefix())
                .setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.toString().toLowerCase(Locale.ROOT))
                .setProperty(TRANSACTION_TIMEOUT_CONFIG, config.getTransactionTimeoutMs())
                .setProperty(ENABLE_IDEMPOTENCE_CONFIG, "true")
                .setProperty(ACKS_CONFIG, "all")
                .setProperty("commit.offsets.on.checkpoint", "true")
                .setBootstrapServers(config.getBootstrapServers())
                .setRecordSerializer(new KafkaSourceMessageSerializationSchema(config.getSinkTopic()));
    }

    private static OffsetsInitializer getJobStartingOffsets(String startingOffsets) {
        if (StringUtils.isBlank(startingOffsets)) {
            return OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST);
        }
        if (OffsetResetStrategy.EARLIEST.name().equalsIgnoreCase(startingOffsets)) {
            return OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST);
        }
        if (OffsetResetStrategy.LATEST.name().equalsIgnoreCase(startingOffsets)) {
            return OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST);
        }
        return analysisStartingOffsets(startingOffsets);
    }

    // 需要实现指定分区消费进度，todo
    private static OffsetsInitializer analysisStartingOffsets(String startingOffsets) {
        return OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST);
    }


}
