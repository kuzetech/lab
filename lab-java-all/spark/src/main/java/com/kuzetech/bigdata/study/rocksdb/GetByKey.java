package com.kuzetech.bigdata.study.rocksdb;

import com.alibaba.fastjson.JSONObject;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.util.List;

public class GetByKey {

    public static void main(String[] args) throws RocksDBException {

        RocksDB db = RocksDB.open("/Users/huangsw/code/funny/funnydb/spark-block-aggregator/checkpoint/user/wal");

        String key = "Structured Streaming Checkpoint测试userkuze";
        byte[] keyBytes = key.getBytes();

        byte[] result = db.get(keyBytes);

        String resultStr = new String(result);

        List<String> list = JSONObject.parseArray(resultStr, String.class);
        System.out.println(list);


    }
}
