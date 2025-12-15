package com.kuzetech.bigdata.flink.derive;

import com.kuzetech.bigdata.flink.domain.IdentifyNewOperatorKeyedState;
import com.kuzetech.bigdata.flink.function.IdentifyNewOperatorKeyedStateBootstrapper;
import com.kuzetech.bigdata.flink.function.IdentifyNewOperatorKeyedStateReaderFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.BatchExecutionOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import static org.apache.flink.configuration.RestOptions.BIND_PORT;
import static org.apache.flink.configuration.TaskManagerOptions.NUM_TASK_SLOTS;

@Slf4j
public class DeriveWriteDebugJob {
    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        Configuration config = new Configuration();
        config.set(BIND_PORT, "49316");
        config.set(BatchExecutionOptions.ADAPTIVE_AUTO_PARALLELISM_ENABLED, false);
        config.set(NUM_TASK_SLOTS, 16);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);
        env.setRuntimeMode(RuntimeExecutionMode.BATCH);
        env.setParallelism(16);

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

        env.execute("DeriveJob");
    }
}
