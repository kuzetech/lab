package com.kuzetech.bigdata.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class NormalDeploy {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.get("/test")
                .handler(c -> {
                    log.info("1");
                    c.next();
                    log.info("3");

                }).handler(c -> {
                    log.info("2");
                    c.end();
                })
        ;

        router.get("/ffff")
                .handler(c -> {
                    log.info("1");
                    if (new Random().nextBoolean()) {
                        c.fail(400, new Exception("test"));
                        return;
                    }
                    c.next();
                    log.info("不触发");

                }).handler(c -> {
                    log.info("不触发");
                    c.end();
                }).failureHandler(c -> {
                    log.error("2");
                    c.next();
                    log.error("4");
                }).failureHandler(c -> {
                    log.error("3");
                    c.end();
                })
        ;


        server.requestHandler(router).listen(8080, "0.0.0.0").onComplete(res -> {
            if (res.succeeded()) {
                log.info("Server is now listening!");
            } else {
                log.info("Failed to bind!");
            }
        });
    }
}
