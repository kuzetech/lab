package com.kuzetech.bigdata.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerVerticle extends AbstractVerticle {

    private HttpServer server;

    @Override
    public void start(Promise<Void> startPromise) {
        server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.get("/test")
                .handler(ctx -> {
                    log.info("test");
                    ctx.json("success");
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
