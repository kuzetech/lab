package com.kuze.bigdata.study.streaming.udsink;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.execution.streaming.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class MySink implements Sink, Serializable {

    private final static Logger logger = LoggerFactory.getLogger(MySink.class);

    @Override
    public void addBatch(long batchId, Dataset<Row> data) {
        long count = data.count();
        logger.info("批次号为 {} 的批次一共有 {} 条数据", batchId, count);
        // 可以从流操作转换成批操作
        // data.queryExecution().toRdd().mapPartitionsWithIndex();
    }

}
