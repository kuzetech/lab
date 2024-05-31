package com.kuzetech.bigdata.flinkstate;

import com.xmfunny.funnydb.flink.model.ActiveMark;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class MergeKeepaliveToDeriveJob {

    public static void main(String[] args) throws Exception {
        if (args.length >= 3) {
            throw new RuntimeException("args length must >= 3");
        }

        String keepaliveSavepointPath = args[0];
        String deriveSavepointPath = args[1];
        String newSavepointPath = args[2];

        StreamExecutionEnvironment env;
        if (args.length > 3) {
            Configuration configuration = new Configuration();
            configuration.setString(RestOptions.BIND_PORT, "9988");
            env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
        } else {
            env = StreamExecutionEnvironment.getExecutionEnvironment();
        }


        SavepointReader keepaliveSavepoint = SavepointReader.read(
                env,
                keepaliveSavepointPath,
                new EmbeddedRocksDBStateBackend(true));

        DataStream<Tuple2<String, Long>> keepaliveState = keepaliveSavepoint.readKeyedState(
                OperatorIdentifier.forUid("suppression-mutation-event"),
                new KeepaliveReaderFunction());

        StateBootstrapTransformation<Tuple2<String, Long>> keepaliveTransformation = OperatorTransformation
                .bootstrapWith(keepaliveState)
                .keyBy(s -> s.f0)
                .transform(new KeepaliveBootstrapper());

        SavepointReader savepoint = SavepointReader.read(
                env,
                deriveSavepointPath,
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
                .newSavepoint(env, new EmbeddedRocksDBStateBackend(true), 20)
                .withOperator(OperatorIdentifier.forUid("suppression-mutation-event"), keepaliveTransformation)
                .withOperator(OperatorIdentifier.forUid("derive-event-process"), eventTransformation)
                .withOperator(OperatorIdentifier.forUid(dauOperatorUid), dauTransformation)
                .withOperator(OperatorIdentifier.forUid(wauOperatorUid), wauTransformation)
                .withOperator(OperatorIdentifier.forUid(mauOperatorUid), mauTransformation)
                .write(newSavepointPath);

        env.execute("MergeKeepaliveToDeriveJob");
    }
}
