package com.kuzetech.bigdata.flinkstate.keepalivetoderive;

import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;

public class KeepaliveBootstrapper extends KeyedStateBootstrapFunction<String, Tuple2<String, Long>> {

    private ValueState<Long> lastTsState;

    @Override
    public void open(Configuration parameters) {
        final ValueStateDescriptor<Long> lastTsStageDesc = new ValueStateDescriptor<>("last-ts-state", Types.LONG);

        StateTtlConfig ttlCfg = StateTtlConfig.newBuilder(Time.days(3))
                .setUpdateType(StateTtlConfig.UpdateType.OnReadAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.ReturnExpiredIfNotCleanedUp)
                .build();

        lastTsStageDesc.enableTimeToLive(ttlCfg);

        lastTsState = getRuntimeContext().getState(lastTsStageDesc);
    }

    @Override
    public void processElement(Tuple2<String, Long> value, Context ctx) throws Exception {
        lastTsState.update(value.f1);
    }
}