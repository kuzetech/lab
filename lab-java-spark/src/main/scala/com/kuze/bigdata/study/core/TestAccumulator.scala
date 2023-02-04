package com.kuze.bigdata.study.core

import com.kuze.bigdata.study.utils.SparkSessionUtils
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.broadcast.Broadcast

object TestAccumulator {

  def main(args: Array[String]): Unit = {

    val spark = SparkSessionUtils.initLocalSparkSession("TestAccumulator")

    val javaSparkContext = JavaSparkContext.fromSparkContext(spark.sparkContext)

    val wordRDD = javaSparkContext.parallelize(SparkSessionUtils.wordList)

    val disableWordCount = spark.sparkContext.longAccumulator("disabled word count")
    // spark.sparkContext.doubleAccumulator
    // spark.sparkContext.collectionAccumulator()

    def f(word: String): Boolean = {
      if(word.isEmpty){
          disableWordCount.add(1)
          return false
      }else{
          return true
      }
    }

    val availableWordRDD = wordRDD.filter(f)

    val result = availableWordRDD.collect()

    println(result)
    println(disableWordCount.value)
  }

}
