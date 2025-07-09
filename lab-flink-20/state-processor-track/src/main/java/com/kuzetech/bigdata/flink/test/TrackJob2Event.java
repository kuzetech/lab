package com.kuzetech.bigdata.flink.test;

import com.kuzetech.bigdata.flink.function.EventOperatorStateBootstrapFunction;
import com.xmfunny.funnydb.flink.pipeline.validator.ValidateEvenStatsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.File;

@Slf4j
public class TrackJob2Event {

    public static final String OLD_SAVEPOINT_PATH = "file:///Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/savepoint/staging";
    public static final String NEW_SAVEPOINT_PATH = "file:///Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/generate/TrackJob2Event";
    public static final String NEW_SAVEPOINT_DIRECTORY = "/Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/generate/TrackJob2Event";

    public static void main(String[] args) throws Exception {
        FileUtils.deleteDirectory(new File(NEW_SAVEPOINT_DIRECTORY));

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(32);

        SavepointReader savepoint = SavepointReader.read(
                env,
                OLD_SAVEPOINT_PATH,
                new EmbeddedRocksDBStateBackend(true));

        DataStream<ValidateEvenStatsResponse> validateEvenStatsResponseDataStream = savepoint.readListState(
                OperatorIdentifier.forUid("event-etl"),
                "validateStatsList",
                TypeInformation.of(ValidateEvenStatsResponse.class));

        StateBootstrapTransformation transformation = OperatorTransformation
                .bootstrapWith(validateEvenStatsResponseDataStream)
                .transform(new EventOperatorStateBootstrapFunction());

        SavepointWriter
                .fromExistingSavepoint(env, TrackJob1Distinct.NEW_SAVEPOINT_PATH, new HashMapStateBackend())
                .withOperator(OperatorIdentifier.forUid("event-etl"), transformation)
                .write(NEW_SAVEPOINT_PATH);

        env.execute("TrackJob2Event");
    }
}
