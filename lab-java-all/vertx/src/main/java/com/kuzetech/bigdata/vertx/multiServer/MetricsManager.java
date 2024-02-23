package com.kuzetech.bigdata.vertx.multiServer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.vertx.core.shareddata.Shareable;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class MetricsManager implements Shareable {

    public static final String UNKNOWN_APP = "unknown";
    public static final String UNKNOWN_ACCESS_ID = "unknown";

    private MeterRegistry registry;
    private Timer metricsCPUDurationTime;

    public MetricsManager(MeterRegistry registry) {
        this.registry = registry;

        metricsCPUDurationTime = Timer
                .builder("vertx_http_server_request_cpu_time")
                .description("request cpu duration time")
                .publishPercentiles(0.5, 0.75, 0.95, 0.99)
                .publishPercentileHistogram()
                .serviceLevelObjectives(Duration.ofMillis(100)) // 期望区间间隔
                .minimumExpectedValue(Duration.ofMillis(10)) // 开始区间 10ms
                .maximumExpectedValue(Duration.ofSeconds(10)) // 结束区间 10s
                .register(registry);
    }

    public void recordCPUTime(long beginTime) {
        long durationTime = System.currentTimeMillis() - beginTime;
        this.metricsCPUDurationTime.record(durationTime, TimeUnit.MILLISECONDS);
    }

    public void recordSignatureErrorRequest() {
        Counter.builder("vertx_http_server_request_signature_error_total")
                .description("signature error request total")
                .tags("app", UNKNOWN_APP)
                .tags("access_id", UNKNOWN_ACCESS_ID)
                .register(registry)
                .increment();
    }
}
