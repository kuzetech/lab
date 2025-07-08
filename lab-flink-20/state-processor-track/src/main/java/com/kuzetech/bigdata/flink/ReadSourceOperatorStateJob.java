package com.kuzetech.bigdata.flink;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.pulsar.source.enumerator.PulsarSourceEnumState;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class ReadSourceOperatorStateJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(32);

        SavepointReader savepoint = SavepointReader.read(
                env,
                "file:///Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/savepoint/staging",
                new HashMapStateBackend());

        DataStream<PulsarSourceEnumState> state = savepoint.readListState(
                OperatorIdentifier.forUid("source-event"),
                "source-event",
                TypeInformation.of(PulsarSourceEnumState.class));

        state.print();

        env.execute("ReadSourceStateJob");

    }
}
