package com.kuzetech.bigdata.flinkstate;

import com.xmfunny.funnydb.flink.model.ActiveMark;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class DeriveJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                "file:///Users/huangsw/code/lab/lab-java-all/flink-state-processor/savepoints/derive",
                new EmbeddedRocksDBStateBackend(true));

        DataStream<Tuple2<String, Long>> eventState = savepoint.readKeyedState(
                OperatorIdentifier.forUid("derive-event-process"),
                new DeriveEventReaderFunction());

        StateBootstrapTransformation<Tuple2<String, Long>> eventTransformation = OperatorTransformation
                .bootstrapWith(eventState)
                .keyBy(s -> s.f0)
                .transform(new DeriveEventBootstrapper());

        String dauActiveCycle = "dau";
        String dauOperatorUid = String.format("derive-event-%s-process", dauActiveCycle);
        DataStream<Tuple2<String, ActiveMark>> dauState = savepoint.readKeyedState(
                OperatorIdentifier.forUid(dauOperatorUid),
                new DeriveAuReaderFunction(dauActiveCycle));

        StateBootstrapTransformation<Tuple2<String, ActiveMark>> dauTransformation = OperatorTransformation
                .bootstrapWith(dauState)
                .keyBy(s -> s.f0)
                .transform(new DeriveAuBootstrapper(dauActiveCycle));

        String wauActiveCycle = "wau";
        String wauOperatorUid = String.format("derive-event-%s-process", wauActiveCycle);
        DataStream<Tuple2<String, ActiveMark>> wauState = savepoint.readKeyedState(
                OperatorIdentifier.forUid(wauOperatorUid),
                new DeriveAuReaderFunction(wauActiveCycle));

        StateBootstrapTransformation<Tuple2<String, ActiveMark>> wauTransformation = OperatorTransformation
                .bootstrapWith(wauState)
                .keyBy(s -> s.f0)
                .transform(new DeriveAuBootstrapper(wauActiveCycle));

        String mauActiveCycle = "mau";
        String mauOperatorUid = String.format("derive-event-%s-process", mauActiveCycle);
        DataStream<Tuple2<String, ActiveMark>> mauState = savepoint.readKeyedState(
                OperatorIdentifier.forUid(mauOperatorUid),
                new DeriveAuReaderFunction(mauActiveCycle));

        StateBootstrapTransformation<Tuple2<String, ActiveMark>> mauTransformation = OperatorTransformation
                .bootstrapWith(mauState)
                .keyBy(s -> s.f0)
                .transform(new DeriveAuBootstrapper(mauActiveCycle));

        SavepointWriter
                .newSavepoint(env, new EmbeddedRocksDBStateBackend(true), 2)
                .withOperator(OperatorIdentifier.forUid("derive-event-process"), eventTransformation)
                .withOperator(OperatorIdentifier.forUid(dauOperatorUid), dauTransformation)
                .withOperator(OperatorIdentifier.forUid(wauOperatorUid), wauTransformation)
                .withOperator(OperatorIdentifier.forUid(mauOperatorUid), mauTransformation)
                .write("file:///Users/huangsw/code/lab/lab-java-all/flink-state-processor/savepoints/derive-new");

        env.execute("DeriveJob");
    }
}
