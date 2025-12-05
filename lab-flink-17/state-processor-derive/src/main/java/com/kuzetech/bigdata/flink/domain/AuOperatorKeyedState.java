package com.kuzetech.bigdata.flink.domain;

import com.xmfunny.funnydb.flink.model.ActiveMark;
import lombok.Data;

@Data
public class AuOperatorKeyedState {
    public String key;
    public ActiveMark activeMark;
}
