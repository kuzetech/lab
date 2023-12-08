package com.xmfunny.funnydb;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.kafka.client.producer.RecordMetadata;

public class ServerProducerVerticle extends AbstractVerticle {

    private final KafkaProducer<String, String> producer;

    public ServerProducerVerticle(KafkaProducer<String, String> producer) {
        this.producer = producer;
    }

    @Override
    public void start() throws Exception {
        HttpServer server = vertx.createHttpServer();

        server.requestHandler(request -> {
            KafkaProducerRecord<String, String> record = KafkaProducerRecord.create("test", "Hello World!");
            final Future<RecordMetadata> fut = producer.send(record);

            fut.onComplete(result->{
                HttpServerResponse response = request.response();
                response.putHeader("content-type", "text/plain");
               if (result.succeeded()){
                   response.end("success!");
               }else{
                   response.end("error!");
               }
            });

        });

        server.listen(8080);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
