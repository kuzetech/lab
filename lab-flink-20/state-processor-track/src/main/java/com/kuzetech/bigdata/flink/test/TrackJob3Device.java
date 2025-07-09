package com.kuzetech.bigdata.flink.test;

import com.kuzetech.bigdata.flink.domain.DeviceOperatorKeyedState;
import com.kuzetech.bigdata.flink.function.DeviceOperatorKeyedStateBootstrapper;
import com.kuzetech.bigdata.flink.function.DeviceOperatorKeyedStateReaderFunction;
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
public class TrackJob3Device {

    public static final String OLD_SAVEPOINT_PATH = "file:///Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/savepoint/staging";
    public static final String NEW_SAVEPOINT_PATH = "file:///Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/generate/TrackJob3Device";
    public static final String NEW_SAVEPOINT_DIRECTORY = "/Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/generate/TrackJob3Device";

    public static void main(String[] args) throws Exception {
        FileUtils.deleteDirectory(new File(NEW_SAVEPOINT_DIRECTORY));

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(32);

        SavepointReader savepoint = SavepointReader.read(
                env,
                OLD_SAVEPOINT_PATH,
                new EmbeddedRocksDBStateBackend(true));

        DataStream<DeviceOperatorKeyedState> deviceOperatorKeyedStateDataStream = savepoint.readKeyedState(
                OperatorIdentifier.forUid("user-login-device-state"),
                new DeviceOperatorKeyedStateReaderFunction(),
                Types.STRING,
                TypeInformation.of(DeviceOperatorKeyedState.class));

        StateBootstrapTransformation<DeviceOperatorKeyedState> transformation = OperatorTransformation
                .bootstrapWith(deviceOperatorKeyedStateDataStream)
                .keyBy(o -> o.key)
                .transform(new DeviceOperatorKeyedStateBootstrapper());

        SavepointWriter
                .fromExistingSavepoint(env, TrackJob2Event.NEW_SAVEPOINT_PATH, new EmbeddedRocksDBStateBackend(true))
                .withOperator(OperatorIdentifier.forUid("user-login-device-state"), transformation)
                .write(NEW_SAVEPOINT_PATH);

        env.execute("TrackJob3Device");
    }
}
