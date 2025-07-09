package com.kuzetech.bigdata.flink.test;

import com.xmfunny.funnydb.flink.pipeline.validator.ValidateEvenStatsResponse;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;

public class EventOperatorStateReadJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(32);

        SavepointReader savepoint = SavepointReader.read(
                env,
                // EventOperatorStateBuildJob.NEW_SAVEPOINT_PATH,
                TrackJob4Enrich.NEW_SAVEPOINT_PATH,
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

        env.execute("EventOperatorStateReadJob");

    }
}
