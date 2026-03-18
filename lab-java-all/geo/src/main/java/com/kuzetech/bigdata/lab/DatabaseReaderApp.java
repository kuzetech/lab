package com.kuzetech.bigdata.lab;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

import java.io.File;
import java.net.InetAddress;

public class DatabaseReaderApp {
    public static void main(String[] args) throws Exception {
        InetAddress ipAddress = InetAddress.getByName("111.27.86.14");

        File file10 = new File("/Users/huangsw/Downloads/controller10/maxmind-geoip2-city.mmdb");
        DatabaseReader reader10 = new DatabaseReader.Builder(file10).build();

        CityResponse response10 = reader10.city(ipAddress);
        System.out.println(response10.getCity());

        File file11 = new File("/Users/huangsw/Downloads/controller11/maxmind-geoip2-city.mmdb");
        DatabaseReader reader11 = new DatabaseReader.Builder(file11).build();

        CityResponse response11 = reader11.city(ipAddress);
        System.out.println(response11.getCity());
    }
}
