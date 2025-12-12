package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.domain.IdentifyNewOperatorKeyedState;
import com.kuzetech.bigdata.flink.function.IdentifyNewOperatorKeyedStateReaderFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;

@Slf4j
public class DeriveReadNewJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                //"file:///Users/huangsw/code/lab/lab-flink-17/state-processor-derive/data/staging/derive",
                "file:///Users/huangsw/code/lab/lab-flink-17/state-processor-track/data/gen/fix",
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

        env.execute("ReadJob");

    }
}
