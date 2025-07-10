package com.kuzetech.bigdata.flink.domain;

import lombok.Data;

@Data
public class DeviceOperatorKeyedState {
    public String key;
    public String deviceId;
}
