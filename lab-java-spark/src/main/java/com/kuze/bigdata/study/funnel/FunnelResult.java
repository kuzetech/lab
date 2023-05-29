package com.kuze.bigdata.study.funnel;

import java.io.Serializable;

public class FunnelResult implements Serializable {
    private Long stepUserCount;
    private Double percent;

    public FunnelResult() {
    }

    public FunnelResult(Long stepUserCount, Double percent) {
        this.stepUserCount = stepUserCount;
        this.percent = percent;
    }

    public Long getStepUserCount() {
        return stepUserCount;
    }

    public void setStepUserCount(Long stepUserCount) {
        this.stepUserCount = stepUserCount;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }
}

