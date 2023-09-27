package com.kuze.bigdata.study;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.StandardCharsets;

@Slf4j
public class KafkaApp {

    public static void main(String[] args) throws Exception {

        log.info("启动应用");

        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        log.info("传入的参数是：" + parameterTool.getProperties().toString());

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, Time.seconds(10)));
        env.getConfig().setGlobalJobParameters(parameterTool);

        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("input-topic")
                .setGroupId("my-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> dataStreamSource =
                env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "source");

        KafkaSink<String> sink = KafkaSink.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setRecordSerializer((KafkaRecordSerializationSchema<String>) (data, context, timestamp) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    String outputTopic;
                    try {
                        JsonNode jsonNode = mapper.readTree(data);
                        outputTopic = jsonNode.get("table").asText();
                    } catch (Exception e) {
                        outputTopic = "error";
                    }
                    return new ProducerRecord<>(outputTopic, data.getBytes(StandardCharsets.UTF_8));
                })
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .build();

        dataStreamSource.sinkTo(sink);

        env.execute("test");

    }
}
