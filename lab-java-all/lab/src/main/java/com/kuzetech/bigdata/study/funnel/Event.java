package com.kuzetech.bigdata.study.funnel;

public class Event {
    private Integer time;
    private Integer step;

    public Event(Integer time, Integer step) {
        this.time = time;
        this.step = step;
    }

    public Integer getTime() {
        return time;
    }

    public Integer getStep() {
        return step;
    }
}

