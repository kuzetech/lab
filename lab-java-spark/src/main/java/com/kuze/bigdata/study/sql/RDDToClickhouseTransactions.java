package com.kuze.bigdata.study.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuze.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import ru.yandex.clickhouse.settings.ClickHouseQueryParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * clickhouse jdbc 插入实现了事务，就能够保证仅一次的语义，但该功能还属于实验性阶段
 * 具体可以参考 https://github.com/ClickHouse/ClickHouse/issues/22086
 *
 */
public class RDDToClickhouseTransactions
{
    public static void main( String[] args ) {

        SparkSession spark = SparkSessionUtils.initLocalSparkSession("RDDToClickhouseTransactions");

        JavaSparkContext jsc = JavaSparkContext.fromSparkContext(spark.sparkContext());
        List<Event> events = new ArrayList<>();
        events.add(new Event("1", "user-login", "2022-01-01"));
        events.add(new Event("2", "user-login", "2022-01-02"));
        events.add(new Event("3", "user-login", "2022-01-03-aasd"));
        events.add(new Event("4", "user-login", "2022-01-04"));
        events.add(new Event("5", "user-login", "2022-01-05"));
        JavaRDD<Event> sourceRDD = jsc.parallelize(events, 2);

        ObjectMapper objectMapper = new ObjectMapper();

        JavaRDD<String> jsonRDD = sourceRDD.map(new Function<Event, String>() {
            @Override
            public String call(Event v1) throws Exception {
                String jsonStr = objectMapper.writeValueAsString(v1);
                return jsonStr;
            }
        });

        Dataset<Row> sourceDF = spark.read().json(jsonRDD);

        Properties properties = new Properties();
        properties.put("driver", "ru.yandex.clickhouse.ClickHouseDriver");
        properties.put("user", "default");
        properties.put("password", "");
        properties.put(ClickHouseQueryParam.INSERT_DEDUPLICATE.getKey(), "1");
        properties.put("implicit_transaction", true);

        String url = "jdbc:clickhouse://localhost:8121?log_queries=1";

        // clickhouse 必须 22.7 以上
        sourceDF.write()
                .mode("append")
                .option("driver","com.clickhouse.jdbc.ClickHouseDriver")
                .jdbc(url, "default.event_local", properties);
    }
}
