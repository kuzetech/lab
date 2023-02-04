package com.kuze.bigdata.study.sql.create

import com.kuze.bigdata.study.utils.SparkSessionUtils
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

object TestCreateDataFrameByCSV {

  def main(args: Array[String]): Unit = {

    val spark = SparkSessionUtils.initLocalSparkSession("TestCreateDataFrameByCSV")

    val structType: StructType = StructType(Array(
      StructField("name", StringType),
      StructField("age", IntegerType)
    ))

    val df = spark.read.format("csv")
      // 如果没有指定 schema，默认每一列都是 string
      .schema(structType)
      // 第一行为列名
      .option("header", true)
      .option("seq", ",")
      .option("escape", "\\")
      .option("nullValue", "")
      .option("dateFormat", "yyyy-MM-dd")
      // permissive     保留异常数据
      // dropMalformed  丢弃异常数据
      // failFast       异常停止
      .option("mode", "dropMalformed")
      .load("file/test.csv")

    df.show()
    df.printSchema()

  }
}
