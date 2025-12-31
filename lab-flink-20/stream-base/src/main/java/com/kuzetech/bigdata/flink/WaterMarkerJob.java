package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.source.BroadcastSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
public class WaterMarkerJob {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        KafkaSourceBuilder<String> stringKafkaSourceBuilder = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("test")
                .setGroupId(UUID.randomUUID().toString())
                .setStartingOffsets(OffsetsInitializer.latest())
                .setDeserializer(KafkaRecordDeserializationSchema.valueOnly(StringDeserializer.class));

        WatermarkStrategy<String> watermarkStrategy =
                WatermarkStrategy.<String>forBoundedOutOfOrderness(Duration.ofSeconds(30))
                        .withTimestampAssigner((data, rts) -> Long.parseLong(data))
                        .withIdleness(Duration.ofMinutes(1));

        SingleOutputStreamOperator<String> sourceStream =
                env.fromSource(stringKafkaSourceBuilder.build(), watermarkStrategy, "source")
                        .returns(Types.STRING)
                        .uid("source")
                        .name("source");

        WatermarkStrategy<Long> metadataWatermarkStrategy =
                WatermarkStrategy.<Long>forBoundedOutOfOrderness(Duration.ofSeconds(30))
                        .withTimestampAssigner((data, rts) -> data)
                        .withIdleness(Duration.ofMinutes(1));

        BroadcastStream<Long> metadataStream =
                env.addSource(new BroadcastSource())
                        .assignTimestampsAndWatermarks(metadataWatermarkStrategy)
                        .setParallelism(1)
                        .name("source-metadata")
                        .uid("source-metadata")
                        .broadcast(new MapStateDescriptor<>("test", Types.VOID, Types.LONG));

        sourceStream
                .connect(metadataStream)
                .process(new BroadcastProcessFunction<String, Long, String>() {
                    private transient DateTimeFormatter formatter;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    }

                    @Override
                    public void processElement(String value, BroadcastProcessFunction<String, Long, String>.ReadOnlyContext ctx, Collector<String> out) throws Exception {
                        Instant waterMarkerInstant = Instant.ofEpochMilli(ctx.currentWatermark());
                        LocalDateTime waterMarkerDate = LocalDateTime.ofInstant(waterMarkerInstant, ZoneId.systemDefault());
                        String manReadTime = waterMarkerDate.format(formatter);
                        out.collect(manReadTime);
                    }

                    @Override
                    public void processBroadcastElement(Long value, BroadcastProcessFunction<String, Long, String>.Context ctx, Collector<String> out) throws Exception {
                        // do nothing
                    }
                })
                .print("Water Marker: ");

        env.execute();
    }

}
