package com.kuzetech.bigdata.study.funnel;

import java.util.Objects;

public class EventsTimestamp {
    private Integer time;
    private Integer sourceIndex;

    public EventsTimestamp(Integer time, Integer sourceIndex) {
        this.time = time;
        this.sourceIndex = sourceIndex;
    }

    public Integer getTime() {
        return time;
    }

    public Integer getSourceIndex() {
        return sourceIndex;
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
