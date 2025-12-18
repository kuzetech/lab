package com.kuzetech.bigdata.flink;

import com.maxmind.geoip2.DatabaseReader;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.File;
import java.net.InetAddress;

public class GeoEnrichJob {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        DataStreamSource<String> source = env.fromData("47.115.22.115");


    }

    public static class GeoEnrichFunction extends RichMapFunction<String, String> {
        private transient DatabaseReader reader;

        @Override
        public void open(Configuration parameters) throws Exception {
            // 1. 从分布式缓存获取文件
            File dbFile = getRuntimeContext().getDistributedCache().getFile("geoip-db");

            // 2. 初始化 Reader
            // 建议使用内存优先模式以提高查询速度
            reader = new DatabaseReader.Builder(dbFile).build();
        }

        @Override
        public String map(String ip) throws Exception {
            // 3. 使用 reader 查询 IP 信息
            InetAddress ipAddress = InetAddress.getByName(ip);
            return reader.city(ipAddress).getCity().getName();
        }

        @Override
        public void close() throws Exception {
            // 4. 记得关闭资源
            if (reader != null) {
                reader.close();
            }
        }
    }
}
