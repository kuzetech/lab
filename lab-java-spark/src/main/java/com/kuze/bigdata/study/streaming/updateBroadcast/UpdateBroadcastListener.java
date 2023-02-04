package com.kuze.bigdata.study.streaming.updateBroadcast;

import com.kuze.bigdata.study.clickhouse.ClickhouseQueryService;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQueryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.LongAccumulator;

public class UpdateBroadcastListener extends StreamingQueryListener {

    private final static Logger logger = LoggerFactory.getLogger(UpdateBroadcastListener.class);
    private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static Long updateBroadcastIntervalMileSec = 5000L;

    private SparkSession spark;
    private LoadResourceManager loadResourceManager;
    private LongAccumulator nextUpdateTimeStamp;
    private ClickhouseQueryService chService;

    public UpdateBroadcastListener(
            SparkSession spark,
            LoadResourceManager loadResourceManager,
            ClickhouseQueryService chService) {
        this.spark = spark;
        this.loadResourceManager = loadResourceManager;
        this.chService = chService;

        nextUpdateTimeStamp = new LongAccumulator((left, right) -> left + right, System.currentTimeMillis());
    }

    @Override
    public void onQueryStarted(QueryStartedEvent event) {
    }

    // 这个方法不是线程安全的
    @Override
    public void onQueryProgress(QueryProgressEvent event) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis >= nextUpdateTimeStamp.longValue()) {
            synchronized (UpdateBroadcastListener.class) {
                if (currentTimeMillis >= nextUpdateTimeStamp.longValue()) {

                    Date now = new Date(currentTimeMillis);
                    logger.info("在 {} 时刻更新目标表 {}.{} 的表结构",
                            format.format(now),
                            ClickhouseQueryService.CLICKHOUSE_DEST_DATABASE,
                            ClickhouseQueryService.CLICKHOUSE_DEST_TABLE);

                    this.loadResourceManager.unpersist();
                    try {
                        this.loadResourceManager.load(spark, this.chService);
                    } catch (Exception e) {
                        logger.error("获取目标表 {}.{} 的表结构时出现异常，异常信息为：{}",
                                ClickhouseQueryService.CLICKHOUSE_DEST_DATABASE,
                                ClickhouseQueryService.CLICKHOUSE_DEST_TABLE,
                                e.getMessage());
                        System.exit(1);
                    }

                    long nextUpdateTimeMileSec = nextUpdateTimeStamp.longValue() + this.updateBroadcastIntervalMileSec;
                    nextUpdateTimeStamp.accumulate(this.updateBroadcastIntervalMileSec);
                    Date nextUpdateTime = new Date(nextUpdateTimeMileSec);
                    logger.info("下一次更新目标表 {}.{} 的表结构时间为 {}",
                            ClickhouseQueryService.CLICKHOUSE_DEST_DATABASE,
                            ClickhouseQueryService.CLICKHOUSE_DEST_TABLE,
                            format.format(nextUpdateTime));
                }
            }
        }
    }


    @Override
    public void onQueryTerminated(QueryTerminatedEvent event) {

    }
}
