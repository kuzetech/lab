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

        // 计算每个步骤的转化率
        Double[] conversionRates = new Double[funnelSteps.length];
        for (int i = 0; i < funnelSteps.length; i++) {
            Integer step = funnelSteps[i];
            // 计算当前步骤的转化率
            if (i == 0) {
                // 第一步的转化率为100%
                conversionRates[i] = 1.0;
            } else {
                // 计算当前步骤的转化率
                Integer prevStep = funnelSteps[i-1];
                Dataset<Row> prevStepUsers = data.filter(col("level").geq(prevStep)).select("deviceId").distinct();
                Dataset<Row> stepUsers = data.filter(col("level").geq(step)).select("deviceId").distinct();
                double conversionRate = (double) stepUsers.count() / (double) prevStepUsers.count();
                conversionRates[i] = conversionRate;
            }
        }

        for (Double conversionRate : conversionRates) {
            // System.out.println(conversionRate);
            BigDecimal bd = new BigDecimal(conversionRate);
            System.out.println(bd.setScale(2, RoundingMode.DOWN).toString());
        }

    }
}
