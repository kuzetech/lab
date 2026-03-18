package com.kuzetech.bigdata.lab;

import com.maxmind.db.Reader;

import java.io.File;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReaderApp {
    public static void main(String[] args) throws Exception {
        InetAddress ipAddress = InetAddress.getByName("111.27.86.14");

        File file = new File("/Users/huangsw/Downloads/controller11/maxmind-geoip2-city.mmdb");
        Reader reader = new Reader(file);

        Map map = reader.get(ipAddress, Map.class);
        System.out.println(map);

        String cityName = Optional.ofNullable(map)
                .map(m -> (Map<?, ?>) m.get("city"))
                .map(c -> (Map<?, ?>) c.get("names"))
                .map(n -> (String) n.get("en"))
                .orElse("Unknown"); // 如果中间任何一层为 null，则返回默认值
        System.out.println(cityName);

        String provinceName = Optional.ofNullable(map)
                .map(m -> (List<?>) m.get("subdivisions"))
                .map(c -> (Map<?, ?>) c.get(0))
                .map(c -> (Map<?, ?>) c.get("names"))
                .map(n -> (String) n.get("en"))
                .orElse("Unknown");
        System.out.println(provinceName);
    }
}
