package com.kuzetech.bigdata.flink.stateprocessor;

import com.kuzetech.bigdata.flink.utils.FlinkUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

public class DeviceMapJob {


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = FlinkUtil.getEnvironment("DeviceMapJob", 2);

        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("topic")
                .setGroupId("test")
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setProperty("commit.offsets.on.checkpoint", "true")
                .build();

        DataStreamSource<String> source =
                env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "source");

        KafkaSink<String> sink = KafkaSink.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setProperty("transaction.timeout.ms", "120000")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("device")
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                )
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .build();

        source.sinkTo(sink);

        env.execute("DeviceMapJob");
    }
}
