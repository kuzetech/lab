package com.kuze.bigdata.study;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ScriptEvaluator;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

public class JaninoBaseTest {

    @Test
    public void base() throws CompileException, InvocationTargetException {
        ScriptEvaluator se = new ScriptEvaluator();

        se.cook(
                ""
                        + "static int method1(int a, int b) {\n"
                        + "    return a+b;\n"
                        + "}\n"
        );

        Object[] args = {1, 2};
        Object result = se.evaluate(args);
        System.out.println("Result: " + result);
    }

}
