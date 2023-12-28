package com.kuzetech.bigdata.spark.rocksdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.util.List;

public class GetByKey {

    public static void main(String[] args) throws RocksDBException, JsonProcessingException {

        RocksDB db = RocksDB.open("/Users/huangsw/code/funny/funnydb/spark-block-aggregator/checkpoint/user/wal");

        String key = "Structured Streaming Checkpoint测试userkuze";
        byte[] keyBytes = key.getBytes();

        byte[] result = db.get(keyBytes);

        String resultStr = new String(result);

        ObjectMapper mapper = new ObjectMapper();

        List<String> list = mapper.readerForArrayOf(String.class).readValue(resultStr);

        System.out.println(list);
    }
}
