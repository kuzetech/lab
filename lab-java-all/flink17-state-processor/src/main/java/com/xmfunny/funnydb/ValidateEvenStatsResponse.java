package com.xmfunny.funnydb;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class ValidateEvenStatsResponse implements Serializable {
    public static final String UNKNOWN_EVENT_NAME = "__unknown__";

    private Long sinceTime;
    private Long untilTime;
    private Map<String, StatsEvent> statsEventMap = new HashMap<>();
    
}
