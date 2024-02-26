package com.kuzetech.bigdata.vertx.multiServer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.kafka.KafkaClientMetrics;
import io.micrometer.core.instrument.config.MeterFilter;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import io.vertx.micrometer.backends.BackendRegistries;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MultiServerApp {
    public static void main(String[] args) {
        VertxOptions vertxOptions = new VertxOptions();
        VertxPrometheusOptions vertxPrometheusOptions = new VertxPrometheusOptions();
        vertxPrometheusOptions.setPublishQuantiles(true);
        vertxPrometheusOptions.setEnabled(true);

        MicrometerMetricsOptions micrometerMetricsOptions = new MicrometerMetricsOptions();
        micrometerMetricsOptions.setPrometheusOptions(vertxPrometheusOptions);
        micrometerMetricsOptions.setEnabled(true);

        vertxOptions.setMetricsOptions(micrometerMetricsOptions);
        Vertx vertx = Vertx.vertx(vertxOptions);

        Map<String, String> producerConfig = new HashMap<>();
        producerConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerConfig.put("compression.type", "lz4");
        producerConfig.put("bootstrap.servers", "localhost:9092");
        KafkaProducer<String, String> producer = KafkaProducer.createShared(vertx, "producer", producerConfig);

        MeterRegistry registry = BackendRegistries.getDefaultNow();
        registry.config().meterFilter(MeterFilter.accept());
        new KafkaClientMetrics(producer.unwrap()).bindTo(registry);
        MetricsManager metricsManager = new MetricsManager(registry);

        Metadata metadata = new Metadata();
        metadata.setName("aaa");

        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(3);
        JsonObject config = new JsonObject();
        config.put("metadata", metadata);
        config.put("metricsManager", metricsManager);
        config.put("kafkaConfig", new KafkaConfig(producerConfig));
        deploymentOptions.setConfig(config);

        Future<String> serverDeployed = vertx.deployVerticle(ServerVerticle.class, deploymentOptions);
        serverDeployed.onComplete(r -> {
            if (r.succeeded()) {
                log.info("deploy server success");
            } else {
                log.error("deploy server fail, {}", r.cause().toString());
            }
        });

        Future<String> metadataDeployed = vertx.deployVerticle(MetadataVerticle.class, new DeploymentOptions().setInstances(1));
        metadataDeployed.onComplete(r -> {
            if (r.succeeded()) {
                log.info("deploy metadata success");
            } else {
                log.error("deploy metadata fail, {}", r.cause().toString());
            }
        });


    }
}
