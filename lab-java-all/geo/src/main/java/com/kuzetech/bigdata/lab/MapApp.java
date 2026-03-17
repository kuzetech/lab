package com.kuzetech.bigdata.lab;

import com.maxmind.db.Reader;

import java.io.File;
import java.net.InetAddress;

public class MapApp {
    public static void main(String[] args) throws Exception {
        InetAddress ipAddress = InetAddress.getByName("111.27.86.14");

        File file11 = new File("/Users/huangsw/Downloads/controller11/maxmind-geoip2-city.mmdb");
        Reader reader11 = new Reader(file11);

        TestResponse res = reader11.get(ipAddress, TestResponse.class);
        System.out.println(res);
    }
}
