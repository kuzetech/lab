package com.kuzetech.bigdata.flink17.mutation;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;

import java.util.Map;

@Slf4j
public class MutationReaderFunction extends KeyedStateReaderFunction<String, Tuple2<String, Integer>> {

    private transient ValueState<Map<String, Object>> dataState;

    @Override
    public void open(Configuration parameters) throws Exception {
        ValueStateDescriptor<Map<String, Object>> stateDesc =
                new ValueStateDescriptor<>("state", TypeInformation.of(new TypeHint<>() {
                }));
        stateDesc.enableTimeToLive(StateTtlConfig.DISABLED);
        this.dataState = getRuntimeContext().getState(stateDesc);
    }

    @Override
    public void readKey(String key, Context ctx, Collector<Tuple2<String, Integer>> out) throws Exception {
        Map<String, Object> value = dataState.value();
        if (value != null) {
            String realKey = key.substring(0, key.lastIndexOf("#"));
            out.collect(Tuple2.of(realKey, value.size()));
        }
    }
}
