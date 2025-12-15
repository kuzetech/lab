package com.kuzetech.bigdata.flink.track;

import com.kuzetech.bigdata.flink.track.domain.EnrichOperatorKeyedState;
import com.kuzetech.bigdata.flink.track.function.EnrichOperatorKeyedStateBootstrapper;
import com.kuzetech.bigdata.flink.track.function.EnrichOperatorKeyedStateReaderFunction;
import com.kuzetech.bigdata.flink.util.FlinkEnvironmentUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@Slf4j
public class TrackJob4 {
    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        StreamExecutionEnvironment env = FlinkEnvironmentUtil.getDefaultStreamExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                parameterTool.get("old"),
                new EmbeddedRocksDBStateBackend(true));

        DataStream<EnrichOperatorKeyedState> enrichOperatorKeyedStateDataStream = savepoint.readKeyedState(
                OperatorIdentifier.forUid("device-info-enrich-state"),
                new EnrichOperatorKeyedStateReaderFunction(),
                Types.STRING,
                TypeInformation.of(EnrichOperatorKeyedState.class));

        StateBootstrapTransformation<EnrichOperatorKeyedState> transformation = OperatorTransformation
                .bootstrapWith(enrichOperatorKeyedStateDataStream)
                .keyBy(o -> o.key)
                .transform(new EnrichOperatorKeyedStateBootstrapper());

        SavepointWriter
                .fromExistingSavepoint(env, parameterTool.get("temp"), new EmbeddedRocksDBStateBackend(true))
                .withOperator(OperatorIdentifier.forUid("device-info-enrich-state"), transformation)
                .write(parameterTool.get("new"));

        env.execute("TrackJob4");
    }
}
