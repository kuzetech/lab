package com.kuzetech.bigdata.flink.redis;

import com.kuzetech.bigdata.flink.udsink.RedisPipelineSink;
import com.kuzetech.bigdata.flink.utils.FlinkUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Random;

public class SinkToPipelineRedisAndKafkaDemo {
    public static void main(String[] args) throws Exception {

        String jobName = "SinkToPipelineRedisAndKafkaDemo";

        StreamExecutionEnvironment env = FlinkUtil.getEnvironment(jobName);
        env.setParallelism(1);

        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("input")
                .setGroupId("test")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> dataStreamSource =
                env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "source");

        KafkaSink<String> sink = KafkaSink.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("output")
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build())
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .setProperty("transaction.timeout.ms", "120000")
                .build();

        dataStreamSource.sinkTo(sink);

        SingleOutputStreamOperator<Tuple2<String, String>> redisTupleStream = dataStreamSource.map(new MapFunction<>() {
            private final Random random = new Random();

            @Override
            public Tuple2<String, String> map(String value) throws Exception {
                return new Tuple2<>(String.valueOf(random.nextInt(1000)), value);
            }
        });

        RedisPipelineSink redisPipelineSink = new RedisPipelineSink();

        redisTupleStream.addSink(redisPipelineSink);

        env.execute(jobName);
    }
}


