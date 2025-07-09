package com.kuzetech.bigdata.flink.test;

import com.kuzetech.bigdata.flink.domain.DistinctOperatorKeyedState;
import com.kuzetech.bigdata.flink.function.DistinctOperatorKeyedStateBootstrapper;
import com.kuzetech.bigdata.flink.function.DistinctOperatorKeyedStateReaderFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.File;

@Slf4j
public class TrackJob1Distinct {

    public static final String OLD_SAVEPOINT_PATH = "file:///Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/savepoint/staging";
    public static final String NEW_SAVEPOINT_PATH = "file:///Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/generate/TrackJob1Distinct";
    public static final String NEW_SAVEPOINT_DIRECTORY = "/Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/generate/TrackJob1Distinct";

    public static void main(String[] args) throws Exception {
        FileUtils.deleteDirectory(new File(NEW_SAVEPOINT_DIRECTORY));

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(32);

        SavepointReader savepoint = SavepointReader.read(
                env,
                OLD_SAVEPOINT_PATH,
                new EmbeddedRocksDBStateBackend(true));

        DataStream<DistinctOperatorKeyedState> distinctOperatorKeyedStateDataStream = savepoint.readKeyedState(
                OperatorIdentifier.forUid("filter-distinct"),
                new DistinctOperatorKeyedStateReaderFunction(),
                Types.STRING,
                TypeInformation.of(DistinctOperatorKeyedState.class));

        StateBootstrapTransformation<DistinctOperatorKeyedState> transformation = OperatorTransformation
                .bootstrapWith(distinctOperatorKeyedStateDataStream)
                .keyBy(o -> o.key)
                .transform(new DistinctOperatorKeyedStateBootstrapper());

        SavepointWriter
                .newSavepoint(env, new EmbeddedRocksDBStateBackend(true), 256)
                .withOperator(OperatorIdentifier.forUid("filter-distinct"), transformation)
                .write(NEW_SAVEPOINT_PATH);

        env.execute("TrackJob1Distinct");
    }
}
