package com.kuze.bigdata.study.utils;

import scala.collection.Iterator;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.List;

public class JavaToScalaUtils<T> {

    public Iterator<T> convertListToIterator(List<T> javaList) {
        Iterator<T> scalaIterator = JavaConverters.<T>asScalaIterator(javaList.iterator());
        return scalaIterator;
    }

    public Seq<T> convertListToSet(List<T> javaList) {
        Seq<T> scalaSeq = JavaConverters.asScalaIteratorConverter(javaList.iterator()).asScala().toSeq();
        return scalaSeq;
    }

}
