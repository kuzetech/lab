package org.example;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main(String[] args) {
        VertxOptions options = new VertxOptions();
        Vertx vertx = Vertx.vertx(options);

        KafkaProducerVerticle kafkaProducerVerticle = new KafkaProducerVerticle();
        Future<String> deployed = vertx.deployVerticle(kafkaProducerVerticle);
        deployed.onComplete(r -> {
            if (r.succeeded()) {
                log.info("deployed kafka producer success");
                Future<String> serverDeployed = vertx.deployVerticle(new ServerProducerVerticle(kafkaProducerVerticle.getProducer()));
                serverDeployed.onComplete(sr -> {
                    if (sr.succeeded()) {
                        log.info("deployed server success");
                    } else {
                        log.error("deployed server error", sr.cause());
                        vertx.close();
                    }
                });
            } else {
                log.error("deployed kafka producer error", r.cause());
                vertx.close();
            }
        });
    }
}
