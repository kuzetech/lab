package com.kuzetech.bigdata.flink18;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.pulsar.source.PulsarSource;
import org.apache.flink.connector.pulsar.source.enumerator.cursor.StartCursor;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class KeyPrintJob {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        
        PulsarSource<String> source = PulsarSource.builder()
                .setServiceUrl("pulsar://localhost:6650")
                .setStartCursor(StartCursor.earliest())
                .setTopics("public/default/test")
                .setDeserializationSchema(new PulsarMessageDeserializationSchema())
                .setSubscriptionName("KeyPrintJob")
                .build();

        DataStreamSource<String> input = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Pulsar Source");

        input.print();

        env.execute("KeyPrintJob");
    }
}
