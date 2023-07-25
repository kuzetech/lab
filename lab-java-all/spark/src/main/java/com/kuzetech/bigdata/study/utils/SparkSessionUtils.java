package com.kuzetech.bigdata.study.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.GenericRow;
import org.apache.spark.sql.types.*;
import scala.Tuple2;

import static org.apache.spark.sql.types.DataTypes.IntegerType;
import static org.apache.spark.sql.types.DataTypes.StringType;

public class SparkSessionUtils {

    public static SparkSession initLocalSparkSession() {
        SparkConf conf = SparkConfUtils.initSparkConf();

        SparkSession spark = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();

        return spark;
    }


    public static Dataset<Row> generatePersonDataFrameByBeanClass() {
        SparkSession spark = initLocalSparkSession();

        JavaSparkContext javaSc = JavaSparkContext.fromSparkContext(spark.sparkContext());

        JavaRDD<Person> sourceRDD = javaSc.parallelize(Constants.PersonList);

        Dataset<Row> dataFrame = spark.createDataFrame(sourceRDD, Person.class);

        return dataFrame;
    }

    public static Dataset<Row> generatePersonDataFrameByStructType() {
        SparkSession spark = initLocalSparkSession();

        JavaSparkContext javaSc = JavaSparkContext.fromSparkContext(spark.sparkContext());

        JavaRDD<Tuple2<String, Integer>> sourceRDD = javaSc.parallelize(Constants.tupleList);

        JavaRDD<Row> rowJavaRDD = sourceRDD.map(o -> new GenericRow(new Object[]{o._1, o._2}));

        StructType structType = new StructType(new StructField[]{
                DataTypes.createStructField("name", StringType, false),
                new StructField("age", IntegerType, false, Metadata.empty()),
        });

        // DataTypes.createStructType()

        Dataset<Row> dataFrame = spark.createDataFrame(rowJavaRDD, structType);

        return dataFrame;
    }


}
