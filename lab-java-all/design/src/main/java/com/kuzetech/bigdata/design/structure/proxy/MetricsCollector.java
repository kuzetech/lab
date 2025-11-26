package com.kuzetech.bigdata.design.structure.proxy;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class MetricsCollector {

    private static final String LOG_TEMPLATE = "spend {} ms";

    public void record(String apiName, Duration spendTime) {
        log.info(LOG_TEMPLATE, spendTime.toMillis());
    }

}
