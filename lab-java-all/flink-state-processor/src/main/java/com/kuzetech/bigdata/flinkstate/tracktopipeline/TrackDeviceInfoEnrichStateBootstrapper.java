package com.kuzetech.bigdata.flinkstate.tracktopipeline;

import com.xmfunny.funnydb.flink.model.DeviceInfoCacheData;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;

public class TrackDeviceInfoEnrichStateBootstrapper extends KeyedStateBootstrapFunction<String, Tuple2<String, DeviceInfoCacheData>> {

    private ValueState<DeviceInfoCacheData> deviceInfoState;

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<DeviceInfoCacheData> deviceInfoStateDesc = new ValueStateDescriptor<>("device-info-state", Types.POJO(DeviceInfoCacheData.class));
        deviceInfoStateDesc.enableTimeToLive(StateTtlConfig.DISABLED);
        this.deviceInfoState = getRuntimeContext().getState(deviceInfoStateDesc);
    }

    @Override
    public void processElement(Tuple2<String, DeviceInfoCacheData> value, Context ctx) throws Exception {
        deviceInfoState.update(value.f1);
    }
}