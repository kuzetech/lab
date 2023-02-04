package com.kuze.bigdata.study.sql.create

import com.kuze.bigdata.study.utils.SparkSessionUtils

object TestCreateDataFrameByParquetOrORC {

  def main(args: Array[String]): Unit = {

    val spark = SparkSessionUtils.initLocalSparkSession("TestCreateDataFrameByParquetOrORC")

    val df = spark.read.format("parquet") // 或者 orc
      .load("file/test.parquet")

    df.show()
    df.printSchema()

  }
}
