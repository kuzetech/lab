package com.kuzetech.bigdata.flinkstate.tracktopipeline;

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
public class PipelineDistinctStateReaderFunction extends KeyedStateReaderFunction<String, Tuple2<String, Boolean>> {

    private ValueState<Boolean> exist;

    @Override
    public void open(Configuration parameters) throws Exception {
        ValueStateDescriptor<Boolean> descriptor =
                new ValueStateDescriptor<>("log-id-exist", Types.BOOLEAN);
        descriptor.enableTimeToLive(StateTtlConfig.DISABLED);

        exist = getRuntimeContext().getState(descriptor);
    }

    @Override
    public void readKey(String key, Context ctx, Collector<Tuple2<String, Boolean>> out) throws Exception {
        Boolean data = exist.value();
        if (data != null) {
            out.collect(Tuple2.of(key, data));
        }
    }
}
