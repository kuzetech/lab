package com.kuzetech.bigdata.flink.test;

import com.kuzetech.bigdata.flink.function.EventOperatorStateBootstrapFunction;
import com.xmfunny.funnydb.flink.pipeline.validator.ValidateEvenStatsResponse;
import org.apache.commons.io.FileUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;

import java.io.File;

public class EventOperatorStateBuildJob {
    public static final String OLD_SAVEPOINT_PATH = "file:///Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/savepoint/staging";
    public static final String NEW_SAVEPOINT_PATH = "file:///Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/generate/EventOperatorStateBuildJob";
    public static final String NEW_SAVEPOINT_DIRECTORY = "/Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/generate/EventOperatorStateBuildJob";

    public static void main(String[] args) throws Exception {
        FileUtils.deleteDirectory(new File(NEW_SAVEPOINT_DIRECTORY));

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(32);

        SavepointReader savepoint = SavepointReader.read(
                env,
                OLD_SAVEPOINT_PATH,
                new HashMapStateBackend());

        DataStream<ValidateEvenStatsResponse> validateEvenStatsResponseDataStream = savepoint.readListState(
                OperatorIdentifier.forUid("event-etl"),
                "validateStatsList",
                TypeInformation.of(ValidateEvenStatsResponse.class));

        DataStream<Long> totalCount = validateEvenStatsResponseDataStream
                .map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum);

        totalCount.print(); //1

        StateBootstrapTransformation transformation = OperatorTransformation
                .bootstrapWith(validateEvenStatsResponseDataStream)
                .transform(new EventOperatorStateBootstrapFunction());

        SavepointWriter
                .newSavepoint(env, new HashMapStateBackend(), 256)
                .withOperator(OperatorIdentifier.forUid("event-etl"), transformation)
                .write(NEW_SAVEPOINT_PATH);

        env.execute("EventOperatorStateBuildJob");

    }
}
