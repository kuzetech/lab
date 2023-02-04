package com.kuze.bigdata.study.sql.create

import com.kuze.bigdata.study.utils.SparkSessionUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row}

object TestCreateDataFrameByRDD {

  def main(args: Array[String]): Unit = {

    val spark = SparkSessionUtils.initLocalSparkSession("TestCreateDataFrameByRDD")

    val sc = spark.sparkContext

    val seq: Seq[(String, Int)] = Seq(("Bob", 14), ("Alice", 18))
    val rdd: RDD[(String, Int)] = sc.parallelize(seq, 2)

    val structType: StructType = StructType(Array(
      StructField("name", StringType),
      StructField("age", IntegerType)
    ))

    // 可以通过提供数据结构创建 DF
    val rowRDD: RDD[Row] = rdd.map(x => Row(x._1, x._2))
    val df2: DataFrame = spark.createDataFrame(rowRDD, structType)
    df2.show(false)
    df2.printSchema()

    // 也可以通过 toDF 自动识别数据结构
    // toDF 需要导入 spark.implicits._ 里面有各种隐式方法
    import spark.implicits._
    val df1: DataFrame = rdd.toDF()
    df1.show(false)
    df1.printSchema()


  }
}
