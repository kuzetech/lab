package com.kuzetech.bigdata.study.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Many {
    public static void main(String[] args) throws JsonProcessingException, InterruptedException {
        Student a = new Student("a", 1, 2L, 1.1F, 1.2);

        ObjectMapper mapper = new ObjectMapper();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    try {
                        mapper.writeValueAsString(a);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);

        long begin = System.currentTimeMillis();
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
        long sub = System.currentTimeMillis() - begin;
        System.out.println(sub);
    }
}
