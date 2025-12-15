package com.kuzetech.bigdata.flink.track.function;

import com.kuzetech.bigdata.flink.domain.EnrichOperatorKeyedState;
import com.xmfunny.funnydb.flink.model.DeviceInfoCacheData;
import com.xmfunny.funnydb.flink.model.RecordHeaders;
import com.xmfunny.funnydb.flink.model.TrackEvent;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EnrichOperatorKeyedStateReaderFunction extends KeyedStateReaderFunction<String, EnrichOperatorKeyedState> {

    ValueState<DeviceInfoCacheData> deviceInfoLastState;
    ListState<Tuple2<RecordHeaders, TrackEvent>> pendingState;

    @Override
    public void open(Configuration configuration) throws Exception {
        StateTtlConfig ttlConfig = StateTtlConfig.newBuilder(Time.days(100))
                .setUpdateType(StateTtlConfig.UpdateType.OnReadAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                .build();
        ValueStateDescriptor<DeviceInfoCacheData> deviceInfoLastStateDesc = new ValueStateDescriptor<>("device-info-last-state", Types.POJO(DeviceInfoCacheData.class));
        deviceInfoLastStateDesc.enableTimeToLive(ttlConfig);
        this.deviceInfoLastState = getRuntimeContext().getState(deviceInfoLastStateDesc);
        ListStateDescriptor<Tuple2<RecordHeaders, TrackEvent>> pendingStateDesc =
                new ListStateDescriptor<>("pending-state", TypeInformation.of(new TypeHint<Tuple2<RecordHeaders, TrackEvent>>() {
                }));
        pendingState = getRuntimeContext().getListState(pendingStateDesc);
    }

    @Override
    public void readKey(String key, Context context, Collector<EnrichOperatorKeyedState> out) throws Exception {
        EnrichOperatorKeyedState data = new EnrichOperatorKeyedState();
        data.key = key;
        data.deviceInfo = deviceInfoLastState.value();
        data.pendingState = StreamSupport
                .stream(pendingState.get().spliterator(), false)
                .collect(Collectors.toList());
        out.collect(data);
    }
}
