package com.kuzetech.bigdata.flink.track;

import com.kuzetech.bigdata.flink.track.domain.DeviceOperatorKeyedState;
import com.kuzetech.bigdata.flink.track.domain.DistinctOperatorKeyedState;
import com.kuzetech.bigdata.flink.track.function.DeviceOperatorKeyedStateReaderFunction;
import com.kuzetech.bigdata.flink.track.function.DistinctOperatorKeyedStateReaderFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;

public class TrackReadJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
//                "file:///Users/huangsw/code/lab/lab-flink-20/state-processor-demo/data/track/staging"
                "file:///Users/huangsw/code/lab/lab-flink-20/state-processor-demo/data/track/20/fix"
                , new EmbeddedRocksDBStateBackend(true));

        DataStream<DistinctOperatorKeyedState> distinctOperatorKeyedStateDataStream = savepoint.readKeyedState(
                OperatorIdentifier.forUid("filter-distinct"),
                new DistinctOperatorKeyedStateReaderFunction(),
                Types.STRING,
                TypeInformation.of(DistinctOperatorKeyedState.class));
        distinctOperatorKeyedStateDataStream
                .map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum)
                .print("distinctOperatorKeyedStateDataStreamResult:") //31946
                .setParallelism(1);

        DataStream<DeviceOperatorKeyedState> deviceOperatorKeyedStateDataStream = savepoint.readKeyedState(
                OperatorIdentifier.forUid("user-login-device-state"),
                new DeviceOperatorKeyedStateReaderFunction(),
                Types.STRING,
                TypeInformation.of(DeviceOperatorKeyedState.class));
        deviceOperatorKeyedStateDataStream
                .map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum)
                .print("deviceOperatorKeyedStateDataStreamResult:") //705858
                .setParallelism(1);

        env.execute();

    }
}
