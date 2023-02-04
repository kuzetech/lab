package com.kuze.bigdata.study.core

import com.kuze.bigdata.study.utils.SparkSessionUtils
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.broadcast.Broadcast

object TestBroadcast {

  def main(args: Array[String]): Unit = {

    val spark = SparkSessionUtils.initLocalSparkSession("TestBroadcast")

    val javaSparkContext = JavaSparkContext.fromSparkContext(spark.sparkContext)

    val wordRDD = javaSparkContext.parallelize(SparkSessionUtils.wordList)

    val availableWord: List[String] = List("a", "b")

    val availableWordBroadcast: Broadcast[List[String]] = spark.sparkContext.broadcast(availableWord)

    val availableWordRDD = wordRDD.filter(word => availableWordBroadcast.value.contains(word))

    val result = availableWordRDD.collect()

    println(result)






  }

}
