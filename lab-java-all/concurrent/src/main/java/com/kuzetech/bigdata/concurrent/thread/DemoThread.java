package com.kuzetech.bigdata.concurrent.thread;

import java.util.concurrent.*;

public class DemoThread {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
            }
        });
        thread.start();

        System.out.println("thread start");
        thread.join();
        System.out.println("thread end");


        new Thread(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return null;
            }
        }.toString());
    }
}
