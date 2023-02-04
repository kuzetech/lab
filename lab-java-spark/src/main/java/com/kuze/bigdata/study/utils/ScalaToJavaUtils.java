package com.kuze.bigdata.study.utils;

import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.collection.JavaConverters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ScalaToJavaUtils<T> {

    public List<T> convertScalaListToJavaList(scala.collection.immutable.List<T> scalaList) {
        List<T> javaList = JavaConverters.seqAsJavaList(scalaList);
        return javaList;
    }

}
