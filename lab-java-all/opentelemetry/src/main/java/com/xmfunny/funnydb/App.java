package com.xmfunny.funnydb;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.MeterProvider;
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

        MeterProvider meterProvider = sdk.getMeterProvider();




        Meter meter = sdk.meterBuilder("instrumentation-library-name")
                .setInstrumentationVersion("1.0.0")
                .build();

        Attributes attributes = Attributes.of(AttributeKey.stringKey("Key"), "SomeWork");

        meter.gaugeBuilder("cpu_usage")
                .setDescription("CPU Usage")
                .setUnit("ms")
                .buildWithCallback(measurement -> {
                    measurement.record(1, attributes);
                });

    }
}
