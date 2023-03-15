package com.kuze.bigdata.study.streaming.multipleStreaming;

import com.kuze.bigdata.study.streaming.listener.MyStreamingQueryListener;
import com.kuze.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.spark.sql.functions.col;

public class TestMultipleStreamingThread {

    private final static Logger logger = LoggerFactory.getLogger(TestMultipleStreamingThread.class);

    private static class NamedDefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        NamedDefaultThreadFactory(String prefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = prefix + "-pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    public static void main(String[] args) throws Exception{
        SparkSession spark = SparkSessionUtils.initLocalSparkSession();

        spark.streams().addListener(new MyStreamingQueryListener());

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("检测到程序关闭，可以在这里关闭数据库连接之类的");
            }
        });

        ExecutorService executorService = Executors.newFixedThreadPool(2,
                new NamedDefaultThreadFactory("TestMultipleStreamingThread"));

        // 如果单纯的使用 ExecutorService 获取的 list future 只能等所有任务都结束了才能一起返回
        // 但是多流任务，实际上只要一个流出错，其他的流应该也要停止，所以需要使用 CompletionService
        CompletionService completionService = new ExecutorCompletionService<>(executorService);

        Callable task1 = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Dataset<Row> kafkaDF = spark.readStream()
                        .format("kafka")
                        .option("kafka.bootstrap.servers", spark.conf().get("spark.kafka.bootstrap.servers"))
                        .option("subscribe", "event")
                        .option("startingOffsets", "earliest")
                        .option("group_id", "TestMultipleStreamingThread")
                        .load();

                Dataset<Row> messageDF = kafkaDF.selectExpr("CAST(value AS STRING)");

                Dataset<Row> tableDF = messageDF.selectExpr("from_json(value, 'uid String, eventId String, eventTime Date') as value").select(col("value.*"));

                tableDF.writeStream()
                        .option("checkpointLocation", "checkpoint/TestMultipleStreamingThread/pipeline1")
                        .format("console")
                        .option("truncate", false)
                        .trigger(Trigger.ProcessingTime("5 seconds"))
                        .start()
                        .awaitTermination();

                return true;
            }
        };


        Callable task2 = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Dataset<Row> kafkaDF2 = spark.readStream()
                        .format("kafka")
                        .option("kafka.bootstrap.servers", spark.conf().get("spark.kafka.bootstrap.servers"))
                        .option("subscribe", "event2")
                        .option("startingOffsets", "earliest")
                        .option("group_id", "TestMultipleStreamingThread")
                        .load();

                Dataset<Row> messageDF2 = kafkaDF2.selectExpr("CAST(value AS STRING)");

                Dataset<Row> tableDF2 = messageDF2.selectExpr("from_json(value, 'uid String, eventId String, eventTime Date') as value").select(col("value.uid"));

                Dataset<Row> tableDF3 = tableDF2.selectExpr("CAST(uid AS INT)");

                Dataset<Row> tableDF4 = tableDF3.selectExpr("10 / uid");

                tableDF4.writeStream()
                        .option("checkpointLocation", "checkpoint/TestMultipleStreamingThread/pipeline2")
                        .format("console")
                        .option("truncate", false)
                        .trigger(Trigger.ProcessingTime("5 seconds"))
                        .start()
                        .awaitTermination();

                return true;
            }
        };

        List<Callable<Boolean>> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);

        for (Callable<Boolean> task : tasks) {
            completionService.submit(task);
        }


        try {
            completionService.take().get();
        } catch (Exception e) {
            logger.error("执行 pipeline 处理时出现错误，具体错误为：{}", e.getMessage());
        }finally {
            executorService.shutdown();
            System.exit(1);
        }

    }
}
