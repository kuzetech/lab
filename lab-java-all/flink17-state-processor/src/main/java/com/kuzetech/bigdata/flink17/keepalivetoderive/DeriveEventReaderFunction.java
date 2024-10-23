package com.kuzetech.bigdata.flink17.keepalivetoderive;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;

@Slf4j
public class DeriveEventReaderFunction extends KeyedStateReaderFunction<String, Tuple2<String, Long>> {

    private ValueState<Long> createdTsState;

    @Override
    public void open(Configuration parameters) throws Exception {
        ValueStateDescriptor<Long> initCreatedTsStateDesc = new ValueStateDescriptor<>("created-ts-state", Types.LONG);
        initCreatedTsStateDesc.enableTimeToLive(StateTtlConfig.DISABLED);
        this.createdTsState = getRuntimeContext().getState(initCreatedTsStateDesc);
    }

    @Override
    public void readKey(String key, Context ctx, Collector<Tuple2<String, Long>> out) throws Exception {
        Long value = createdTsState.value();
        if (value != null) {
            out.collect(Tuple2.of(key, value));
        }
    }
}
