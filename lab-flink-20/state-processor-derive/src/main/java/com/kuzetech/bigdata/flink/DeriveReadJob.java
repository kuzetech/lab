package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.domain.AuOperatorKeyedState;
import com.kuzetech.bigdata.flink.domain.IdentifyNewOperatorKeyedState;
import com.kuzetech.bigdata.flink.function.AuOperatorKeyedStateReaderFunction;
import com.kuzetech.bigdata.flink.function.IdentifyNewOperatorKeyedStateReaderFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;

public class DeriveReadJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                "file:///Users/huangsw/code/lab/lab-flink-17/state-processor-derive/data/staging/derive",
                new EmbeddedRocksDBStateBackend(true));

        savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-process"),
                        new IdentifyNewOperatorKeyedStateReaderFunction(),
                        Types.STRING,
                        TypeInformation.of(IdentifyNewOperatorKeyedState.class))
                .map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum)
                .print("identifyDataStreamResult: "); //5809096

        savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-dau-process"),
                        new AuOperatorKeyedStateReaderFunction("dau-state"),
                        Types.STRING,
                        TypeInformation.of(AuOperatorKeyedState.class)).map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum)
                .print("dauDataStreamResult: "); //3907255

        savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-wau-process"),
                        new AuOperatorKeyedStateReaderFunction("wau-state"),
                        Types.STRING,
                        TypeInformation.of(AuOperatorKeyedState.class))
                .map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum)
                .print("wauDataStreamResult: "); //3907250

        savepoint.readKeyedState(
                        OperatorIdentifier.forUid("derive-event-mau-process"),
                        new AuOperatorKeyedStateReaderFunction("mau-state"),
                        Types.STRING,
                        TypeInformation.of(AuOperatorKeyedState.class)).map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum)
                .print("mauDataStreamResult: "); //3907248

        env.execute("ReadJob");

    }
}
