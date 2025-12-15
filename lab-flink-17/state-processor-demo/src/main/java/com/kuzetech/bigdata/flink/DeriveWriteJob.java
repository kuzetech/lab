package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.domain.AuOperatorKeyedState;
import com.kuzetech.bigdata.flink.domain.IdentifyNewOperatorKeyedState;
import com.kuzetech.bigdata.flink.function.AuOperatorKeyedStateBootstrapper;
import com.kuzetech.bigdata.flink.function.AuOperatorKeyedStateReaderFunction;
import com.kuzetech.bigdata.flink.function.IdentifyNewOperatorKeyedStateBootstrapper;
import com.kuzetech.bigdata.flink.function.IdentifyNewOperatorKeyedStateReaderFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.BatchExecutionOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@Slf4j
public class DeriveWriteJob {
    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        Configuration config = new Configuration();
        config.set(BatchExecutionOptions.ADAPTIVE_AUTO_PARALLELISM_ENABLED, false);
        config.set(TaskManagerOptions.NUM_TASK_SLOTS, 16);

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

        DataStream<AuOperatorKeyedState> wauDataStream = savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-wau-process"),
                        new AuOperatorKeyedStateReaderFunction("wau-state"),
                        Types.STRING,
                        TypeInformation.of(AuOperatorKeyedState.class))
                .filter(value -> value.getActiveMark() != null);
        StateBootstrapTransformation<AuOperatorKeyedState> wauTransformation = OperatorTransformation
                .bootstrapWith(wauDataStream)
                .keyBy(o -> o.key)
                .transform(new AuOperatorKeyedStateBootstrapper("wau-state"));

        DataStream<AuOperatorKeyedState> mauDataStream = savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-mau-process"),
                        new AuOperatorKeyedStateReaderFunction("mau-state"),
                        Types.STRING,
                        TypeInformation.of(AuOperatorKeyedState.class))
                .filter(value -> value.getActiveMark() != null);
        StateBootstrapTransformation<AuOperatorKeyedState> mauTransformation = OperatorTransformation
                .bootstrapWith(mauDataStream)
                .keyBy(o -> o.key)
                .transform(new AuOperatorKeyedStateBootstrapper("mau-state"));

        SavepointWriter
                .fromExistingSavepoint(env, parameterTool.get("track"), new EmbeddedRocksDBStateBackend(true))
                .withOperator(OperatorIdentifier.forUid("derive-event-process"), newTransformation)
                .withOperator(OperatorIdentifier.forUid("derive-event-dau-process"), dauTransformation)
                .withOperator(OperatorIdentifier.forUid("derive-event-wau-process"), wauTransformation)
                .withOperator(OperatorIdentifier.forUid("derive-event-mau-process"), mauTransformation)
                .write(parameterTool.get("target"));

        env.execute("DeriveWriteJob");
    }
}
