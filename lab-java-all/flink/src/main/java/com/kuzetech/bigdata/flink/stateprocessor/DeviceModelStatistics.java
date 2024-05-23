package com.kuzetech.bigdata.flink.stateprocessor;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceModelStatistics implements Serializable {
    private String model;
    private Integer count;
}
