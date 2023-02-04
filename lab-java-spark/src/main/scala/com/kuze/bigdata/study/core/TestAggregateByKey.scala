package com.kuze.bigdata.study.core

import com.kuze.bigdata.study.utils.SparkSessionUtils

object TestAggregateByKey {

  def main(args: Array[String]): Unit = {

    val wordRdd = SparkSessionUtils.generateWordListRdd("TestAggregateByKey")

    val wordAndOneRDD = wordRdd.map(word => (word, 1))

    def f1(x: Int, y: Int): Int = {
      return x+y;
    }

    def f2(x: Int, y: Int): Int = {
      return math.max(x, y);
    }

    val wordCount = wordAndOneRDD.aggregateByKey(0)(f1, f2)

    val result = wordCount.collect()

    println(result.mkString);

  }

}
