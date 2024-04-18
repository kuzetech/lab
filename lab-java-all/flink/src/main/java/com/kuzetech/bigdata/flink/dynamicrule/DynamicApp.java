package com.kuzetech.bigdata.flink.dynamicrule;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.producer.ProducerRecord;

@Slf4j
public class DynamicApp {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        KafkaSource<InputMessage> kafkaSource = KafkaSource.<InputMessage>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("input")
                .setGroupId("FunnyGroup")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setDeserializer(new KafkaEventDeserializationSchema())
                .build();

        DataStreamSource<InputMessage> sourceStream =
                env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "source");

        SingleOutputStreamOperator<OutputMessage> processedStream = sourceStream.process(new ProcessFunction<>() {
            // 动态规则配置中心
            private ZkBasedConfigCenter zkBasedConfigCenter;

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                this.zkBasedConfigCenter = ZkBasedConfigCenter.getInstance();
            }

            @Override
            public void close() throws Exception {
                super.close();
                if (this.zkBasedConfigCenter != null) {
                    this.zkBasedConfigCenter.close();
                }
            }

            @Override
            public void processElement(
                    InputMessage input,
                    ProcessFunction<InputMessage, OutputMessage>.Context ctx,
                    Collector<OutputMessage> out) throws Exception {
                String key = input.getApp() + "_" + input.getEvent();
                DynamicProducerRule rule = this.zkBasedConfigCenter.getMap().get(key);
                if (rule != null && rule.eval(input)) {
                    rule.process(input);
                    out.collect(OutputMessage.createFromInputMessage(rule.getTargetTopic(), input));
                }
            }
        });

        KafkaSink<OutputMessage> sink = KafkaSink.<OutputMessage>builder()
                .setBootstrapServers("localhost:9092")
                .setRecordSerializer((KafkaRecordSerializationSchema<OutputMessage>) (data, context, timestamp) -> {
                    return new ProducerRecord<>(data.getDestTopic(), data.getData());
                })
                .build();

        processedStream.sinkTo(sink);

        env.execute("test");

    }
}
