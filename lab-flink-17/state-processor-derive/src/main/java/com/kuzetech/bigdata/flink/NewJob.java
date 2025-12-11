package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.domain.IdentifyNewOperatorKeyedState;
import com.kuzetech.bigdata.flink.function.IdentifyNewOperatorKeyedStateBootstrapper;
import com.kuzetech.bigdata.flink.function.IdentifyNewOperatorKeyedStateReaderFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@Slf4j
public class NewJob {
    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                parameterTool.get("derive"),
                new EmbeddedRocksDBStateBackend(true));

        DataStream<IdentifyNewOperatorKeyedState> newDataStream = savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-process"),
                        new IdentifyNewOperatorKeyedStateReaderFunction(),
                        Types.STRING,
                        TypeInformation.of(IdentifyNewOperatorKeyedState.class))
                .filter(value -> value.getCreatedTs() != null);
        StateBootstrapTransformation<IdentifyNewOperatorKeyedState> newTransformation = OperatorTransformation
                .bootstrapWith(newDataStream)
                .keyBy(o -> o.key)
                .transform(new IdentifyNewOperatorKeyedStateBootstrapper());

        SavepointWriter
                .fromExistingSavepoint(env, parameterTool.get("track"), new EmbeddedRocksDBStateBackend(true))
                .withOperator(OperatorIdentifier.forUid("derive-event-process"), newTransformation)
                .write(parameterTool.get("target"));

        env.execute("NewJob");
    }
}
