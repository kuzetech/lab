package com.kuzetech.bigdata.flink17.mutation;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;

import java.util.Map;
import java.util.Random;

@Slf4j
public class MutationReaderDetailFunction extends KeyedStateReaderFunction<String, String> {

    private transient Random random;
    private transient ValueState<Map<String, Object>> dataState;

    @Override
    public void open(Configuration parameters) throws Exception {
        random = new Random();

        ValueStateDescriptor<Map<String, Object>> stateDesc =
                new ValueStateDescriptor<>("state", TypeInformation.of(new TypeHint<>() {
                }));
        stateDesc.enableTimeToLive(StateTtlConfig.DISABLED);
        this.dataState = getRuntimeContext().getState(stateDesc);
    }

    @Override
    public void readKey(String key, Context ctx, Collector<String> out) throws Exception {
        Map<String, Object> value = dataState.value();
        if (value != null && random.nextDouble() < 0.3) {
            String content = key + value.toString();
            log.info(content);
            out.collect(content);
        }
    }
}
