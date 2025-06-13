package com.kuzetech.bigdata.lab.hash;

import org.apache.commons.lang3.StringUtils;

public class App {
    public static void main(String[] args) {
        String deviceIdPre = "9f89c84a559f573636a47ff8daed0d33_";
        for (int i = 1; i <= 5; i++) {
            String deviceId = deviceIdPre + i;
            String result = StringUtils.join(new String[]{"test", "0", deviceId}, "#");
            System.out.println(result.hashCode() % 128);
        }

    }
}
