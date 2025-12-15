package com.kuzetech.bigdata.flink.track.function;

import com.xmfunny.funnydb.flink.metadata.MetaDataContent;
import com.xmfunny.funnydb.flink.pipeline.validator.ValidateEvenStatsResponse;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.state.api.functions.StateBootstrapFunction;

public class EventOperatorStateBootstrapFunction extends StateBootstrapFunction<ValidateEvenStatsResponse> {

    private ListState<MetaDataContent> metaDataContentOperatorListState;
    private ListState<ValidateEvenStatsResponse> validateEvenStatsResponseOperatorListState;

    @Override
    public void processElement(ValidateEvenStatsResponse validateEvenStatsResponse, Context context) throws Exception {
        validateEvenStatsResponseOperatorListState.add(validateEvenStatsResponse);
    }

    @Override
    public void snapshotState(FunctionSnapshotContext functionSnapshotContext) throws Exception {

    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        validateEvenStatsResponseOperatorListState = context.getOperatorStateStore().getListState(
                new ListStateDescriptor<>("validateStatsList", ValidateEvenStatsResponse.class));
        metaDataContentOperatorListState = context.getOperatorStateStore().getListState(
                new ListStateDescriptor<>("rulesList", MetaDataContent.class));
    }
}
