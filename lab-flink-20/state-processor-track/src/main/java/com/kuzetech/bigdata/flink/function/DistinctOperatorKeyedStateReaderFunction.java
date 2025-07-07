package com.kuzetech.bigdata.flink.function;

import com.kuzetech.bigdata.flink.domain.DistinctOperatorKeyedState;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;

public class DistinctOperatorKeyedStateReaderFunction extends KeyedStateReaderFunction<String, DistinctOperatorKeyedState> {

    ValueState<Boolean> existState;

    @Override
    public void open(Configuration configuration) throws Exception {
        StateTtlConfig ttlConfig = StateTtlConfig
                .newBuilder(Time.minutes(10))
                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.ReturnExpiredIfNotCleanedUp)
                .build();
        ValueStateDescriptor<Boolean> stateDescriptor = new ValueStateDescriptor<>("log-id-exist", Types.BOOLEAN);
        stateDescriptor.enableTimeToLive(ttlConfig);
        existState = getRuntimeContext().getState(stateDescriptor);
    }

    @Override
    public void readKey(String key, Context context, Collector<DistinctOperatorKeyedState> out) throws Exception {
        DistinctOperatorKeyedState data = new DistinctOperatorKeyedState();
        data.key = key;
        data.exist = existState.value();
        out.collect(data);
    }
}
