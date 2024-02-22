package com.kuzetech.bigdata.vertx.multiServer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.kafka.KafkaClientMetrics;
import io.micrometer.core.instrument.config.MeterFilter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.AllowForwardHeaders;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.kafka.client.producer.RecordMetadata;
import io.vertx.micrometer.PrometheusScrapingHandler;
import io.vertx.micrometer.backends.BackendRegistries;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ServerVerticle extends AbstractVerticle {

    private HttpServer server;
    private Metadata metadata;

    @Override
    public void start(Promise<Void> startPromise) {
        metadata = (Metadata) config().getValue("metadata");
        log.info("初始化 server，metadata 为 " + metadata.getName());

        Map<String, String> producerConfig = new HashMap<>();
        producerConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerConfig.put("compression.type", "lz4");
        producerConfig.put("bootstrap.servers", "localhost:9092");
        KafkaProducer<String, String> producer = KafkaProducer.createShared(vertx, "producer", producerConfig);

        vertx.eventBus().consumer("news.uk.sport", message -> {
            Metadata m = (Metadata) message.body();
            log.info("I have received a message: " + m.getName());
            metadata = m;
        });

        MeterRegistry registry = BackendRegistries.getDefaultNow();
        registry.config().meterFilter(MeterFilter.accept());
        new KafkaClientMetrics(producer.unwrap()).bindTo(registry);

        server = vertx.createHttpServer();
        Router router = Router.router(vertx)
                // 允许代理访问记录源IP
                .allowForward(AllowForwardHeaders.X_FORWARD);

        router.route().handler(BodyHandler.create().setBodyLimit(10000));

        HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx);
        healthCheckHandler.register("health", promise -> promise.complete(Status.OK()));

        router.get("/_/health").handler(healthCheckHandler);
        router.get("/metrics").handler(PrometheusScrapingHandler.create());

        router.get("/test")
                .handler(ctx -> {
                    String message = " done at " + Thread.currentThread().getId() + " " + Thread.currentThread().getName();
                    KafkaProducerRecord<String, String> record = KafkaProducerRecord.create("topic", message);
                    final Future<RecordMetadata> fut = producer.send(record);
                    fut.onSuccess(event -> {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        ctx.end("success");
                    }).onFailure(event -> {
                        ctx.end("fail");
                    });
                });

        // Now bind the server:
        server.requestHandler(router).listen(8080, res -> {
            if (res.succeeded()) {
                startPromise.complete();
            } else {
                startPromise.fail(res.cause());
            }
        });
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        server.close().onComplete(res -> {
            if (res.succeeded()) {
                stopPromise.complete();
            } else {
                stopPromise.fail(res.cause());
            }
        });
    }
}
