package com.kuze.bigdata.study;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ExpressionEvaluator;
import org.codehaus.janino.ScriptEvaluator;
import org.codehaus.janino.SimpleCompiler;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JaninoBaseTest {

    @Test
    public void expression() throws CompileException, InvocationTargetException {
        ExpressionEvaluator ee = new ExpressionEvaluator();
        ee.setParameters(new String[] { "a", "b" }, new Class[] { int.class, int.class });
        ee.setExpressionType(int.class);
        ee.cook("a + b");

        Object result = ee.evaluate(new Object[] { 1, 2 });
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

}
