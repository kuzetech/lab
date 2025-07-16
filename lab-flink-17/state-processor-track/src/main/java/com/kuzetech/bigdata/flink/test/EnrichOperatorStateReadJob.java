package com.kuzetech.bigdata.flink.test;

import com.kuzetech.bigdata.flink.domain.EnrichOperatorKeyedState;
import com.kuzetech.bigdata.flink.function.EnrichOperatorKeyedStateReaderFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class EnrichOperatorStateReadJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(32);

        SavepointReader savepoint = SavepointReader.read(
                env,
                // "file:///Users/huangsw/code/lab/lab-flink-17/state-processor-track/data/savepoint/track/staging/source/256",
                "file:///Users/huangsw/code/lab/lab-flink-17/state-processor-track/data/savepoint/track/local/gen/512/enrich",
                new EmbeddedRocksDBStateBackend(true));

        DataStream<EnrichOperatorKeyedState> enrichOperatorKeyedStateDataStream = savepoint.readKeyedState(
                OperatorIdentifier.forUid("device-info-enrich-state"),
                new EnrichOperatorKeyedStateReaderFunction(),
                Types.STRING,
                TypeInformation.of(EnrichOperatorKeyedState.class));

        enrichOperatorKeyedStateDataStream.print();

        env.execute("EnrichOperatorStateReadJob");

    }
}
