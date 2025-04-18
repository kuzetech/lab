package com.kuzetech.bigdata.flink17.tracktopipeline;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class PipelineDistinctStateReaderFunction extends KeyedStateReaderFunction<String, Tuple2<String, Boolean>> {

    private ValueState<Boolean> exist;

    @Override
    public void open(Configuration parameters) throws Exception {
        ValueStateDescriptor<Boolean> descriptor =
                new ValueStateDescriptor<>("log-id-exist", Types.BOOLEAN);

        StateTtlConfig ttlConfig = StateTtlConfig
                .newBuilder(Time.days(10))
                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                .build();

        descriptor.enableTimeToLive(ttlConfig);

        exist = getRuntimeContext().getState(descriptor);
    }

    @Override
    public void readKey(String key, Context ctx, Collector<Tuple2<String, Boolean>> out) throws Exception {
        Boolean data = exist.value();
        if (StringUtils.isNotEmpty(key) && data != null) {
            out.collect(Tuple2.of(key, data));
        }
    }
}
