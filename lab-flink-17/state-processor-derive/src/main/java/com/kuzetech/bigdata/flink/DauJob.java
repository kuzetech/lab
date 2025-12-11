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
public class DauJob {
    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                parameterTool.get("derive"),
                new EmbeddedRocksDBStateBackend(true));

        DataStream<AuOperatorKeyedState> dauDataStream = savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-dau-process"),
                        new AuOperatorKeyedStateReaderFunction("dau-state"),
                        Types.STRING,
                        TypeInformation.of(AuOperatorKeyedState.class))
                .filter(value -> value.getActiveMark() != null);
        StateBootstrapTransformation<AuOperatorKeyedState> dauTransformation = OperatorTransformation
                .bootstrapWith(dauDataStream)
                .keyBy(o -> o.key)
                .transform(new AuOperatorKeyedStateBootstrapper("dau-state"));

        SavepointWriter
                .fromExistingSavepoint(env, parameterTool.get("track"), new EmbeddedRocksDBStateBackend(true))
                .withOperator(OperatorIdentifier.forUid("derive-event-dau-process"), dauTransformation)
                .write(parameterTool.get("target"));

        env.execute("DauJob");
    }
}
