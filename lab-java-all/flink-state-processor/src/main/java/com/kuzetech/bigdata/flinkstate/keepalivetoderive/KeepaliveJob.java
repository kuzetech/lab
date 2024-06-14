package com.kuzetech.bigdata.flinkstate.keepalivetoderive;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class KeepaliveJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                "file:///Users/huangsw/code/lab/lab-java-all/flink-state-processor/savepoints/keepalive",
                new EmbeddedRocksDBStateBackend(true));

        DataStream<Tuple2<String, Long>> keyedState = savepoint.readKeyedState(
                OperatorIdentifier.forUid("suppression-mutation-event"),
                new KeepaliveReaderFunction());

        StateBootstrapTransformation<Tuple2<String, Long>> transformation = OperatorTransformation
                .bootstrapWith(keyedState)
                .keyBy(s -> s.f0)
                .transform(new KeepaliveBootstrapper());

        SavepointWriter
                .newSavepoint(env, new EmbeddedRocksDBStateBackend(true), 2)
                .withOperator(OperatorIdentifier.forUid("suppression-mutation-event"), transformation)
                .write("file:///Users/huangsw/code/lab/lab-java-all/flink-state-processor/savepoints/keepalive-new");


        env.execute("KeepaliveJob");
    }
}
