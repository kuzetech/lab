package com.kuze.bigdata.study;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.ICompiler;
import org.codehaus.commons.compiler.util.ResourceFinderClassLoader;
import org.codehaus.commons.compiler.util.resource.MapResourceCreator;
import org.codehaus.commons.compiler.util.resource.MapResourceFinder;
import org.codehaus.commons.compiler.util.resource.Resource;
import org.codehaus.commons.compiler.util.resource.StringResource;
import org.codehaus.janino.CompilerFactory;
import org.codehaus.janino.ExpressionEvaluator;
import org.codehaus.janino.ScriptEvaluator;
import org.codehaus.janino.SimpleCompiler;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class JaninoBaseTest {

    @Test
    public void expression() throws CompileException, InvocationTargetException {
        ExpressionEvaluator ee = new ExpressionEvaluator();
        ee.setParameters(new String[]{"a", "b"}, new Class[]{int.class, int.class});
        ee.setExpressionType(int.class);
        ee.cook("a + b");

        Object result = ee.evaluate(new Object[]{1, 2});
        System.out.println("Janino result=" + result);
    }

    @Test
    public void script() throws CompileException, InvocationTargetException {
        ScriptEvaluator se = new ScriptEvaluator();

        se.cook(
                ""
                        + "static void method1() {\n"
                        + "    System.out.println(1);\n"
                        + "}\n"
                        + "\n"
                        + "method1();\n"
                        + "method2();\n"
                        + "\n"
                        + "static void method2() {\n"
                        + "    System.out.println(2);\n"
                        + "}\n"
        );

        se.evaluate();
    }

    @Test
    public void simple() throws CompileException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        SimpleCompiler compiler = new SimpleCompiler();
        compiler.setParentClassLoader(JaninoBaseTest.class.getClassLoader());

        String content = "public class Foo {\n" +
                "    public void say(int a) {\n" +
                "        System.out.println(a);\n" +
                "    }\n" +
                "}";
        compiler.cook(content);

        Class<?> aClass = compiler.getClassLoader().loadClass("Foo");

        Method sayMethod = aClass.getDeclaredMethod("say", int.class);
        sayMethod.invoke(aClass.newInstance(), new Object[]{1});

    }

    @Test
    public void batch() throws CompileException, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        CompilerFactory compilerFactory = new CompilerFactory();
        ICompiler compiler = compilerFactory.newCompiler();

        // Store generated .class files in a Map:
        Map<String, byte[]> classes = new HashMap<String, byte[]>();
        compiler.setClassFileCreator(new MapResourceCreator(classes));

        // Now compile two units from strings:
        compiler.compile(new Resource[]{
                new StringResource(
                        "pkg1/A.java",
                        "package pkg1; public class A { public static int meth() { return pkg2.B.meth(); } }"
                ),
                new StringResource(
                        "pkg2/B.java",
                        "package pkg2; public class B { public static int meth() { return 77;            } }"
                ),
        });

        // Set up a class loader that uses the generated classes.
        ClassLoader cl = new ResourceFinderClassLoader(
                new MapResourceFinder(classes),    // resourceFinder
                JaninoBaseTest.class.getClassLoader() // parent
        );

        Class<?> bClass = cl.loadClass("pkg2.B");
        Method meth = bClass.getDeclaredMethod("meth");

        Object result = meth.invoke(bClass.newInstance());
        System.out.println("Janino result=" + result);
    }


    @Test
    public void demo() throws CompileException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        SimpleCompiler compiler = new SimpleCompiler();
        compiler.setParentClassLoader(JaninoBaseTest.class.getClassLoader());

        String content = "public class demo_002 {\n"
                + "    public boolean eval(com.kuze.bigdata.study.ClientLog log) {\n"
                + "        return String.valueOf(log.getLogId()).equals(\"1\");\n"
                + "    }\n"
                + "}";
        compiler.cook(content);

        Class<?> aClass = compiler.getClassLoader().loadClass("demo_002");

        Method sayMethod = aClass.getDeclaredMethod("eval", ClientLog.class);

        Object result = sayMethod.invoke(aClass.newInstance(), new Object[]{ClientLog.getDefaultInstance()});
        System.out.println("Janino result=" + result);

    }

}
