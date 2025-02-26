package com.kuzetech.bigdata.concurrent.completablefuture;

import java.util.concurrent.CompletableFuture;

public class ExceptionallyDemo {
    public static void main(String[] args) {
        CompletableFuture<Integer> f0 = CompletableFuture
                .supplyAsync(() -> (7 / 0))
                .thenApply(r -> r * 10)
                .exceptionally(e -> 1);

        // 结果返回 1
        System.out.println(f0.join());
    }
}
