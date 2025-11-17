package com.kuzetech.bigdata.flink.timer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountWithTimestamp {
    private String key;
    private Long count;
    private Long lastModified;

    public void increaseCount() {
        this.count = this.count + 1;
    }
}
