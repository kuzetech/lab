package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.udsource.CountParallelSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Slf4j
public class MyApp3 {

    public static final Random RANDOM = new Random();

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<Tuple2<String, Long>> eventSource = env.addSource(new CountParallelSource());

        KafkaSink<Tuple2<String, Long>> sink = KafkaSink.<Tuple2<String, Long>>builder()
                .setBootstrapServers("kafka-headless:9092")
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .setTransactionalIdPrefix("aaaa")
                .setProperty(TRANSACTION_TIMEOUT_CONFIG, "900000")
                .setProperty(ENABLE_IDEMPOTENCE_CONFIG, "true")
                .setProperty(ACKS_CONFIG, "all")
                .setRecordSerializer(new KafkaRecordSerializationSchema<Tuple2<String, Long>>() {
                    @Nullable
                    @Override
                    public ProducerRecord<byte[], byte[]> serialize(Tuple2<String, Long> element, KafkaSinkContext context, Long timestamp) {
                        return new ProducerRecord<>("test" + (RANDOM.nextInt(3) + 1), element.f0.getBytes(StandardCharsets.UTF_8));
                    }
                })
                .build();

        eventSource.sinkTo(sink);

        env.execute("MyApp3");

    }


}
