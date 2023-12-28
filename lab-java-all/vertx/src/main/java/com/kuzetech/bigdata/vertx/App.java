package com.kuzetech.bigdata.vertx;

import io.vertx.core.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class App {
    public static void main(String[] args) {
        VertxOptions options = new VertxOptions();
        options.setEventLoopPoolSize(2);
        Vertx vertx = Vertx.vertx(options);

        List<Verticle> verticleList = new ArrayList<>();


        for (int i = 0; i < 5; i++) {
            Verticle verticle = new MyVertical(i);
            verticleList.add(verticle);
        }

        Future.all(verticleList.stream().map(vertx::deployVerticle).collect(Collectors.toList())).result();
    }

    private static class MyVertical extends AbstractVerticle implements Verticle {

        private final int i;

        public MyVertical(int i) {
            this.i = i;
        }


        @Override
        public void start() {
            getVertx().setTimer(0, l -> {
                System.out.println(this.i + " done at " + Thread.currentThread().getId() + " " + Thread.currentThread().getName());
            });
        }
    }
}
