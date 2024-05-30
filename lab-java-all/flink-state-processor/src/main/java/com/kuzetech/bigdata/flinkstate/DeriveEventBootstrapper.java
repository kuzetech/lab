package com.kuzetech.bigdata.flinkstate;

import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;

public class DeriveEventBootstrapper extends KeyedStateBootstrapFunction<String, Tuple2<String, Long>> {

    private ValueState<Long> createdTsState;

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<Long> initCreatedTsStateDesc = new ValueStateDescriptor<>("created-ts-state", Types.LONG);
        initCreatedTsStateDesc.enableTimeToLive(StateTtlConfig.DISABLED);
        this.createdTsState = getRuntimeContext().getState(initCreatedTsStateDesc);
    }

    @Override
    public void processElement(Tuple2<String, Long> value, Context ctx) throws Exception {
        createdTsState.update(value.f1);
    }
}