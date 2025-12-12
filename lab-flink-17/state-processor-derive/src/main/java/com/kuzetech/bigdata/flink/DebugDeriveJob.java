package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.domain.IdentifyNewOperatorKeyedState;
import com.kuzetech.bigdata.flink.function.IdentifyNewOperatorKeyedStateBootstrapper;
import com.kuzetech.bigdata.flink.function.IdentifyNewOperatorKeyedStateReaderFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import static org.apache.flink.configuration.RestOptions.BIND_PORT;
import static org.apache.flink.configuration.TaskManagerOptions.*;

@Slf4j
public class DebugDeriveJob {
    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        Configuration config = new Configuration();
        config.set(BIND_PORT, "49316");
        config.set(JVM_OVERHEAD_MIN, MemorySize.parse("512m"));
        config.set(JVM_OVERHEAD_MAX, MemorySize.parse("512m"));
        config.set(JVM_METASPACE, MemorySize.parse("256m"));
        config.set(NETWORK_MEMORY_MIN, MemorySize.parse("64m"));
        config.set(NETWORK_MEMORY_MAX, MemorySize.parse("64m"));
        config.set(FRAMEWORK_HEAP_MEMORY, MemorySize.parse("128m"));
        config.set(FRAMEWORK_OFF_HEAP_MEMORY, MemorySize.parse("128m"));
        config.set(TASK_HEAP_MEMORY, MemorySize.parse("2g"));
        config.set(TASK_OFF_HEAP_MEMORY, MemorySize.ZERO);
        config.set(MANAGED_MEMORY_SIZE, MemorySize.ZERO);
        config.set(NUM_TASK_SLOTS, 16);
        
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);
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
