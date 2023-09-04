package org.example;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

/**
 * Hello world!
 *
 */

@Slf4j
public class App {

    public static void main( String[] args )
    {
        VertxOptions options = new VertxOptions().setWorkerPoolSize(40);

        Vertx vertx = Vertx.vertx(options);

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        Route handler = router.post("/v1/collect").handler(ctx -> {
            log.info("post!");
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
