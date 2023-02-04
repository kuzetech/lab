package com.kuze.bigdata.study.core

import org.apache.spark.{SparkConf}
import org.apache.spark.sql.SparkSession

object TestInitSparkSession {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
    conf.setAppName("TestInitSparkSession")
    conf.setMaster("local[*]")

    val spark = SparkSession.builder()
      .config(conf)
      //.config("","")
      .appName("TestInitSparkSession")
      .master("local[*]")
      .getOrCreate()

    val sc = spark.sparkContext

    // val lines = sc.textFile("data.txt")

    val data = Array("a", "b", "c", "d", "e")
    val distData = sc.parallelize(data)

    val words = distData.map(word => (word, 1))
    val wordAndCount = words.reduceByKey((a, b) => a + b)

    val result = wordAndCount.collect()
    println(result.mkString)

  }
}
