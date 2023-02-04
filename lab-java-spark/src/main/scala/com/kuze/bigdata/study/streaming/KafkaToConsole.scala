package com.kuze.bigdata.study.streaming

import com.kuze.bigdata.study.utils.SparkSessionUtils

object KafkaToConsole {

  def main(args: Array[String]): Unit = {

    val spark = SparkSessionUtils.initLocalSparkSession("scala-first-try")

    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "event")
      .option("startingOffsets", "latest")
      .option("group_id", "scala-first-try")
      .load

    val messageDF = kafkaDF.selectExpr("CAST(value AS STRING)")

    messageDF
      .writeStream
      .format("console")
      .option("truncate", false)
      .option("checkpointLocation", "./checkpoint/scala-first-try")
      .start()
      .awaitTermination()

  }

}
