package org.example;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
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
public class App {

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
                .handler(routingContext -> {

                    log.info("接收到请求");

                    String body = routingContext.body().asString("utf-8");
                    KafkaProducerRecord<String, String> record = KafkaProducerRecord.create("my_topic", body);

                    producer.send(record).onComplete(result -> {
                        boolean succeeded = result.succeeded();
                        if (succeeded) {
                            log.info("接收结束");
                            routingContext.json("success");
                        } else {
                            log.error(result.cause().getMessage());
                            routingContext.json("fail");
                        }

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
