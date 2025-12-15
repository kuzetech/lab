package com.kuzetech.bigdata.flink.track.domain;

import lombok.Data;

@Data
public class DeviceOperatorKeyedState {
    public String key;
    public String deviceId;
}
