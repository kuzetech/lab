package com.kuzetech.bigdata.flinkstate;

import com.xmfunny.funnydb.flink.metadata.MetaDataContent;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class ReadPipelineJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                "/Users/huangsw/code/lab/lab-java-all/flink-state-processor/savepoints/new",
                new EmbeddedRocksDBStateBackend(true));

        DataStream<MetaDataContent> ruleState = savepoint.readListState(
                OperatorIdentifier.forUid("event-etl"),
                "rulesList",
                TypeInformation.of(MetaDataContent.class));

        ruleState.print();

        env.execute("ReadPipelineJob");
    }
}
