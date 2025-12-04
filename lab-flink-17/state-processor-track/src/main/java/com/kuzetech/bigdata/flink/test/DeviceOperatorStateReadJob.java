package com.kuzetech.bigdata.flink.test;

import com.kuzetech.bigdata.flink.domain.DeviceOperatorKeyedState;
import com.kuzetech.bigdata.flink.function.DeviceOperatorKeyedStateReaderFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.ContinuousProcessingTimeTrigger;

public class DeviceOperatorStateReadJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(32);

        SavepointReader savepoint = SavepointReader.read(
                env,
                "file:///Users/huangsw/code/lab/lab-flink-17/state-processor-track/data/savepoint/track/staging/source/256",
                new EmbeddedRocksDBStateBackend(true));

        DataStream<DeviceOperatorKeyedState> deviceOperatorKeyedStateDataStream = savepoint.readKeyedState(
                OperatorIdentifier.forUid("user-login-device-state"),
                new DeviceOperatorKeyedStateReaderFunction(),
                Types.STRING,
                TypeInformation.of(DeviceOperatorKeyedState.class));

        DataStream<Long> totalCount = deviceOperatorKeyedStateDataStream
                .map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.create())
                .trigger(ContinuousProcessingTimeTrigger.of(Time.seconds(30)))
                .reduce(Long::sum);

        totalCount.print(); // 3260604

        env.execute("DeviceOperatorStateReadJob");

    }
}
