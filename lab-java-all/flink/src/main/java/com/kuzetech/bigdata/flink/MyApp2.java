package com.kuzetech.bigdata.flink;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.apache.kafka.clients.consumer.ConsumerConfig.ISOLATION_LEVEL_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Slf4j
public class MyApp2 {

    public static final Random RANDOM = new Random();

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers("kafka-headless:9092")
                .setTopics("test")
                .setGroupId("my")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setProperty(ISOLATION_LEVEL_CONFIG, "read_committed")
                .build();

        DataStreamSource<String> eventSource =
                env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "source");

        KafkaSink<String> sink = KafkaSink.<String>builder()
                .setBootstrapServers("kafka-headless:9092")
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .setTransactionalIdPrefix("aaaa")
                .setProperty(TRANSACTION_TIMEOUT_CONFIG, "900000")
                .setProperty(ENABLE_IDEMPOTENCE_CONFIG, "true")
                .setProperty(ACKS_CONFIG, "all")
                .setRecordSerializer(new KafkaRecordSerializationSchema<String>() {
                    @Nullable
                    @Override
                    public ProducerRecord<byte[], byte[]> serialize(String element, KafkaSinkContext context, Long timestamp) {
                        return new ProducerRecord<>("test" + (RANDOM.nextInt(3) + 1), element.getBytes(StandardCharsets.UTF_8));
                    }
                })
                .build();

        eventSource.sinkTo(sink);

        env.execute("MyApp2");

    }


}
