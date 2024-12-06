package com.kuzetech.bigdata.json.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Student {
    private int id;
    private String name;
    @JsonProperty(required = true)
    private Double money;
}
