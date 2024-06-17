package com.kuzetech.bigdata.flinkstate.tracktopipeline;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;

@Slf4j
public class TrackUserLoginDeviceStateReaderFunction extends KeyedStateReaderFunction<String, Tuple2<String, String>> {

    private ValueState<String> userLoginDeviceState;

    @Override
    public void open(Configuration parameters) throws Exception {
        ValueStateDescriptor<String> userDeviceStateDesc = new ValueStateDescriptor<>("user-login-device-state", Types.STRING);
        userDeviceStateDesc.enableTimeToLive(StateTtlConfig.DISABLED);
        this.userLoginDeviceState = getRuntimeContext().getState(userDeviceStateDesc);
    }

    @Override
    public void readKey(String key, Context ctx, Collector<Tuple2<String, String>> out) throws Exception {
        String device = userLoginDeviceState.value();
        if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(device)) {
            out.collect(Tuple2.of(key, device));
        }
    }
}
