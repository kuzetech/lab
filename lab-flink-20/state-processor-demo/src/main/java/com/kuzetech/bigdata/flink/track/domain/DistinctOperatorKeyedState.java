package com.kuzetech.bigdata.flink.track.domain;

import lombok.Data;

@Data
public class DistinctOperatorKeyedState {
    public String key;
    public Boolean exist;
}
