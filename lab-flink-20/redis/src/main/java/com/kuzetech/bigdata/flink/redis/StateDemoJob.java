package com.kuzetech.bigdata.flink.redis;

import com.kuzetech.bigdata.flink.base.FlinkUtil;
import com.kuzetech.bigdata.flink.redis.func.StateProcessFunc;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.UUID;

public class StateDemoJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = FlinkUtil.initEnv(ParameterTool.fromArgs(args));
        env.setParallelism(2);

        KafkaSourceBuilder<String> stringKafkaSourceBuilder = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("test")
                .setGroupId(UUID.randomUUID().toString())
                .setStartingOffsets(OffsetsInitializer.latest())
                .setDeserializer(KafkaRecordDeserializationSchema.valueOnly(StringDeserializer.class));

        SingleOutputStreamOperator<String> sourceStream =
                env.fromSource(stringKafkaSourceBuilder.build(), WatermarkStrategy.noWatermarks(), "source")
                        .returns(Types.STRING)
                        .uid("source")
                        .name("source");


        sourceStream
                .process(new StateProcessFunc())
                .print();

        env.execute();
    }
}
