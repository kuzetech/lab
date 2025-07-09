package com.kuzetech.bigdata.flink.test;

import com.kuzetech.bigdata.flink.domain.EnrichOperatorKeyedState;
import com.kuzetech.bigdata.flink.function.EnrichOperatorKeyedStateBootstrapper;
import com.kuzetech.bigdata.flink.function.EnrichOperatorKeyedStateReaderFunction;
import org.apache.commons.io.FileUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;

import java.io.File;

public class EnrichOperatorStateBuildJob {

    public static final String OLD_SAVEPOINT_PATH = "file:///Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/savepoint/staging";
    public static final String NEW_SAVEPOINT_PATH = "file:///Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/generate/EnrichOperatorStateBuildJob";
    public static final String NEW_SAVEPOINT_DIRECTORY = "/Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/generate/EnrichOperatorStateBuildJob";

    public static void main(String[] args) throws Exception {
        FileUtils.deleteDirectory(new File(NEW_SAVEPOINT_DIRECTORY));

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(32);

        SavepointReader savepoint = SavepointReader.read(
                env,
                OLD_SAVEPOINT_PATH,
                new EmbeddedRocksDBStateBackend(true));

        DataStream<EnrichOperatorKeyedState> enrichOperatorKeyedStateDataStream = savepoint.readKeyedState(
                OperatorIdentifier.forUid("device-info-enrich-state"),
                new EnrichOperatorKeyedStateReaderFunction(),
                Types.STRING,
                TypeInformation.of(EnrichOperatorKeyedState.class));

        DataStream<Long> totalCount = enrichOperatorKeyedStateDataStream
                .map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum);

        totalCount.print(); //3918

        StateBootstrapTransformation<EnrichOperatorKeyedState> transformation = OperatorTransformation
                .bootstrapWith(enrichOperatorKeyedStateDataStream)
                .keyBy(o -> o.key)
                .transform(new EnrichOperatorKeyedStateBootstrapper());

        SavepointWriter
                .newSavepoint(env, new EmbeddedRocksDBStateBackend(true), 256)
                .withOperator(OperatorIdentifier.forUid("device-info-enrich-state"), transformation)
                .write(NEW_SAVEPOINT_PATH);

        env.execute("EnrichOperatorStateBuildJob");

    }
}
