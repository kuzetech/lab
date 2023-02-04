package com.kuze.bigdata.study.utils;

import scala.collection.Iterator;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JavaToScalaUtils<T> {

    public Iterator<T> convertJavaListToScalaIterator(List<T> javaList) {
        Iterator<T> scalaIterator = JavaConverters.asScalaIterator(javaList.iterator());
        return scalaIterator;
    }

    public Seq<T> convertJavaListToScalaSet(List<T> javaList) {
        Seq<T> scalaSeq = JavaConverters.asScalaIteratorConverter(javaList.iterator()).asScala().toSeq();
        return scalaSeq;
    }

}
