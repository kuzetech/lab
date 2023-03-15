package com.kuze.bigdata.study.streaming.outputModel;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.streaming.OutputMode;


public class TestUpdateOutputModel {
    public static void main(String[] args) throws Exception {
        Dataset<Row> countDF = TestCompleteOutputModel.getRowDataset();

        // 仅输出内容有更新的计算结果
        countDF.writeStream()
                .format("console")
                .option("truncate", false)
                .outputMode(OutputMode.Update())
                .start()
                .awaitTermination();
    }
}
