package org.example;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 */

@Slf4j
public class AllInOneApp {

    public static void main(String[] args) {

        VertxOptions options = new VertxOptions();
        Vertx vertx = Vertx.vertx(options);

        Map<String, String> producerConfig = new HashMap<>();
        producerConfig.put("bootstrap.servers", "localhost:9092");
        producerConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerConfig.put("acks", "1");

        KafkaProducer<String, String> producer = KafkaProducer.create(vertx, producerConfig);

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.post("/test")
                .handler(ctx -> {
                    HttpServerRequest request = ctx.request();
                    request.body().onComplete(r -> {
                        KafkaProducerRecord<String, String> record = KafkaProducerRecord.create("test2", r.result().toString());
                        producer.send(record).onComplete(result -> {
                            boolean succeeded = result.succeeded();
                            if (succeeded) {
                                ctx.json("success");
                            } else {
                                ctx.json("fail");
                            }
                        });
                    });
                });

        server.requestHandler(router).listen(8080, "0.0.0.0").onComplete(res -> {
            if (res.succeeded()) {
                log.info("Server is now listening!");
            } else {
                log.info("Failed to bind!");
            }
        });

    }

}
