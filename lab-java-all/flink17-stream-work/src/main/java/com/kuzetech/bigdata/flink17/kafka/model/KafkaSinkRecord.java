package com.kuzetech.bigdata.flink17.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaSinkRecord implements Serializable {
    private byte[] key;
    private String value;
}
