package com.kuze.bigdata.study.rocksdb;

import com.alibaba.fastjson.JSONObject;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.util.ArrayList;
import java.util.List;

public class PutByKey {

    public static void main(String[] args) throws RocksDBException {

        RocksDB db = RocksDB.open("/Users/huangsw/code/study/study-spark/checkpoint/wal");

        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("5");

        // MyConfig config = new MyConfig(list);

        String jsonStr = JSONObject.toJSONString(list);

        String key = "test";
        byte[] keyBytes = key.getBytes();

        db.put(keyBytes, jsonStr.getBytes());

    }
}
