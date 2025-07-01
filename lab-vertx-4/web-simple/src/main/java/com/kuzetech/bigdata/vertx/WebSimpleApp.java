package com.kuzetech.bigdata.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class WebSimpleApp
{
    public static void main( String[] args )
    {
        log.info("Hello World!");
        Vertx vertx = Vertx.vertx();

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.get("/header/show").handler(c -> {
            log.info(c.request().headers().toString());
            c.end();
        });

        router.get("/parameter/show").handler(c -> {
            Map<String, String> parasMap = c.queryParams().entries().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            log.info(parasMap.toString());
            c.end();
        });

        server.requestHandler(router).listen(8888, "0.0.0.0").onComplete(res -> {
            if (res.succeeded()) {
                log.info("Server is now listening!");
            } else {
                log.info("Failed to bind!");
            }
        });
    }
}
