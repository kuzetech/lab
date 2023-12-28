package com.kuzetech.bigdata.spark.streaming.outputModel;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.streaming.OutputMode;


public class TestUpdateOutputModel {
    public static void main(String[] args) throws Exception {
        Dataset<Row> countDF = TestCompleteOutputModel.getRowDataset();

        // 输出有更新的行
        countDF.writeStream()
                .format("console")
                .option("truncate", false)
                .outputMode(OutputMode.Update())
                .start()
                .awaitTermination();
    }
}
