package com.kuzetech.bigdata.lab.flink20.sql.production.funnydb.counter;

import com.kuzetech.bigdata.lab.flink20.sql.core.config.JobConfig;
import com.kuzetech.bigdata.lab.flink20.sql.core.util.StreamExecutionEnvironmentUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.header.Header;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MutationEventCountWithAllowedLateness {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Instant MIN_EVENT_TIME = Instant.parse("2020-01-01T00:00:00Z");
    private static final Duration MAX_FUTURE_SKEW = Duration.ofMinutes(30);
    private static final Duration WATERMARK_OUT_OF_ORDERNESS = Duration.ofMinutes(10);
    private static final Duration ALLOWED_LATENESS = Duration.ofMinutes(60);
    private static final Duration WINDOW_SIZE = Duration.ofMinutes(5);

    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        JobConfig jobConfig = JobConfig.getInstance(parameterTool);
        String appFilter = jobConfig.getUserDefinedConfig().getAppFilter();

        log.info("jobConfig: {}", jobConfig);

        StreamExecutionEnvironment env = StreamExecutionEnvironmentUtil.getConfigStreamExecutionEnvironment(parameterTool);

        KafkaSource<RawKafkaEvent> source = KafkaSource.<RawKafkaEvent>builder()
                .setBootstrapServers(jobConfig.getKafkaConfig().getBootstrapServers())
                .setTopics(jobConfig.getKafkaConfig().getTopic())
                .setGroupId(jobConfig.getKafkaConfig().getGroupId())
                .setStartingOffsets(OffsetsInitializer.committedOffsets(parseOffsetResetStrategy(jobConfig.getKafkaConfig().getOffsetReset())))
                .setDeserializer(new MutationEventDeserializationSchema())
                .setProperty("isolation.level", "read_committed")
                .setProperty("partition.discovery.interval.ms", String.valueOf(Duration.ofMinutes(5).toMillis()))
                .build();

        DataStream<MutationEvent> events = env.fromSource(
                        source,
                        WatermarkStrategy.noWatermarks(),
                        "mutation-source")
                .map(value -> parseEvent(value, appFilter))
                .filter(event -> event != null)
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<MutationEvent>forBoundedOutOfOrderness(WATERMARK_OUT_OF_ORDERNESS)
                                .withTimestampAssigner((SerializableTimestampAssigner<MutationEvent>) (event, recordTimestamp) -> event.getEventTimeMillis())
                                .withIdleness(Duration.ofMinutes(1)));

        events
                .windowAll(TumblingEventTimeWindows.of(Time.milliseconds(WINDOW_SIZE.toMillis())))
                .allowedLateness(Time.milliseconds(ALLOWED_LATENESS.toMillis()))
                .process(new CountWindowProcessFunction())
                .addSink(JdbcSink.sink(
                        String.format(
                                "INSERT INTO %s (app, window_start, window_end, event, total) " +
                                        "VALUES (?, ?, ?, ?, ?) " +
                                        "ON CONFLICT (app, window_start, window_end, event) " +
                                        "DO UPDATE SET total = EXCLUDED.total",
                                jobConfig.getJdbcConfig().getTable()),
                        (statement, record) -> {
                            statement.setString(1, record.getApp());
                            statement.setTimestamp(2, Timestamp.from(record.getWindowStart()));
                            statement.setTimestamp(3, Timestamp.from(record.getWindowEnd()));
                            statement.setString(4, record.getEvent());
                            statement.setLong(5, record.getTotal());
                        },
                        JdbcExecutionOptions.builder()
                                .withBatchSize(1)
                                .build(),
                        new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                                .withUrl(jobConfig.getJdbcConfig().getUrl())
                                .withDriverName("org.postgresql.Driver")
                                .withUsername(jobConfig.getJdbcConfig().getUsername())
                                .withPassword(jobConfig.getJdbcConfig().getPassword())
                                .build()
                ));

        env.execute(MutationEventCountWithAllowedLateness.class.getSimpleName());
    }

    private static MutationEvent parseEvent(RawKafkaEvent value, String appFilter) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(value.getPayload());
            JsonNode eventTimeNode = root.get("#event_time");
            if (eventTimeNode == null || !eventTimeNode.canConvertToLong()) {
                return null;
            }

            long eventTimeMillis = eventTimeNode.asLong();
            Instant eventTime = Instant.ofEpochMilli(eventTimeMillis);
            Instant now = Instant.now();

            if (eventTime.isBefore(MIN_EVENT_TIME) || eventTime.isAfter(now.plus(MAX_FUTURE_SKEW))) {
                return null;
            }

            String app = value.getHeaders().get("app");
            String mutationType = value.getHeaders().get("mutation_type");

            if (!appFilter.equals(app) || !"USER".equals(mutationType)) {
                return null;
            }

            return new MutationEvent(eventTimeMillis);
        } catch (Exception e) {
            log.debug("ignore malformed mutation event: {}", value, e);
            return null;
        }
    }

    private static OffsetResetStrategy parseOffsetResetStrategy(String value) {
        if (value == null) {
            return OffsetResetStrategy.EARLIEST;
        }

        switch (value.toLowerCase()) {
            case "latest":
                return OffsetResetStrategy.LATEST;
            case "none":
                return OffsetResetStrategy.NONE;
            case "earliest":
            default:
                return OffsetResetStrategy.EARLIEST;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class MutationEvent {
        private long eventTimeMillis;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class RawKafkaEvent {
        private String payload;
        private Map<String, String> headers;
    }

    private static class MutationEventDeserializationSchema implements KafkaRecordDeserializationSchema<RawKafkaEvent> {
        @Override
        public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<RawKafkaEvent> out) {
            Map<String, String> headers = new HashMap<>();
            for (Header header : record.headers()) {
                headers.put(header.key(), new String(header.value(), StandardCharsets.UTF_8));
            }

            out.collect(new RawKafkaEvent(
                    record.value() == null ? null : new String(record.value(), StandardCharsets.UTF_8),
                    headers
            ));
        }

        @Override
        public org.apache.flink.api.common.typeinfo.TypeInformation<RawKafkaEvent> getProducedType() {
            return org.apache.flink.api.common.typeinfo.TypeInformation.of(RawKafkaEvent.class);
        }
    }

    private static class CountWindowProcessFunction extends ProcessAllWindowFunction<MutationEvent, WindowCountRecord, TimeWindow> {
        @Override
        public void process(Context context, Iterable<MutationEvent> elements, Collector<WindowCountRecord> out) {
            long total = 0L;
            for (MutationEvent ignored : elements) {
                total++;
            }

            out.collect(new WindowCountRecord(
                    "MUTATION",
                    Instant.ofEpochMilli(context.window().getStart()),
                    Instant.ofEpochMilli(context.window().getEnd()),
                    "USER",
                    total
            ));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class WindowCountRecord {
        private String app;
        private Instant windowStart;
        private Instant windowEnd;
        private String event;
        private long total;
    }
}
