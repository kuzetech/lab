package com.kuzetech.bigdata.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.LoggerHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleHttpServer {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route()
                .handler(LoggerHandler.create())
                .handler(c -> {
                    log.info(c.request().remoteAddress().hostAddress());
                    c.response().setChunked(true);
                    c.response().write("2222222");
                    c.vertx().setTimer(1000, timeId -> {
                        log.info(String.valueOf(timeId));
                        c.response().end("1111111");
                    });
                }).failureHandler(c -> {
                    log.error("出现异常", c.failure());
                    c.response().end("error");
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
