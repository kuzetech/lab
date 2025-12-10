package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.domain.AuOperatorKeyedState;
import com.kuzetech.bigdata.flink.function.AuOperatorKeyedStateBootstrapper;
import com.kuzetech.bigdata.flink.function.AuOperatorKeyedStateReaderFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@Slf4j
public class WAUJob {
    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                parameterTool.get("derive"),
                new EmbeddedRocksDBStateBackend(true));

        DataStream<AuOperatorKeyedState> wauDataStream = savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-wau-process"),
                        new AuOperatorKeyedStateReaderFunction("wau-state"),
                        Types.STRING,
                        TypeInformation.of(AuOperatorKeyedState.class))
                .filter(value -> !value.getKey().startsWith("demo"));
        StateBootstrapTransformation<AuOperatorKeyedState> wauTransformation = OperatorTransformation
                .bootstrapWith(wauDataStream)
                .keyBy(o -> o.key)
                .transform(new AuOperatorKeyedStateBootstrapper("wau-state"));


        SavepointWriter
                .fromExistingSavepoint(env, parameterTool.get("track"), new EmbeddedRocksDBStateBackend(true))
                .withOperator(OperatorIdentifier.forUid("derive-event-wau-process"), wauTransformation)
                .write(parameterTool.get("target"));

        env.execute("WAUJob");
    }
}
