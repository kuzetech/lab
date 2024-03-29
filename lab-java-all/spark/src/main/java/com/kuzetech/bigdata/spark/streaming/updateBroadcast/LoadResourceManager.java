/*
package com.kuzetech.bigdata.study.streaming.updateBroadcast;


import com.kuzetech.bigdata.study.clickhouse.ClickHouseQueryService;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.SparkSession;

import java.io.Serializable;

class LoadResourceManager implements Serializable {

    private volatile Broadcast<ClickhouseBroadcastContent> broadcast = null;

    public Broadcast<ClickhouseBroadcastContent> get() {
        return broadcast;
    }

    public void unpersist() {
        broadcast.unpersist(true);
    }

    public void load(SparkSession spark, ClickHouseQueryService chService) throws Exception {
        JavaSparkContext jsc = JavaSparkContext.fromSparkContext(spark.sparkContext());
        ClickhouseBroadcastContent content = chService.searchClickhouseBroadcastContent();
        broadcast = jsc.broadcast(content);
    }
}
*/
