package com.kuzetech.bigdata.flink.work;

import com.kuzetech.bigdata.flink.domain.DistinctOperatorKeyedState;
import com.kuzetech.bigdata.flink.function.DistinctOperatorKeyedStateBootstrapper;
import com.kuzetech.bigdata.flink.function.DistinctOperatorKeyedStateReaderFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@Slf4j
public class TrackJob1 {
    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                parameterTool.get("old"),
                new EmbeddedRocksDBStateBackend(true));

        DataStream<DistinctOperatorKeyedState> distinctOperatorKeyedStateDataStream = savepoint.readKeyedState(
                OperatorIdentifier.forUid("filter-distinct"),
                new DistinctOperatorKeyedStateReaderFunction(),
                Types.STRING,
                TypeInformation.of(DistinctOperatorKeyedState.class));

        StateBootstrapTransformation<DistinctOperatorKeyedState> transformation = OperatorTransformation
                .bootstrapWith(distinctOperatorKeyedStateDataStream)
                .keyBy(o -> o.key)
                .transform(new DistinctOperatorKeyedStateBootstrapper());

        SavepointWriter
                .newSavepoint(env, new EmbeddedRocksDBStateBackend(true), parameterTool.getInt("max", 512))
                .withOperator(OperatorIdentifier.forUid("filter-distinct"), transformation)
                .write(parameterTool.get("new"));

        env.execute("TrackJob1");
    }
}
