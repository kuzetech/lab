package com.kuzetech.bigdata.lab;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

import java.io.InputStream;
import java.net.InetAddress;

public class ReaderApp {
    public static void main(String[] args) throws Exception {
        InetAddress ipAddress = InetAddress.getByName("128.101.101.101");
        InputStream inputStream = ReaderApp.class.getResourceAsStream("/maxmind-geoip2-city.mmdb");
        assert inputStream != null;

        DatabaseReader reader = new DatabaseReader.Builder(inputStream).build();
        CityResponse response = reader.city(ipAddress);
        System.out.println(response);

        reader.close();
    }
}
