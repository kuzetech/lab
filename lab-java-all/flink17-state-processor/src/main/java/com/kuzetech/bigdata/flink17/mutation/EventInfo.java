package com.kuzetech.bigdata.flink17.mutation;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EventInfo implements Serializable {
    private String app;
    private String lifeCycle;
    private String identifyType;
    private String identify;
    private Integer fieldCount;
    private String key;
    private String sourceKey;
}
