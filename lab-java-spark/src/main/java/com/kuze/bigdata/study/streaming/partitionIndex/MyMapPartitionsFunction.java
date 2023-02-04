package com.kuze.bigdata.study.streaming.partitionIndex;

import org.apache.spark.TaskContext;
import org.apache.spark.api.java.function.MapPartitionsFunction;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Serializable;

import java.util.Iterator;


public class MyMapPartitionsFunction implements MapPartitionsFunction<Row, Row>, Serializable {

    private final static Logger logger = LoggerFactory.getLogger(MyMapPartitionsFunction.class);

    @Override
    public Iterator<Row> call(Iterator<Row> input){
        int partitionId = TaskContext.getPartitionId();
        // 分区ID从0开始
        logger.info("当前分区ID为：{}", partitionId);
        return input;
    }
}
