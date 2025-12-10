package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.domain.AuOperatorKeyedState;
import com.kuzetech.bigdata.flink.function.AuOperatorKeyedStateReaderFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;

import java.util.Random;

@Slf4j
public class DeriveReadJob {

    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        boolean exceptDemo = parameterTool.getBoolean("exceptDemo", true);
        log.info("exceptDemo: {}", exceptDemo);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                //parameterTool.get("path"),
                "file:///Users/huangsw/code/lab/lab-flink-17/state-processor-derive/data/staging/derive",
                new EmbeddedRocksDBStateBackend(true));

        savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-wau-process"),
                        new AuOperatorKeyedStateReaderFunction("wau-state"),
                        Types.STRING,
                        TypeInformation.of(AuOperatorKeyedState.class))
                .map(v -> {
                    return Tuple2.of(new Random().nextInt(128), 1L);
                })
                .returns(new TypeHint<Tuple2<Integer, Long>>() {
                })
                .keyBy(value -> value.f0)
                .window(GlobalWindows.create())
                .trigger(new GlobalWindows.EndOfStreamTrigger())
                .reduce((ReduceFunction<Tuple2<Integer, Long>>) (value1, value2) -> new Tuple2<>(value1.f0, value1.f1 + value2.f1))
                .map(value -> value.f1)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum)
                .print("wauDataStreamResult: ").setParallelism(1); //3907250

        env.execute("DeriveReadJob");

    }
}
