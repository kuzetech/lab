package com.kuzetech.bigdata.flink.track.domain;

import com.xmfunny.funnydb.flink.model.DeviceInfoCacheData;
import com.xmfunny.funnydb.flink.model.RecordHeaders;
import com.xmfunny.funnydb.flink.model.TrackEvent;
import lombok.Data;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.List;

@Data
public class EnrichOperatorKeyedState {
    public String key;
    public DeviceInfoCacheData deviceInfo;
    public List<Tuple2<RecordHeaders, TrackEvent>> pendingState;
}
