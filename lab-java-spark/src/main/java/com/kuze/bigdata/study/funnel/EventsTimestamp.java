package com.kuze.bigdata.study.funnel;

import java.io.Serializable;
import java.util.Objects;

public class EventsTimestamp implements Serializable {
    private Long time;
    private Integer sourceIndex;

    public EventsTimestamp() {
    }

    public EventsTimestamp(Long time, Integer sourceIndex) {
        this.time = time;
        this.sourceIndex = sourceIndex;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getSourceIndex() {
        return sourceIndex;
    }

    public void setSourceIndex(Integer sourceIndex) {
        this.sourceIndex = sourceIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventsTimestamp that = (EventsTimestamp) o;
        return Objects.equals(time, that.time) &&
                Objects.equals(sourceIndex, that.sourceIndex);
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
