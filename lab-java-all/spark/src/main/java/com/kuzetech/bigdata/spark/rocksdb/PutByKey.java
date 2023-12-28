package com.kuzetech.bigdata.spark.rocksdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.util.ArrayList;
import java.util.List;

public class PutByKey {

    public static void main(String[] args) throws RocksDBException, JsonProcessingException {

        RocksDB db = RocksDB.open("/Users/huangsw/code/study/study-spark/checkpoint/wal");

        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("5");

        // MyConfig config = new MyConfig(list);

        ObjectMapper mapper = new ObjectMapper();

        String jsonStr = mapper.writeValueAsString(list);

        String key = "test";
        byte[] keyBytes = key.getBytes();

        db.put(keyBytes, jsonStr.getBytes());

    }
}
