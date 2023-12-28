package com.kuzetech.bigdata.spark.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;

public class TestClient {
    public static void main(String[] args) throws IOException, InterruptedException {

        URI uri = URI.create("hdfs://172.26.0.4:8020");

        //相关配置
        Configuration conf = new Configuration();
        //可以设置副本个数如:conf.set("dfs.replication","3");

        FileSystem fs = FileSystem.get(uri, conf, "hdfsuser");
        Path path = new Path("/test");

        if(!fs.exists(path)){
            FSDataOutputStream outputStream = fs.create(path, true);
            outputStream.writeUTF("test");
            outputStream.close();
        }

        fs.close();
    }
}
