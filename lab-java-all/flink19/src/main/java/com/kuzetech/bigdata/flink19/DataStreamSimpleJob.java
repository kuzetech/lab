package com.kuzetech.bigdata.flink19;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

@Slf4j
public class DataStreamSimpleJob {

    public static void main(String[] args) throws Exception {
        int sourceParallel = Integer.parseInt(args[0]);
        String sourceGroup = args[1];
        int targetParallel = Integer.parseInt(args[2]);
        String targetGroup = args[3];

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        GeneratorFunction<Long, String> generatorFunction = index -> "Number: " + index;

        DataGeneratorSource<String> dataGeneratorSource = new DataGeneratorSource<>(
                generatorFunction,
                Long.MAX_VALUE,
                RateLimiterStrategy.perSecond(1),
                Types.STRING);

        SingleOutputStreamOperator<String> sourceStream = env.fromSource(
                        dataGeneratorSource,
                        WatermarkStrategy.noWatermarks(),
                        "Generator Source")
                .setParallelism(sourceParallel)
                .slotSharingGroup(sourceGroup);

        SingleOutputStreamOperator<String> mapStream = sourceStream.map(new MapFunction<String, String>() {
            @Override
            public String map(String value) throws Exception {
                return "prefix-" + value;
            }
        });

        mapStream.process(new ProcessFunction<String, Void>() {
                    @Override
                    public void processElement(String value, ProcessFunction<String, Void>.Context ctx, Collector<Void> out) throws Exception {
                        log.info(value);
                    }
                })
                .setParallelism(targetParallel)
                .slotSharingGroup(targetGroup);

        env.execute("demo");
    }
}
