package com.kuzetech.bigdata.flink.domain;

import lombok.Data;

@Data
public class IdentifyNewOperatorKeyedState {
    public String key;
    public Long createdTs;
}
