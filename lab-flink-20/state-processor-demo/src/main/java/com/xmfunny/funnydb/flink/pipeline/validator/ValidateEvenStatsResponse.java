package com.xmfunny.funnydb.flink.pipeline.validator;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class ValidateEvenStatsResponse implements Serializable {

    private Long sinceTime;
    private Long untilTime;
    private Map<String, StatsEvent> statsEventMap = new HashMap<>();

    public void setSinceTime(Long sinceTime) {
        this.sinceTime = sinceTime;
    }

    public void setUntilTime(Long untilTime) {
        this.untilTime = untilTime;
    }

    public void setStatsEventMap(Map<String, StatsEvent> statsEventMap) {
        this.statsEventMap = statsEventMap;
    }


}
