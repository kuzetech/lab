package com.kuzetech.bigdata.vertx;

import com.kuzetech.bigdata.vertx.verticles.HelloWorldHttpServerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class VirtualThreadDeploy {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(2);

        Future<String> d = vertx.deployVerticle(HelloWorldHttpServerVerticle.class, deploymentOptions);
        d.onComplete(r -> {
            if (r.succeeded()) {
                log.info("deploy success");
            } else {
                log.error("deploy fail, {}", r.cause().toString());
                vertx.close().onComplete(vr -> {
                    throw new RuntimeException(r.cause());
                });
            }
        });


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down server...");
            // vertx 关闭了之后无法使用 log 打印日志
            try {
                vertx.close().toCompletionStage().toCompletableFuture().get(30, TimeUnit.SECONDS);
                System.out.println("vertx closed successfully");
            } catch (Exception e) {
                System.out.println("Error while closing vertx: " + e.getCause());
            }

            // 可以继续关闭其他服务
            System.out.println("other closed successfully");
        }));
    }
}
