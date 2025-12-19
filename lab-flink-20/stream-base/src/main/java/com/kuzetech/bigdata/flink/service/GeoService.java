package com.kuzetech.bigdata.flink.service;

import com.maxmind.db.CHMCache;
import com.maxmind.db.Reader;
import com.maxmind.geoip2.DatabaseReader;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class GeoService {

    private static DatabaseReader reader;

    public static synchronized DatabaseReader getDatabaseReader() throws IOException {
        if (reader == null) {
            // 1. 从分布式缓存获取文件
            // File dbFile = getRuntimeContext().getDistributedCache().getFile("geoip-db");
            File geoFile = GeoService.getResourceGeoFile("/maxmind-geoip2-city.mmdb");
            // 使用内存映射模式 (Memory Mapped)，性能高且内存共享好
            reader = new DatabaseReader.Builder(geoFile)
                    .fileMode(Reader.FileMode.MEMORY_MAPPED)
                    .withCache(new CHMCache())
                    .build();
            log.info("初始化 Geo DatabaseReader 成功");
        }
        return reader;
    }

    public static synchronized void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    private static File getResourceGeoFile(String fileName) {
        File tempFile;
        try {
            tempFile = File.createTempFile("geoip-", ".mmdb");
            tempFile.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (
                InputStream in = GeoService.class.getResourceAsStream(fileName);
                OutputStream out = new FileOutputStream(tempFile)) {
            if (in == null) {
                throw new RuntimeException("geoip db file does no exist, name is " + fileName);
            }
            in.transferTo(out);

            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
