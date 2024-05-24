package com.kuzetech.bigdata.flink.stateprocessor;

import com.kuzetech.bigdata.flink.stateprocessor.model.DeviceModelStatistics;
import com.kuzetech.bigdata.flink.utils.FlinkUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

public class DeviceMapStatisticJob {


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = FlinkUtil.getEnvironment(
                "DeviceMapStatisticJob",
                2,
                "file:///Users/huangsw/code/lab/lab-java-all/flink/checkpoints/DeviceMapStatisticJob/3e2aa8d8165236fa7cb217fef8eddc72/chk-3");

        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("topic")
                .setGroupId("test")
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setProperty("commit.offsets.on.checkpoint", "true")
                .build();

        DataStreamSource<String> source =
                env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "source");

        SingleOutputStreamOperator<DeviceModelStatistics> stream = source
                .keyBy(v -> v)
                .process(new KeyedProcessFunction<>() {

                    private transient ValueState<DeviceModelStatistics> statisticsValueState;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        ValueStateDescriptor<DeviceModelStatistics> descriptor =
                                new ValueStateDescriptor<>(
                                        "model-count",
                                        TypeInformation.of(new TypeHint<DeviceModelStatistics>() {
                                        }));
                        statisticsValueState = getRuntimeContext().getState(descriptor);
                    }

                    @Override
                    public void processElement(
                            String value,
                            KeyedProcessFunction<String, String, DeviceModelStatistics>.Context ctx,
                            Collector<DeviceModelStatistics> out) throws Exception {
                        DeviceModelStatistics statistics = statisticsValueState.value();
                        if (statistics == null) {
                            statistics = new DeviceModelStatistics(value, 1);
                        } else {
                            statistics.setCount(statistics.getCount() + 1);
                        }
                        statisticsValueState.update(statistics);
                        out.collect(statistics);
                    }
                });

        stream.uid("statistic-test").print();

        env.execute("DeviceMapStatisticJob");
    }
}
