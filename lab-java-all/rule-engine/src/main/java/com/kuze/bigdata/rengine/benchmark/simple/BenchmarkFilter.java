package com.kuze.bigdata.rengine.benchmark.simple;

import groovy.lang.GroovyClassLoader;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;

import java.lang.reflect.InvocationTargetException;

public class BenchmarkFilter {

    private static void benchmarkForJava() {
        long start2 = System.currentTimeMillis();

        Evaluable evaluable = new NativeEvaluable();

        for (int i = 0; i < 50000000; i++) {
            evaluable.eval(i);
        }

        long end2 = System.currentTimeMillis();

        System.out.println("java:" + (end2 - start2) + " ms");
    }

    public static void benchmarkForGroovyClassLoader() {

        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

        String groovyCode = "public class demo1 implements com.kuze.bigdata.study.Evaluable{\n"
                + "    @Override\n"
                + "    public Boolean eval(int i) {\n"
                + "        return String.valueOf(i).equals(\"1\");\n"
                + "    }\n"
                + "}";
        try {
            // 获得GroovyShell_2加载后的class
            Class<?> groovyClass = groovyClassLoader.parseClass(groovyCode);
            // 获得GroovyShell_2的实例
            Evaluable evaluable = (Evaluable) groovyClass.newInstance();

            long start1 = System.currentTimeMillis();

            for (int i = 0; i < 50000000; i++) {
                evaluable.eval(i);
            }

            long end1 = System.currentTimeMillis();

            System.out.println("groovy:" + (end1 - start1) + " ms");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public static void benchmarkForJanino() throws CompileException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        SimpleCompiler compiler = new SimpleCompiler();
        compiler.setParentClassLoader(BenchmarkFilter.class.getClassLoader());

        String content = "public class demo2 implements com.kuze.bigdata.study.Evaluable{\n"
                + "    @Override\n"
                + "    public Boolean eval(int i) {\n"
                + "        return String.valueOf(i).equals(\"1\");\n"
                + "    }\n"
                + "}";
        compiler.cook(content);

        Class<?> aClass = compiler.getClassLoader().loadClass("demo2");

        Evaluable evaluable = (Evaluable) aClass.newInstance();

        long start2 = System.currentTimeMillis();

        for (int i = 0; i < 50000000; i++) {
            evaluable.eval(i);
        }

        long end2 = System.currentTimeMillis();

        System.out.println("janino:" + (end2 - start2) + " ms");
    }

    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 5; i++) {
            benchmarkForJava();

            // janino
            benchmarkForJanino();

            // groovy classloader
            benchmarkForGroovyClassLoader();

            System.out.println();
        }
    }

}
