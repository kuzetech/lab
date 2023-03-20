package com.kuze.bigdata.study.streaming.udsink;

import com.kuze.bigdata.study.clickhouse.ClickHouseQueryConfig;
import com.kuze.bigdata.study.clickhouse.ClickHouseQueryService;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.TaskContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.execution.streaming.Sink;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;

public class ClickHouseSink implements Sink, Serializable {

    private final static Logger logger = LoggerFactory.getLogger(ClickHouseSink.class);

    private ClickHouseQueryConfig config;
    private ClickHouseQueryService service;
    private WalService walService;
    private Long createTimeStamp = new Date().getTime();

    public ClickHouseSink(ClickHouseQueryConfig config, Configuration hadoopConfig) {
        this.config = config;
        try {
            this.service = new ClickHouseQueryService(config, true);
            this.walService = new FileSystemWalService(config, hadoopConfig);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("create ClickHouseQueryService error");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("create FileSystemWalService error");
        }
    }



    @Override
    public void addBatch(long batchId, Dataset<Row> data) {
        Wal wal = new Wal();
        final StructType rowSchema = data.schema();

        try {
            wal.setStructType(service.searchDestTableStructType());

            Wal lastWal = walService.getWal();

            if (lastWal != null) {
                logger.error(lastWal.toString());
                wal.setBatchAllocation(lastWal.getBatchAllocation());
                wal.setBatchIndex(lastWal.getBatchIndex());
            }else{
                logger.error("lastWal 为空啊");
                wal.setBatchAllocation(service.searchAvailableServer());
                wal.setBatchIndex(createTimeStamp + "-" + batchId);
            }

            walService.setWal(wal);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("init Batch error when execute addBatch function");
        }

        // 从流操作转换成批操作
        data.queryExecution().toRdd().toJavaRDD().repartition(wal.getBatchAllocation().size()).foreachPartition(iter -> {
            ClickHouseQueryService workService = new ClickHouseQueryService(config, false);
            int partitionId = TaskContext.getPartitionId();
            String connectUrl = wal.getBatchAllocation().get(partitionId);
            workService.batchInsert(iter, rowSchema, wal.getStructType(), connectUrl, wal.getBatchIndex());
        });

        try {
            // System.exit(0);
            walService.deleteWal();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("execute walService deleteWal error");
        }
    }

}
