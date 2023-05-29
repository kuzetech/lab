package com.kuze.bigdata.study.funnel;


import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.types.DataTypes.IntegerType;
import static org.apache.spark.sql.types.DataTypes.StringType;

public class FunnelAnalysis {
    public static void main(String[] args) {
        // 创建SparkSession
        SparkSession spark = SparkSession.builder()
                .appName("FunnelAnalysis")
                .master("local[*]")
                .getOrCreate();

        // 定义漏斗步骤
        Integer[] funnelSteps = {1, 2, 3,4,5,6,7,8,9,10,11,12,13,14,15,16,17};


        StructType structType = new StructType(new StructField[]{
                DataTypes.createStructField("deviceId", StringType, false),
                DataTypes.createStructField("level", IntegerType, false),
        });

        Dataset<Row> data = spark.read().schema(structType).csv("/Users/huangsw/code/lab/lab-java-spark/data/level.csv");

        Dataset<Row> firstStepUsers = data.filter(col("level").geq(funnelSteps[0])).select("deviceId").distinct();
        long firstStepUserCount = firstStepUsers.count();

        // 计算每个步骤的转化率
        FunnelResult[] results = new FunnelResult[funnelSteps.length];
        for (int i = 0; i < funnelSteps.length; i++) {

            if (i == 0) {
                // 第一步的转化率为100%
                results[i] = new FunnelResult(firstStepUserCount,1.0);
            } else {
                // 计算当前步骤的转化率
                Integer step = funnelSteps[i];
                Dataset<Row> stepUsers = data.filter(col("level").geq(step)).select("deviceId").distinct();
                long stepUserCount = stepUsers.count();
                double conversionRate = (double)  stepUserCount/ (double) firstStepUserCount;
                results[i] = new FunnelResult(stepUserCount,conversionRate);
            }
        }

        for (int i = 0; i < results.length; i++) {
            FunnelResult result = results[i];
            BigDecimal bd = new BigDecimal(result.getPercent() * 100);
            String percentStr = bd.setScale(2, RoundingMode.DOWN).toString();

            System.out.println(String.format("%d|%s", result.getStepUserCount(), percentStr));
        }
    }
}
