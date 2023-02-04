package com.kuze.bigdata.study.sql.create

import com.kuze.bigdata.study.utils.SparkSessionUtils

object TestCreateDataFrameByRDBMS {

  def main(args: Array[String]): Unit = {

    val spark = SparkSessionUtils.initLocalSparkSession("TestCreateDataFrameByRDBMS")

    val df = spark.read.format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("url", "jdbc:mysql://hostname:port/mysql")
      .option("user", "root")
      .option("password", "root")
      .option("numPartitions", 20)
      // 除了制定数据库和表，还可以传 sql 语句，直接过滤数据
      // .option("dbtable","select * from default.table where gender = 'female'")
      .option("dbtable", "default.table")
      .load()

    df.show()
    df.printSchema()

  }
}
