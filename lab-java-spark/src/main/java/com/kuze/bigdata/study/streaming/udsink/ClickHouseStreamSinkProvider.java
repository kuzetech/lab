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
        return "test";
    }

    @Override
    public Sink createSink(
            SQLContext sqlContext,
            Map<String, String> parameters,
            Seq<String> partitionColumns,
            OutputMode outputMode) {

        logger.error("这里是在 driver 执行");

        CaseInsensitiveMap<String> optionMap = new CaseInsensitiveMap<>(parameters);

        String connectUrl = optionMap.get("connectUrl").isEmpty() ? "" : parameters.get("connectUrl").get();
        String cluster = optionMap.get("cluster").isEmpty() ? "" : parameters.get("cluster").get();
        String port = optionMap.get("port").isEmpty() ? "" : parameters.get("port").get();
        String user = optionMap.get("user").isEmpty() ? "" : parameters.get("user").get();
        String password = optionMap.get("password").isEmpty() ? "" : parameters.get("password").get();
        String database = optionMap.get("database").isEmpty() ? "" : parameters.get("database").get();
        String table = optionMap.get("table").isEmpty() ? "" : parameters.get("table").get();
        String shardingColumn = optionMap.get("shardingColumn").isEmpty() ? "" : parameters.get("shardingColumn").get();

        ClickHouseQueryConfig config = new ClickHouseQueryConfig(connectUrl, cluster, port, user, password, database, table, shardingColumn);

        return new ClickHouseSink(config);
    }

}
