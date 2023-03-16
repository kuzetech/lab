package com.kuze.bigdata.study.streaming.udsink;

import com.kuze.bigdata.study.clickhouse.ClickHouseQueryConfig;
import com.kuze.bigdata.study.clickhouse.ClickHouseQueryService;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.execution.streaming.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.spark.sql.functions.col;

public class ClickHouseSink implements Sink, Serializable {

    private final static Logger logger = LoggerFactory.getLogger(ClickHouseSink.class);

    private ClickHouseQueryConfig config;
    private ClickHouseQueryService service;

    public ClickHouseSink(ClickHouseQueryConfig config){
        this.config = config;
        try {
            this.service = new ClickHouseQueryService(config, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addBatch(long batchId, Dataset<Row> data) {
        logger.error("这里是在 driver 执行");
        List<String> availableServers = new ArrayList<>();
        final Test test = new Test();

        try {
            availableServers.addAll(service.searchAvailableServer());
            test.setStructType(service.searchDestTableStructType());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Dataset<Row> partitionDF = data.repartition(availableServers.size(), col(config.getShardingColumn()));

        //StructType rowSchema = partitionDF.schema();

        // 从流操作转换成批操作
        partitionDF.queryExecution().toRdd().foreachPartition(iter -> {
            logger.error("test");
            return null;
        });

        /*partitionDF.queryExecution().toRdd().toJavaRDD().foreachPartition(iter -> {
            ClickHouseQueryService workService = new ClickHouseQueryService(config, false);
            int partitionId = TaskContext.getPartitionId();
            String connectUrl = availableServers.get(partitionId);
            workService.batchInsert(iter, rowSchema, test.getStructType(), connectUrl , "test");
        });*/
    }

}
