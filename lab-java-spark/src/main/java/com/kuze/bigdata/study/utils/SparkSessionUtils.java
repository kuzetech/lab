package com.kuze.bigdata.study.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.GenericRow;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import static org.apache.spark.sql.types.DataTypes.IntegerType;
import static org.apache.spark.sql.types.DataTypes.StringType;

public class SparkSessionUtils {

    public static SparkSession initLocalSparkSession(String appName) {
        SparkConf conf = new SparkConf();
        conf.setAppName(appName);
        conf.setMaster("local[*]");

        SparkSession spark = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();

        return spark;
    }


    public static Dataset<Row> generatePersonDataFrameByBeanClass(String appName) {
        SparkSession spark = initLocalSparkSession(appName);

        JavaSparkContext javaSc = JavaSparkContext.fromSparkContext(spark.sparkContext());

        JavaRDD<Person> sourceRDD = javaSc.parallelize(Constants.PersonList);

        Dataset<Row> dataFrame = spark.createDataFrame(sourceRDD, Person.class);

        return dataFrame;
    }

    public static Dataset<Row> generatePersonDataFrameByStructType(String appName) {
        SparkSession spark = initLocalSparkSession(appName);

        JavaSparkContext javaSc = JavaSparkContext.fromSparkContext(spark.sparkContext());

        JavaRDD<Tuple2<String, Integer>> sourceRDD = javaSc.parallelize(Constants.tupleList);

        JavaRDD<Row> rowJavaRDD = sourceRDD.map(o -> new GenericRow(new Object[]{o._1, o._2}));

        StructType structType = new StructType(new StructField[]{
                new StructField("name", StringType, false, Metadata.empty()),
                new StructField("age", IntegerType, false, Metadata.empty()),
        });

        Dataset<Row> dataFrame = spark.createDataFrame(rowJavaRDD, structType);

        return dataFrame;
    }


}
