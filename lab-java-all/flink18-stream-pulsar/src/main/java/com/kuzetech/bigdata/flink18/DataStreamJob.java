package com.kuzetech.bigdata.flink18;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.pulsar.sink.PulsarSink;
import org.apache.flink.connector.pulsar.source.PulsarSource;
import org.apache.flink.connector.pulsar.source.enumerator.cursor.StartCursor;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class DataStreamJob {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        PulsarSource<String> source = PulsarSource.builder()
                .setServiceUrl("pulsar://localhost:6650")
                .setStartCursor(StartCursor.earliest())
                .setTopics("public/default/source-topic")
                .setDeserializationSchema(new SimpleStringSchema())
                .setSubscriptionName("flink-subscription")
                .build();

        DataStreamSource<String> input = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Pulsar Source");

        PulsarSink<String> sink = PulsarSink.builder()
                .setServiceUrl("pulsar://localhost:6650")
                .setAdminUrl("pulsar://localhost:8080")
                .setTopics("public/default/sink-topic")
                .setSerializationSchema(new SimpleStringSchema())
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();

        input.sinkTo(sink);

        env.execute("Flink Java API Skeleton");
    }
}
