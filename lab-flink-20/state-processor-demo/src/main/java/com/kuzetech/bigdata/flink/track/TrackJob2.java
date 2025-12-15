package com.kuzetech.bigdata.flink.track;

import com.kuzetech.bigdata.flink.function.EventOperatorStateBootstrapFunction;
import com.xmfunny.funnydb.flink.pipeline.validator.ValidateEvenStatsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@Slf4j
public class TrackJob2 {
    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                parameterTool.get("old"),
                new EmbeddedRocksDBStateBackend(true));

        DataStream<ValidateEvenStatsResponse> validateEvenStatsResponseDataStream = savepoint.readListState(
                OperatorIdentifier.forUid("event-etl"),
                "validateStatsList",
                TypeInformation.of(ValidateEvenStatsResponse.class));

        StateBootstrapTransformation transformation = OperatorTransformation
                .bootstrapWith(validateEvenStatsResponseDataStream)
                .transform(new EventOperatorStateBootstrapFunction());

        SavepointWriter
                .fromExistingSavepoint(env, parameterTool.get("temp"), new HashMapStateBackend())
                .withOperator(OperatorIdentifier.forUid("event-etl"), transformation)
                .write(parameterTool.get("new"));

        env.execute("TrackJob2");
    }
}
