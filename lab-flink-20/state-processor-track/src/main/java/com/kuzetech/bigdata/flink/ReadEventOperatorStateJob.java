package com.kuzetech.bigdata.flink;

import com.xmfunny.funnydb.flink.metadata.MetaDataContent;
import com.xmfunny.funnydb.flink.pipeline.validator.ValidateEvenStatsResponse;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class ReadEventOperatorStateJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                "file:///Users/huangsw/code/lab/lab-flink-20/state-processor-track/temp/savepoint/staging",
                new HashMapStateBackend());

        DataStream<ValidateEvenStatsResponse> validateEvenStatsResponseDataStream = savepoint.readListState(
                OperatorIdentifier.forUid("event-etl"),
                "validateStatsList",
                TypeInformation.of(ValidateEvenStatsResponse.class));

        validateEvenStatsResponseDataStream.print();

        DataStream<MetaDataContent> metaDataContentDataStream = savepoint.readListState(
                OperatorIdentifier.forUid("event-etl"),
                "rulesList",
                TypeInformation.of(MetaDataContent.class));

        metaDataContentDataStream.print();

        env.execute("ReadIngestEventProcessFunctionJob");

    }
}
