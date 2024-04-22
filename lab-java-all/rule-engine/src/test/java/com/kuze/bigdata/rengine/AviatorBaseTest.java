package com.kuze.bigdata.rengine;

import com.googlecode.aviator.AviatorEvaluator;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AviatorBaseTest {

    @Test
    public void variableTest() {
        Map<String, Object> map = new HashMap<>();
        map.put("a", 4);
        map.put("b", 100);

        // 2.定义表达式
        String exp = "a >= 3 && b > 50";

        // 3.使用Aviator执行表达式
        Object result = AviatorEvaluator.execute(exp, map);

        // 4.输出结果
        System.out.println(result);

    }
}
