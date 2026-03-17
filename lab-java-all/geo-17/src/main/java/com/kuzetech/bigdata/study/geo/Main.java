package com.kuzetech.bigdata.study.geo;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

import java.io.File;
import java.net.InetAddress;

public class Main {
    public static void main(String[] args) throws Exception {
        InetAddress ipAddress = InetAddress.getByName("111.27.86.14");

        File file11 = new File("/Users/huangsw/Downloads/controller11/maxmind-geoip2-city.mmdb");
        DatabaseReader reader11 = new DatabaseReader.Builder(file11).build();

        CityResponse response11 = reader11.city(ipAddress);
        System.out.println(response11.city());
    }
}