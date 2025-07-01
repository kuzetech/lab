package com.kuzetech.bigdata.vertx;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OfficialTemplateApp {
    public static void main(String[] args) {
        log.info("Hello World!");
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle());
    }
}
