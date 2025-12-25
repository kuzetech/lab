package com.kuzetech.bigdata.flink;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 使用这种方式初始化 geo 库
 * 好处是      仅初始化一次库，并且所有的任务共同依赖，节省内存
 * 需要注意    由于 db 文件缓存在 OS Page 中，这部分的内存 flink 是不会统计的，所以初始化 TM 时需要多分配一些内存
 */

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

        sourceStream.process(new ProcessFunction<String, String>() {
            private transient DateTimeFormatter formatter;

            @Override
            public void open(OpenContext openContext) throws Exception {
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            }

            @Override
            public void processElement(String value, ProcessFunction<String, String>.Context ctx, Collector<String> out) throws Exception {
                Instant waterMarkerInstant = Instant.ofEpochMilli(ctx.timerService().currentWatermark());
                LocalDateTime waterMarkerDate = LocalDateTime.ofInstant(waterMarkerInstant, ZoneId.systemDefault());
                System.out.println("Water Marker: " + waterMarkerDate.format(formatter));

                Instant dataInstant = Instant.ofEpochMilli(Long.parseLong(value));
                LocalDateTime dataDate = LocalDateTime.ofInstant(dataInstant, ZoneId.systemDefault());
                out.collect(dataDate.format(formatter));
            }
        }).print("Data: ");

        env.execute();
    }

}
