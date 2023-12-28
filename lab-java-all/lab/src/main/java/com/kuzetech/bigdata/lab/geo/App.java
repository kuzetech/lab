package com.kuzetech.bigdata.lab.geo;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.IspResponse;

import java.io.IOException;
import java.net.InetAddress;

public class App {
    public static void main(String[] args) throws IOException, GeoIp2Exception {

        DatabaseReader ispReader = new DatabaseReader.Builder(App.class.getResourceAsStream("/maxmind-geoip2-isp.mmdb")).build();

        //InetAddress ipAddress = InetAddress.getByName("103.98.240.233");
        InetAddress ipAddress = InetAddress.getByName("134.68.246.32");
        IspResponse response = ispReader.isp(ipAddress);

        ispReader.close();
    }
}
