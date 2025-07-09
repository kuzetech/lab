package com.kuzetech.bigdata.flink.work;

import com.kuzetech.bigdata.flink.domain.DeviceOperatorKeyedState;
import com.kuzetech.bigdata.flink.function.DeviceOperatorKeyedStateBootstrapper;
import com.kuzetech.bigdata.flink.function.DeviceOperatorKeyedStateReaderFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@Slf4j
public class TrackJob3 {
    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                parameterTool.get("old"),
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
                .fromExistingSavepoint(env, parameterTool.get("temp"), new EmbeddedRocksDBStateBackend(true))
                .withOperator(OperatorIdentifier.forUid("user-login-device-state"), transformation)
                .write(parameterTool.get("new"));

        env.execute("TrackJob3");
    }
}
