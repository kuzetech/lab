package com.kuzetech.bigdata.flink;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class ReadDistinctStateJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(env, "file:///Users/huangsw/code/lab/lab-flink-20/state-processor-track/data/savepoint/staging", new HashMapStateBackend());

        DataStream<Object> state = savepoint.readUnionState(
                OperatorIdentifier.forUid("source-event"),
                "source-event",
                TypeInformation.of(Object.class));

        state.print();

        env.execute("ReadSourceStateJob");

    }
}
