package com.kuze.bigdata.study;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Benchmark {

    private static void benchmarkForJava() {
        ClientLog log = ClientLog.getDefaultInstance();

        long start2 = System.currentTimeMillis();

        for (int i = 0; i < 50000000; i++) {
            String.valueOf(log.getLogId()).equals("1");
        }

        long end2 = System.currentTimeMillis();

        System.out.println("java:" + (end2 - start2) + " ms");
    }

    public static void benchmarkForGroovyClassLoader() {

        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

        String groovyCode = "public class demo1 {\n"
                + "    public boolean eval(com.kuze.bigdata.study.ClientLog log) {\n"
                + "        return String.valueOf(log.getLogId()).equals(\"1\");\n"
                + "    }\n"
                + "}";
        try {
            // 获得GroovyShell_2加载后的class
            Class<?> groovyClass = groovyClassLoader.parseClass(groovyCode);
            // 获得GroovyShell_2的实例
            GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

            ClientLog log = ClientLog.getDefaultInstance();

            long start1 = System.currentTimeMillis();

            for (int i = 0; i < 50000000; i++) {
                groovyObject.invokeMethod("eval", log);
            }

            long end1 = System.currentTimeMillis();

            System.out.println("groovy:" + (end1 - start1) + " ms");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public static void benchmarkForJanino() throws CompileException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        SimpleCompiler compiler = new SimpleCompiler();
        compiler.setParentClassLoader(Benchmark.class.getClassLoader());

        String content = "public class demo2 {\n"
                + "    public boolean eval(com.kuze.bigdata.study.ClientLog log) {\n"
                + "        return String.valueOf(log.getLogId()).equals(\"1\");\n"
                + "    }\n"
                + "}";
        compiler.cook(content);

        Class<?> aClass = compiler.getClassLoader().loadClass("demo2");

        Method sayMethod = aClass.getDeclaredMethod("eval", ClientLog.class);

        Object instance = aClass.newInstance();

        ClientLog log = ClientLog.getDefaultInstance();

        long start2 = System.currentTimeMillis();

        for (int i = 0; i < 50000000; i++) {
            sayMethod.invoke(instance, new Object[]{log});
        }

        long end2 = System.currentTimeMillis();

        System.out.println("janino:" + (end2 - start2) + " ms");
    }

    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 10; i++) {
            benchmarkForJava();

            // janino
            benchmarkForJanino();

            // groovy classloader
            benchmarkForGroovyClassLoader();

            System.out.println();
        }
    }

}
