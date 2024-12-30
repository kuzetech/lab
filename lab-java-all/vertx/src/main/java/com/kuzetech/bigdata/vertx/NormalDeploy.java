package com.kuzetech.bigdata.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NormalDeploy {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(c -> {
            c.response().setChunked(true);
            c.next();
        });

        router.route("/health").handler(c -> {
            c.response().write("health");
            c.next();
        });

        router.route("/ingest/*").handler(c -> {
            c.response().write("ingest");
            c.next();
        });

        router.route("/ingest/:project").handler(c -> {
            String project = c.pathParam("project");
            c.response().write(project);
            c.next();
        });

        router.route().handler(RoutingContext::end);


        server.requestHandler(router).listen(8080, "0.0.0.0").onComplete(res -> {
            if (res.succeeded()) {
                log.info("Server is now listening!");
            } else {
                log.info("Failed to bind!");
            }
        });
    }
}
