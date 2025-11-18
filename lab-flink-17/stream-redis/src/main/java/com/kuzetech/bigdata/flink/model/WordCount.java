package com.kuzetech.bigdata.flink.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WordCount {

    private String word;
    private Integer count;

}
