package com.kuze.bigdata.rengine.benchmark.complex;

import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@Getter
@Setter
public class Event {

    public static String generateRandomIPAddress() {
        Random random = new Random();
        StringBuilder ipAddress = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            ipAddress.append(random.nextInt(256));
            if (i < 3) {
                ipAddress.append(".");
            }
        }
        return ipAddress.toString();
    }

    private String id;
    private String name;
    private String ip;
    private long time;
}
