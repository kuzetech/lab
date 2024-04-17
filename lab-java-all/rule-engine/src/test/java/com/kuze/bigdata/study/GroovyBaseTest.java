package com.kuze.bigdata.study;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.junit.Test;

public class GroovyBaseTest {

    @Test
    public void base() throws InstantiationException, IllegalAccessException {
        String groovy = " def call(int a,int b) { " +
                "   return a + b " +
                "}";

        GroovyClassLoader loader = new GroovyClassLoader();
        Class scriptClass = loader.parseClass(groovy);
        GroovyObject scriptInstance = (GroovyObject) scriptClass.newInstance();
        Object result = scriptInstance.invokeMethod("call", new Object[]{1, 2});
        System.out.println("Groovy result=" + result);
    }

}
