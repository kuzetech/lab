package com.kuzetech.bigdata.flinkstate;

import com.kuzetech.bigdata.flinkstate.tracktopipeline.PipelineDistinctStateBootstrapper;
import com.kuzetech.bigdata.flinkstate.tracktopipeline.PipelineDistinctStateReaderFunction;
import com.kuzetech.bigdata.flinkstate.tracktopipeline.PipelineProcessorBootstrapFunction;
import com.xmfunny.funnydb.pipeline.PipelineConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class MergeTrackToPipelineJob {

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
            configuration.setInteger(TaskManagerOptions.NUM_TASK_SLOTS, 6);
            env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
        } else {
            env = StreamExecutionEnvironment.getExecutionEnvironment();
        }
        env.setParallelism(6);

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

        SavepointWriter
                .fromExistingSavepoint(env, trackSavepointPath, new EmbeddedRocksDBStateBackend(true))
                .withOperator(OperatorIdentifier.forUid("filter-distinct"), distinctStateTransformation)
                .withOperator(OperatorIdentifier.forUid("event-etl"), ruleStateTransformation)
                .write(newSavepointPath);

        env.execute("MergeTrackToPipelineJob");
    }
}
