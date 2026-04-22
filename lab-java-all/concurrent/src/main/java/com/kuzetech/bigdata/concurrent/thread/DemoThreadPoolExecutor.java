package com.kuzetech.bigdata.concurrent.thread;

import java.util.concurrent.*;

public class DemoThreadPoolExecutor {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                3,
                10,
                1,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(20),
                new ThreadPoolExecutor.AbortPolicy()
        );

        Future<?> result = threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
            }
        });
        result.get();

        threadPoolExecutor.shutdown();
    }
}
