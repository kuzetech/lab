package com.kuzetech.bigdata.flink.derive;

import com.kuzetech.bigdata.flink.derive.domain.AuOperatorKeyedState;
import com.kuzetech.bigdata.flink.derive.domain.IdentifyNewOperatorKeyedState;
import com.kuzetech.bigdata.flink.derive.function.AuOperatorKeyedStateReaderFunction;
import com.kuzetech.bigdata.flink.derive.function.IdentifyNewOperatorKeyedStateReaderFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;

@Slf4j
public class DeriveReadJob {

    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                parameterTool.get("derive"),
                new EmbeddedRocksDBStateBackend(true));

        savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-process"),
                        new IdentifyNewOperatorKeyedStateReaderFunction(),
                        Types.STRING,
                        TypeInformation.of(IdentifyNewOperatorKeyedState.class))
                .filter(value -> value.getCreatedTs() != null)
                .map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum)
                .print("newDataStreamResult: ") //1841838
                .setParallelism(1);

        savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-dau-process"),
                        new AuOperatorKeyedStateReaderFunction("dau-state"),
                        Types.STRING,
                        TypeInformation.of(AuOperatorKeyedState.class))
                .filter(value -> value.getActiveMark() != null)
                .map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum)
                .print("dauDataStreamResult: ")
                .setParallelism(1);

        savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-wau-process"),
                        new AuOperatorKeyedStateReaderFunction("wau-state"),
                        Types.STRING,
                        TypeInformation.of(AuOperatorKeyedState.class))
                .filter(value -> value.getActiveMark() != null)
                .map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum)
                .print("wauDataStreamResult: ")
                .setParallelism(1);

        savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-mau-process"),
                        new AuOperatorKeyedStateReaderFunction("mau-state"),
                        Types.STRING,
                        TypeInformation.of(AuOperatorKeyedState.class))
                .filter(value -> value.getActiveMark() != null)
                .map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum)
                .print("mauDataStreamResult: ")
                .setParallelism(1);

        env.execute();

    }
}
