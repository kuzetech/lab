package com.kuzetech.bigdata.flinkstate.tracktopipeline;

import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;

public class TrackUserLoginDeviceStateBootstrapper extends KeyedStateBootstrapFunction<String, Tuple2<String, String>> {

    private ValueState<String> userLoginDeviceState;

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<String> userDeviceStateDesc = new ValueStateDescriptor<>("user-login-device-state", Types.STRING);
        userDeviceStateDesc.enableTimeToLive(StateTtlConfig.DISABLED);
        this.userLoginDeviceState = getRuntimeContext().getState(userDeviceStateDesc);
    }

    @Override
    public void processElement(Tuple2<String, String> value, Context ctx) throws Exception {
        userLoginDeviceState.update(value.f1);
    }
}