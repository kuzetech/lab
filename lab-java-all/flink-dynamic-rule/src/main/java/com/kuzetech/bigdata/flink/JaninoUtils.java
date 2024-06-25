package com.kuzetech.bigdata.flink;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.janino.SimpleCompiler;


@Slf4j
public class JaninoUtils {

    public static Class<Evaluable> genCodeAndGetClazz(String id, String topic, String code) throws Exception {
        SimpleCompiler COMPILER = new SimpleCompiler();
        COMPILER.setParentClassLoader(JaninoUtils.class.getClassLoader());
        String className = "CodeGen_" + topic + "_" + id;

        COMPILER.cook(code);

        return (Class<Evaluable>) COMPILER.getClassLoader().loadClass(className);
    }

}
