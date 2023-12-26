package org.example;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultiServerApp {
    public static void main(String[] args) {
        VertxOptions options = new VertxOptions();
        Vertx vertx = Vertx.vertx(options);

        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(3);
        Future<String> deployed = vertx.deployVerticle(ServerVerticle.class, deploymentOptions);
        deployed.onComplete(r -> {
            if (r.succeeded()) {
                log.info("deploy success");
            } else {
                log.error("deploy fail, {}", r.cause().toString());
            }
        });


    }
}
