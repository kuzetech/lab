package com.xmfunny.funnydb;

import io.opentelemetry.exporter.logging.LoggingMetricExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args ) {

        SdkMeterProvider sdkMeterProvider = SdkMeterProvider.builder()
                .registerMetricReader(PeriodicMetricReader.builder(LoggingMetricExporter.create()).build())
                .build();

        OpenTelemetrySdk sdk = AutoConfiguredOpenTelemetrySdk.initialize()
                .getOpenTelemetrySdk();
    }
}
