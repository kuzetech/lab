package com.xmfunny.funnydb;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args ) {

        Vertx vertx = Vertx.vertx();

        KafkaProducerVerticle kafkaProducerVerticle = new KafkaProducerVerticle();
        Future<String> deployedResult = vertx.deployVerticle(kafkaProducerVerticle);

        deployedResult.onComplete(result -> {
            if (result.succeeded()){
                ServerVerticle serverVerticle = new ServerVerticle(kafkaProducerVerticle.getProducer());
                vertx.deployVerticle(serverVerticle);
            }else{
                throw new RuntimeException("deploy kafkaProducerVerticle error");
            }
        });

    }
}
