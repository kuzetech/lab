package com.kuzetech.bigdata.flinkstate;

import com.kuzetech.bigdata.flinkstate.tracktopipeline.*;
import com.xmfunny.funnydb.flink.model.DeviceInfoCacheData;
import com.xmfunny.funnydb.pipeline.PipelineConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class MergeTrackToPipelineNewJob {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            throw new RuntimeException("args length must >= 3");
        }

        String trackSavepointPath = args[0];
        String pipelineSavepointPath = args[1];
        String newSavepointPath = args[2];

        StreamExecutionEnvironment env;
        if (args.length > 3) {
            Configuration configuration = new Configuration();
            configuration.setString(RestOptions.BIND_PORT, "9988");
            env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
        } else {
            env = StreamExecutionEnvironment.getExecutionEnvironment();
        }

        SavepointReader trackSavepoint = SavepointReader.read(
                env,
                trackSavepointPath,
                new EmbeddedRocksDBStateBackend(true));

        SavepointReader pipelineSavepoint = SavepointReader.read(
                env,
                pipelineSavepointPath,
                new EmbeddedRocksDBStateBackend(true));

        DataStream<Tuple2<String, Boolean>> distinctState = pipelineSavepoint.readKeyedState(
                OperatorIdentifier.forUid("distinct"),
                new PipelineDistinctStateReaderFunction());

        StateBootstrapTransformation<Tuple2<String, Boolean>> distinctStateTransformation = OperatorTransformation
                .bootstrapWith(distinctState)
                .keyBy(s -> s.f0)
                .transform(new PipelineDistinctStateBootstrapper());

        DataStream<PipelineConfig> ruleState = pipelineSavepoint.readListState(
                OperatorIdentifier.forUid("evaluator"),
                "rulesList",
                TypeInformation.of(PipelineConfig.class));

        StateBootstrapTransformation<PipelineConfig> ruleStateTransformation = OperatorTransformation
                .bootstrapWith(ruleState)
                .transform(new PipelineProcessorBootstrapFunction());

        DataStream<Tuple2<String, String>> userLoginDeviceState = trackSavepoint.readKeyedState(
                OperatorIdentifier.forUid("user-login-device-state"),
                new TrackUserLoginDeviceStateReaderFunction());

        StateBootstrapTransformation<Tuple2<String, String>> userLoginDeviceStateTransformation = OperatorTransformation
                .bootstrapWith(userLoginDeviceState)
                .keyBy(s -> s.f0)
                .transform(new TrackUserLoginDeviceStateBootstrapper());

        DataStream<Tuple2<String, DeviceInfoCacheData>> deviceInfoEnrichState = trackSavepoint.readKeyedState(
                OperatorIdentifier.forUid("device-info-enrich-state"),
                new TrackDeviceInfoEnrichStateReaderFunction());

        StateBootstrapTransformation<Tuple2<String, DeviceInfoCacheData>> deviceInfoEnrichStateTransformation = OperatorTransformation
                .bootstrapWith(deviceInfoEnrichState)
                .keyBy(s -> s.f0)
                .transform(new TrackDeviceInfoEnrichStateBootstrapper());

        SavepointWriter
                .newSavepoint(env, new EmbeddedRocksDBStateBackend(true), 100)
                .withOperator(OperatorIdentifier.forUid("filter-distinct"), distinctStateTransformation)
                .withOperator(OperatorIdentifier.forUid("event-etl"), ruleStateTransformation)
                .withOperator(OperatorIdentifier.forUid("user-login-device-state"), userLoginDeviceStateTransformation)
                .withOperator(OperatorIdentifier.forUid("device-info-enrich-state"), deviceInfoEnrichStateTransformation)
                .write(newSavepointPath);

        env.execute("MergeTrackToPipelineNewJob");
    }
}
