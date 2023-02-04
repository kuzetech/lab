package com.kuze.bigdata.study.streaming.udsink;

import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.execution.streaming.Sink;
import org.apache.spark.sql.sources.DataSourceRegister;
import org.apache.spark.sql.sources.StreamSinkProvider;
import org.apache.spark.sql.streaming.OutputMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.Seq;
import scala.collection.immutable.Map;

public class MyStreamSinkProvider implements StreamSinkProvider, DataSourceRegister {

    private final static Logger logger = LoggerFactory.getLogger(MyStreamSinkProvider.class);

    @Override
    public String shortName() {
        return "test";
    }

    @Override
    public Sink createSink(
            SQLContext sqlContext,
            Map<String, String> parameters,
            Seq<String> partitionColumns,
            OutputMode outputMode) {

        // 主线程
        return new MySink();
    }

}
