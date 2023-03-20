package com.kuze.bigdata.study.streaming.udsink;

import com.kuze.bigdata.study.clickhouse.ClickHouseQueryConfig;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap;
import org.apache.spark.sql.execution.streaming.Sink;
import org.apache.spark.sql.sources.DataSourceRegister;
import org.apache.spark.sql.sources.StreamSinkProvider;
import org.apache.spark.sql.streaming.OutputMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.Seq;
import scala.collection.immutable.Map;

public class ClickHouseStreamSinkProvider implements StreamSinkProvider, DataSourceRegister {

    private final static Logger logger = LoggerFactory.getLogger(ClickHouseStreamSinkProvider.class);

    @Override
    public String shortName() {
        return "clickhouse";
    }

    @Override
    public Sink createSink(
            SQLContext sqlContext,
            Map<String, String> parameters,
            Seq<String> partitionColumns,
            OutputMode outputMode) {

        logger.error("这里是在 driver 执行");

        CaseInsensitiveMap<String> optionMap = new CaseInsensitiveMap<>(parameters);

        ClickHouseQueryConfig config = new ClickHouseQueryConfig(optionMap);

        return new ClickHouseSink(config, sqlContext.sparkContext().hadoopConfiguration());
    }

}
