package com.kuzetech.bigdata.flinkstate.tracktopipeline;

import com.xmfunny.funnydb.flink.metadata.MetaDataContent;
import com.xmfunny.funnydb.flink.pipeline.validator.ValidateEvenStatsResponse;
import com.xmfunny.funnydb.pipeline.PipelineConfig;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.state.api.functions.StateBootstrapFunction;

public class PipelineProcessorBootstrapFunction extends StateBootstrapFunction<PipelineConfig> {

    private ListState<MetaDataContent> metaDataContentOperatorListState;
    private ListState<ValidateEvenStatsResponse> validateEvenStatsResponseOperatorListState;

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        metaDataContentOperatorListState = context.getOperatorStateStore().getListState(
                new ListStateDescriptor<>("rulesList", MetaDataContent.class));

        validateEvenStatsResponseOperatorListState = context.getOperatorStateStore().getListState(
                new ListStateDescriptor<>("validateStatsList", ValidateEvenStatsResponse.class));
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
    }

    @Override
    public void processElement(PipelineConfig value, Context ctx) throws Exception {
        metaDataContentOperatorListState.add(MetaDataContent.generateFromPipelineConfig(value));
    }


}
