package com.kuzetech.bigdata.flink.test;

import com.kuzetech.bigdata.flink.domain.DistinctOperatorKeyedState;
import com.kuzetech.bigdata.flink.function.DistinctOperatorKeyedStateReaderFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;

public class DistinctOperatorStateReadJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(32);

        SavepointReader savepoint = SavepointReader.read(
                env,
                // DistinctOperatorStateBuildJob.NEW_SAVEPOINT_PATH,
                TrackJob4Enrich.NEW_SAVEPOINT_PATH,
                new EmbeddedRocksDBStateBackend(true));

        DataStream<DistinctOperatorKeyedState> distinctOperatorKeyedStateDataStream = savepoint.readKeyedState(
                OperatorIdentifier.forUid("filter-distinct"),
                new DistinctOperatorKeyedStateReaderFunction(),
                Types.STRING,
                TypeInformation.of(DistinctOperatorKeyedState.class));

        DataStream<Long> totalCount = distinctOperatorKeyedStateDataStream
                .map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum);

        totalCount.print(); // 16662

        env.execute("DistinctOperatorStateReadJob");

    }
}
