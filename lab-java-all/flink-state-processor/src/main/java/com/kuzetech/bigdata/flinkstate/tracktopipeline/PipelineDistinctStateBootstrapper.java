package com.kuzetech.bigdata.flinkstate.tracktopipeline;

import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;

public class PipelineDistinctStateBootstrapper extends KeyedStateBootstrapFunction<String, Tuple2<String, Boolean>> {

    private ValueState<Boolean> exist;

    @Override
    public void open(Configuration parameters) {
        StateTtlConfig ttlConfig = StateTtlConfig
                .newBuilder(Time.minutes(10))
                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                .build();

        ValueStateDescriptor<Boolean> descriptor =
                new ValueStateDescriptor<>("log-id-exist", Types.BOOLEAN);
        descriptor.enableTimeToLive(ttlConfig);

        exist = getRuntimeContext().getState(descriptor);
    }

    @Override
    public void processElement(Tuple2<String, Boolean> value, Context ctx) throws Exception {
        exist.update(value.f1);
    }
}