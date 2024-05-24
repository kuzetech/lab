package com.kuzetech.bigdata.flink.stateprocessor.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCountryStatistics implements Serializable {
    private String country;
    private Integer count;
}
