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
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;

public class DeriveReadJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(32);

        SavepointReader savepoint = SavepointReader.read(
                env,
                "file:///Users/huangsw/code/lab/lab-flink-17/state-processor-track/data/staging/derive",
                new EmbeddedRocksDBStateBackend(true));

        DataStream<IdentifyNewOperatorKeyedState> identifyDataStream = savepoint.readKeyedState(
                OperatorIdentifier.forUid("derive-event-process"),
                new IdentifyNewOperatorKeyedStateReaderFunction(),
                Types.STRING,
                TypeInformation.of(IdentifyNewOperatorKeyedState.class));
        identifyDataStream
                .map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum)
                .print("identifyDataStreamResult: "); //5809096

        DataStream<AuOperatorKeyedState> dauDataStream = savepoint.readKeyedState(
                OperatorIdentifier.forUid("derive-event-dau-process"),
                new AuOperatorKeyedStateReaderFunction("dau-state"),
                Types.STRING,
                TypeInformation.of(AuOperatorKeyedState.class));
        dauDataStream
                .map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum)
                .print("dauDataStreamResult: "); //3907255

        DataStream<AuOperatorKeyedState> wauDataStream = savepoint.readKeyedState(
                OperatorIdentifier.forUid("derive-event-wau-process"),
                new AuOperatorKeyedStateReaderFunction("wau-state"),
                Types.STRING,
                TypeInformation.of(AuOperatorKeyedState.class));
        wauDataStream
                .map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum)
                .print("wauDataStreamResult: "); //3907250

        DataStream<AuOperatorKeyedState> mauDataStream = savepoint.readKeyedState(
                OperatorIdentifier.forUid("derive-event-mau-process"),
                new AuOperatorKeyedStateReaderFunction("mau-state"),
                Types.STRING,
                TypeInformation.of(AuOperatorKeyedState.class));
        mauDataStream
                .map(x -> 1L)
                .returns(Types.LONG)
                .windowAll(GlobalWindows.createWithEndOfStreamTrigger())
                .reduce(Long::sum)
                .print("mauDataStreamResult: "); //3907248

        env.execute("ReadJob");

    }
}
