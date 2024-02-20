package com.kuzetech.bigdata.vertx.testMetrics;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.PrometheusScrapingHandler;
import io.vertx.micrometer.VertxPrometheusOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main(String[] args) {

        VertxOptions vertxOptions = new VertxOptions();
        VertxPrometheusOptions vertxPrometheusOptions = new VertxPrometheusOptions();
        vertxPrometheusOptions.setPublishQuantiles(true);
        vertxPrometheusOptions.setEnabled(true);

        MicrometerMetricsOptions micrometerMetricsOptions = new MicrometerMetricsOptions();
        micrometerMetricsOptions.setPrometheusOptions(vertxPrometheusOptions);
        micrometerMetricsOptions.setEnabled(true);

        vertxOptions.setMetricsOptions(micrometerMetricsOptions);
        Vertx vertx = Vertx.vertx(vertxOptions);

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.get("/metrics").handler(PrometheusScrapingHandler.create());

        router.get("/v1/s").handler(ctx ->
                ctx.vertx().setTimer(5000, id -> ctx.json("success"))
        );
        router.get("/v1/f").handler(ctx -> ctx.fail(HttpResponseStatus.PRECONDITION_FAILED.code()));

        router.get("/v1/*").failureHandler(failureRoutingContext -> {
            HttpServerResponse response = failureRoutingContext.response();
            response.setStatusCode(failureRoutingContext.statusCode());
            response.end(new JsonObject().put("error", "system error, contact manager").encode());
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
