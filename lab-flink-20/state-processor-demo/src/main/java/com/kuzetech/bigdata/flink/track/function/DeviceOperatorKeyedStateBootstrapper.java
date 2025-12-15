package com.kuzetech.bigdata.flink.track.function;

import com.kuzetech.bigdata.flink.track.domain.DeviceOperatorKeyedState;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;

public class DeviceOperatorKeyedStateBootstrapper extends KeyedStateBootstrapFunction<String, DeviceOperatorKeyedState> {

    ValueState<String> userLoginDeviceState;

    @Override
    public void open(Configuration configuration) throws Exception {
        ValueStateDescriptor<String> userDeviceStateDesc = new ValueStateDescriptor<>("user-login-device-state", Types.STRING);
        userDeviceStateDesc.enableTimeToLive(StateTtlConfig.DISABLED);
        this.userLoginDeviceState = getRuntimeContext().getState(userDeviceStateDesc);
    }

    @Override
    public void processElement(
            DeviceOperatorKeyedState value,
            KeyedStateBootstrapFunction<String, DeviceOperatorKeyedState>.Context context) throws Exception {
        userLoginDeviceState.update(value.deviceId);
    }
}
