package com.kuzetech.bigdata.vertx.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class HelloWorldHttpServerVerticle extends AbstractVerticle {
    @Override
    public void start() {
        var server = vertx.createHttpServer();
        server.requestHandler(request -> {
            request.response().end("Hello World");
        });
        Future.await(server.listen(8080, "localhost"));
    }
}
