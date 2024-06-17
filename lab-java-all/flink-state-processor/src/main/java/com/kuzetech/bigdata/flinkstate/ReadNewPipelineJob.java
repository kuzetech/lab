package com.kuzetech.bigdata.flinkstate;

import com.kuzetech.bigdata.flinkstate.tracktopipeline.PipelineDistinctStateReaderFunction;
import com.kuzetech.bigdata.flinkstate.tracktopipeline.TrackDeviceInfoEnrichStateReaderFunction;
import com.kuzetech.bigdata.flinkstate.tracktopipeline.TrackUserLoginDeviceStateReaderFunction;
import com.xmfunny.funnydb.flink.metadata.MetaDataContent;
import com.xmfunny.funnydb.flink.model.DeviceInfoCacheData;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;

import java.io.IOException;

public class ReadNewPipelineJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //readDistinctState(env);
        readRuleState(env);
        //readUserLoginDeviceState(env);
        //readDeviceEnrichState(env);


        env.execute("ReadPipelineJob");
    }

    private static void readDistinctState(StreamExecutionEnvironment env) throws IOException {
        SavepointReader savepoint = SavepointReader.read(
                env,
                "/Users/huangsw/code/lab/lab-java-all/flink-state-processor/savepoints/new",
                new EmbeddedRocksDBStateBackend(true));

        DataStream<Tuple2<String, Boolean>> distinctState = savepoint.readKeyedState(
                OperatorIdentifier.forUid("filter-distinct"),
                new PipelineDistinctStateReaderFunction());

        SingleOutputStreamOperator<Integer> sum = distinctState.map(new MapFunction<Tuple2<String, Boolean>, Integer>() {
                    @Override
                    public Integer map(Tuple2<String, Boolean> stringBooleanTuple2) throws Exception {
                        return 1;
                    }
                }).windowAll(GlobalWindows.create())
                .trigger(CountTrigger.of(50000))
                .sum(0);

        sum.print();
    }

    private static void readUserLoginDeviceState(StreamExecutionEnvironment env) throws IOException {
        SavepointReader savepoint = SavepointReader.read(
                env,
                "/Users/huangsw/code/lab/lab-java-all/flink-state-processor/savepoints/new",
                new EmbeddedRocksDBStateBackend(true));

        DataStream<Tuple2<String, String>> userLoginDeviceState = savepoint.readKeyedState(
                OperatorIdentifier.forUid("user-login-device-state"),
                new TrackUserLoginDeviceStateReaderFunction());

        SingleOutputStreamOperator<Integer> sum = userLoginDeviceState.map(new MapFunction<Tuple2<String, String>, Integer>() {
                    @Override
                    public Integer map(Tuple2<String, String> stringBooleanTuple2) throws Exception {
                        return 1;
                    }
                }).windowAll(GlobalWindows.create())
                .trigger(CountTrigger.of(50000))
                .sum(0);

        sum.print();
    }

    private static void readDeviceEnrichState(StreamExecutionEnvironment env) throws IOException {
        SavepointReader savepoint = SavepointReader.read(
                env,
                "/Users/huangsw/code/lab/lab-java-all/flink-state-processor/savepoints/new",
                new EmbeddedRocksDBStateBackend(true));

        DataStream<Tuple2<String, DeviceInfoCacheData>> deviceInfoEnrichState = savepoint.readKeyedState(
                OperatorIdentifier.forUid("device-info-enrich-state"),
                new TrackDeviceInfoEnrichStateReaderFunction());

        SingleOutputStreamOperator<Integer> sum = deviceInfoEnrichState.map(new MapFunction<Tuple2<String, DeviceInfoCacheData>, Integer>() {
                    @Override
                    public Integer map(Tuple2<String, DeviceInfoCacheData> stringBooleanTuple2) throws Exception {
                        return 1;
                    }
                }).windowAll(GlobalWindows.create())
                .trigger(CountTrigger.of(50000))
                .sum(0);

        sum.print();
    }


    private static void readRuleState(StreamExecutionEnvironment env) throws IOException {
        SavepointReader savepoint = SavepointReader.read(
                env,
                "/Users/huangsw/code/lab/lab-java-all/flink-state-processor/savepoints/new",
                new EmbeddedRocksDBStateBackend(true));

        DataStream<MetaDataContent> ruleState = savepoint.readListState(
                OperatorIdentifier.forUid("event-etl"),
                "rulesList",
                TypeInformation.of(MetaDataContent.class));

        ruleState.print();
    }
}
