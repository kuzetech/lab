package com.kuzetech.bigdata.flink.broadcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuzetech.bigdata.flink.udsource.EventSingleParallelSource;
import com.kuzetech.bigdata.flink.udsource.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.StandardCharsets;

@Slf4j
public class BroadcastApp {

    public static void main(String[] args) throws Exception {

        log.info("启动应用");

        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        log.info("传入的参数是：" + parameterTool.getProperties().toString());

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(1000);
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, Time.seconds(10)));
        env.getConfig().setGlobalJobParameters(parameterTool);

        DataStreamSource<Rule> ruleSource = env.addSource(new RuleRichParallelSource()).setParallelism(1);

        DataStreamSource<Event> eventSource = env.addSource(new EventSingleParallelSource()).setParallelism(1);

        MapStateDescriptor<Void, Rule> bcStateDescriptor =
                new MapStateDescriptor<>("rules", Types.VOID, Types.POJO(Rule.class));

        BroadcastStream<Rule> bcedRules = ruleSource.broadcast(bcStateDescriptor);

        DataStream<Tuple2<String, Event>> matches = eventSource
                .connect(bcedRules)
                .process(new RuleEvaluator());

        KafkaSink<Tuple2<String, Event>> sink = KafkaSink.<Tuple2<String, Event>>builder()
                .setBootstrapServers("localhost:9092")
                .setRecordSerializer((KafkaRecordSerializationSchema<Tuple2<String, Event>>) (data, context, timestamp) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    String outputTopic = data.f0;
                    String outputValue;
                    try {
                        outputValue = mapper.writeValueAsString(data.f1);
                    } catch (Exception e) {
                        outputTopic = "error";
                        outputValue = data.f1.toString();
                    }
                    return new ProducerRecord<>(outputTopic, outputValue.getBytes(StandardCharsets.UTF_8));
                })
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .setTransactionalIdPrefix("bbbbb")
                .build();

        matches.sinkTo(sink);

        env.execute("broadcast test");

    }


}
