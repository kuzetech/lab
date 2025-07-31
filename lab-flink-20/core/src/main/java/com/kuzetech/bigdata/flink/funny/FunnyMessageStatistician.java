package com.kuzetech.bigdata.flink.funny;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FunnyMessageStatistician implements Serializable {
    private String app;
    private String event;
    private Long result = 0L;

    public String getKey() {
        return this.app + "@" + this.event;
    }

    public void incrResult() {
        this.result++;
    }

    public void decrResult() {
        this.result--;
    }
}
