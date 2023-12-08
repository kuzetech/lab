package com.xmfunny.funnydb;

import io.vertx.core.Vertx;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args ) {

        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new ServerDemoVerticle());

//        KafkaProducerVerticle kafkaProducerVerticle = new KafkaProducerVerticle();
//        Future<String> deployedResult = vertx.deployVerticle(kafkaProducerVerticle);
//
//        deployedResult.onComplete(result -> {
//            if (result.succeeded()){
//                ServerProducerVerticle serverVerticle = new ServerProducerVerticle(kafkaProducerVerticle.getProducer());
//                vertx.deployVerticle(serverVerticle);
//            }else{
//                throw new RuntimeException("deploy kafkaProducerVerticle error");
//            }
//        });

    }
}
