package com.kuze.bigdata.study.utils;

import scala.collection.JavaConverters;

import java.util.List;

public class ScalaToJavaUtils<T> {

    public List<T> convertList(scala.collection.immutable.List<T> scalaList) {
        List<T> javaList = JavaConverters.<T>seqAsJavaList(scalaList);
        return javaList;
    }


    public List<T> convertSeqToList(scala.collection.Seq<T> seq) {
        List<T> javaList = JavaConverters.<T>seqAsJavaList(seq);
        return javaList;
    }
}
