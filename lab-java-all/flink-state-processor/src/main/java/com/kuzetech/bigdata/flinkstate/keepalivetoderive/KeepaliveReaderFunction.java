package com.kuzetech.bigdata.flinkstate.keepalivetoderive;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;

@Slf4j
public class KeepaliveReaderFunction extends KeyedStateReaderFunction<String, Tuple2<String, Long>> {

    private ValueState<Long> lastTsState;

    @Override
    public void open(Configuration parameters) throws Exception {
        final ValueStateDescriptor<Long> lastTsStageDesc = new ValueStateDescriptor<>("last-ts-state", Types.LONG);

        StateTtlConfig ttlCfg = StateTtlConfig.newBuilder(Time.days(3))
                .setUpdateType(StateTtlConfig.UpdateType.OnReadAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                .build();

        lastTsStageDesc.enableTimeToLive(ttlCfg);

        lastTsState = getRuntimeContext().getState(lastTsStageDesc);
    }

    @Override
    public void readKey(String key, Context ctx, Collector<Tuple2<String, Long>> out) throws Exception {
        Long value = lastTsState.value();
        if (value != null) {
            out.collect(Tuple2.of(key, value));
        }
    }
}
