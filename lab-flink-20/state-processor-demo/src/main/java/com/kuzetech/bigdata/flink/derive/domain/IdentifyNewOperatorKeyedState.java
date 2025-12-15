package com.kuzetech.bigdata.flink.derive.domain;

import lombok.Data;

@Data
public class IdentifyNewOperatorKeyedState {
    public String key;
    public Long createdTs;
}
