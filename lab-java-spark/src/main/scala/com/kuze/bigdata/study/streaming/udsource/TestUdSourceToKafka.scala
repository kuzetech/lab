package com.kuze.bigdata.study.streaming.udsource

import java.util.concurrent.TimeUnit
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.Trigger

object TestUdSourceToKafka {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf
    conf.setMaster("local[3]")

    val spark = SparkSession.builder.appName("TestUdSourceToKafka").config(conf).getOrCreate

    val rateDF = spark.readStream
      .format("com.kuze.bigdata.study.streaming.udsource.ScRateStreamProvider")
      .option("rowsPerSecond", 10000)
      .option("rampUpTime", "1s")
      .option("numPartitions", 3)
      .load

    rateDF
      .selectExpr("to_json(struct(*)) AS value")
      .writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("topic", "event")
      .option("checkpointLocation", "./checkpoint/TestUdSourceToKafka")
      .trigger(Trigger.ProcessingTime(5, TimeUnit.SECONDS))
      .start()
      .awaitTermination()

  }

}
