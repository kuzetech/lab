package com.kuzetech.bigdata.flink.test;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WordWithCount {

    private String word;
    private int count;
}
