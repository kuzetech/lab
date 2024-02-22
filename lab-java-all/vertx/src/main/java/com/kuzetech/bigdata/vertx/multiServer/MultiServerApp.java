package com.kuzetech.bigdata.vertx.multiServer;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import lombok.extern.slf4j.Slf4j;

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

        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(3);

        JsonObject config = new JsonObject();
        Metadata metadata = new Metadata();
        metadata.setName("aaa");
        config.put("metadata", metadata);
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
