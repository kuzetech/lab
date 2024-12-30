package com.kuzetech.bigdata.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NormalDeploy {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route("/health").handler(c -> {
            c.fail(400);
        });

        router.route("/ingest/*").failureHandler(c -> {
            c.response().setChunked(true);
            c.response().write("1");
            c.next();
        }).failureHandler(c -> {
            c.response().write("2");
            c.end();
        });

        router.route("/ingest/:project").handler(c -> {
            c.fail(401);
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
