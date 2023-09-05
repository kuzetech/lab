package org.example;

import com.google.common.net.InetAddresses;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.AllowForwardHeaders;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.kafka.client.producer.RecordMetadata;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 */

@Slf4j
public class App {

    public static void main(String[] args) {
        MyConfig config = new MyConfig("test123");

        VertxOptions options = new VertxOptions().setWorkerPoolSize(40);

        Vertx vertx = Vertx.vertx(options);

        // setTimer setPeriodic 有什么区别
        long timerId = vertx.setPeriodic(15000, id -> {
            log.info("change output topic --- test22222");
            config.setOutputTopic("test22222");
        });

        // boolean b = vertx.cancelTimer(timerId);

        Map<String, String> producerConfig = new HashMap<>();
        producerConfig.put("bootstrap.servers", "localhost:9092");
        producerConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerConfig.put("acks", "1");

        KafkaProducer<String, String> producer = KafkaProducer.create(vertx, producerConfig);

        HttpServer server = vertx.createHttpServer();

        // 服务器级别 handler
        // 设置 server.requestHandler(router) 后无效
        server.requestHandler(httpServerRequest -> {
            log.info("httpServerRequest");
        });

        Router router = Router.router(vertx);

        BodyHandler bodyHandler = BodyHandler.create();
        bodyHandler.setBodyLimit(10 * 1024 * 1024);

        LoggerHandler loggerHandler = LoggerHandler.create();

        // 可以设置 router 级别的处理器
        router.allowForward(AllowForwardHeaders.X_FORWARD).route()
                .handler(loggerHandler)
                .handler(bodyHandler)
                .handler(routingContext -> {
                    log.info("route level handler1");
                    routingContext.next();
                }).handler(routingContext -> {
                    log.info("route level handler2");
                    routingContext.next();
                });


        Route handler = router.post("/v1/collect")
                // Content-Type
                .consumes("application/json")
                // Accept
                .produces("application/json")
                .handler(routingContext -> {

                    String clientIP = getClientIP(routingContext.request());
                    log.info(clientIP);

                    JsonObject jsonObject = routingContext.body().asJsonObject();
                    RequestContent content = jsonObject.mapTo(RequestContent.class);

                    KafkaProducerRecord<String, String> record = KafkaProducerRecord.create(config.getOutputTopic(), "key", content.getContent());
                    producer.send(record).onComplete(result -> {
                        boolean succeeded = result.succeeded();
                        if (succeeded) {
                            RecordMetadata recordMetadata = result.result();
                            log.info(
                                    "Message " + record.value() + " written on topic=" + recordMetadata.getTopic() +
                                            ", partition=" + recordMetadata.getPartition() +
                                            ", offset=" + recordMetadata.getOffset()
                            );
                            routingContext.json(new MyResponse("success", 200, ""));
                        } else {
                            log.error(result.cause().getMessage());
                            routingContext.json(new MyResponse("fail", 500, result.cause().getMessage()));
                            // throw new Exception
                            //routingContext.fail(403);
                        }

                    });
                });

        handler.failureHandler(failureRoutingContext -> {
            int statusCode = failureRoutingContext.statusCode();
            // Status code will be 500 for the RuntimeException
            // or 403 for the other failure
            failureRoutingContext.json(new MyResponse("fail", statusCode, "error"));

        });

        server.requestHandler(router).listen(8080, "0.0.0.0").onComplete(res -> {
            if (res.succeeded()) {
                log.info("Server is now listening!");
            } else {
                log.info("Failed to bind!");
            }
        });

    }

    public static String getClientIP(HttpServerRequest request) {
        String clientIP = "";

        String forwardedAddress = request.getHeader("X-Forwarded-For");
        if (forwardedAddress != null) {
            String[] ips = forwardedAddress.trim().split(",");
            if (ips.length > 0 && !ips[0].isEmpty()) {
                clientIP = ips[0];
            }
        }

        if (clientIP.isEmpty()) {
            clientIP = request.remoteAddress().host();
        }

        boolean isValidAddress = InetAddresses.isInetAddress(clientIP);

        if (isValidAddress) {
            return clientIP;
        } else {
            return "";
        }
    }

}
