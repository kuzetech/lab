package com.kuzetech.bigdata.springboot.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaInfo {
    private String topic;
    private int partition;
    private long offset;
}
