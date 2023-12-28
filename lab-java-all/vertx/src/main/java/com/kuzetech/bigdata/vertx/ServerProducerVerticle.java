package com.kuzetech.bigdata.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;

public class ServerProducerVerticle extends AbstractVerticle {

    private final KafkaProducer<String, String> producer;

    public ServerProducerVerticle(KafkaProducer<String, String> producer) {
        this.producer = producer;
    }

    @Override
    public void start() throws Exception {
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

        server.requestHandler(router).listen(8080);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
