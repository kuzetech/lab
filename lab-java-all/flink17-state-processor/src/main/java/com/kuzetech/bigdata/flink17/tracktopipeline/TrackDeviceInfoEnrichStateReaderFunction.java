package com.kuzetech.bigdata.flink17.tracktopipeline;

import com.xmfunny.funnydb.flink.model.DeviceInfoCacheData;
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
public class TrackDeviceInfoEnrichStateReaderFunction extends KeyedStateReaderFunction<String, Tuple2<String, DeviceInfoCacheData>> {

    private ValueState<DeviceInfoCacheData> deviceInfoState;

    @Override
    public void open(Configuration parameters) throws Exception {
        ValueStateDescriptor<DeviceInfoCacheData> deviceInfoStateDesc = new ValueStateDescriptor<>("device-info-state", Types.POJO(DeviceInfoCacheData.class));
        deviceInfoStateDesc.enableTimeToLive(StateTtlConfig.DISABLED);
        this.deviceInfoState = getRuntimeContext().getState(deviceInfoStateDesc);
    }

    @Override
    public void readKey(String key, Context ctx, Collector<Tuple2<String, DeviceInfoCacheData>> out) throws Exception {
        DeviceInfoCacheData data = deviceInfoState.value();
        if (StringUtils.isNotEmpty(key) && data != null) {
            out.collect(Tuple2.of(key, data));
        }
    }
}
