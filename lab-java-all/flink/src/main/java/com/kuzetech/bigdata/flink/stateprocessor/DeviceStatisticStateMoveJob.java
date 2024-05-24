package com.kuzetech.bigdata.flink.stateprocessor;

import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class DeviceStatisticStateMoveJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                "file:///Users/huangsw/code/lab/lab-java-all/flink/checkpoints/DeviceStatisticJob/1a68c40b8b5e60ffb1a74589be180f54/chk-2",
                new EmbeddedRocksDBStateBackend(true));

        DataStream<KeyedState> keyedState = savepoint.readKeyedState(
                OperatorIdentifier.forUid("statistic-test"),
                new ReaderFunction());

        StateBootstrapTransformation<KeyedState> transformation = OperatorTransformation
                .bootstrapWith(keyedState)
                .keyBy(s -> s.getStatistics().getModel())
                .transform(new KeyedStateBootstrapper());

        SavepointWriter
                .newSavepoint(env, new EmbeddedRocksDBStateBackend(true), 2)
                .withOperator(OperatorIdentifier.forUid("statistic-test"), transformation)
                .write("file:///Users/huangsw/code/lab/lab-java-all/flink/checkpoints/DeviceMapStatisticJob/1a68c40b8b5e60ffb1a74589be180f54/chk-2");

        env.execute("DeviceStatisticStateMoveJob");
    }
}
