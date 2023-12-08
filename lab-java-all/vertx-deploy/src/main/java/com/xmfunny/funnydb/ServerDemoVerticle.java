package com.xmfunny.funnydb;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class ServerDemoVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.route(HttpMethod.POST, "/v1/test/:uuid")
                .handler(ctx -> {
                    Future<Buffer> future = ctx.request().body();
                    future.onComplete(result->{
                        String uuid = ctx.pathParam("uuid");
                        if (result.succeeded()){
                            String content = result.result().toString("utf-8");
                            ctx.response().end(uuid + "-" + content);
                        }else{
                            ctx.response().end(uuid);
                        }
                    });
                });

        server.requestHandler(router).listen(8081);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
