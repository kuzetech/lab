package com.kuzetech.bigdata.flinkstate;

import com.kuzetech.bigdata.flinkstate.mutation.MutationReaderDetailFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@Slf4j
public class ReadMutationDetailJob {

    public static void main(String[] args) throws Exception {
        String spPath = "/Users/huangsw/code/lab/lab-java-all/flink-state-processor/savepoints/dev-mutation";
        if (args.length > 0) {
            spPath = args[0];
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                spPath,
                new EmbeddedRocksDBStateBackend(true));

        DataStream<String> mutationState = savepoint.readKeyedState(
                OperatorIdentifier.forUid("mutation-state-process"),
                new MutationReaderDetailFunction());

        mutationState.print();

        env.execute("ReadMutationDetailJob");
    }


}
