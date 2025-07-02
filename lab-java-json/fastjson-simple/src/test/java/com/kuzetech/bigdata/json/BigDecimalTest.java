package com.kuzetech.bigdata.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class BigDecimalTest {

    @Test
    public void test() {
        String jsonStr = "{'pay_normal': 123333333, 'pay_sum': 499999999999999999999999900000000001, 'pay_total': 49999999999999955555999999.99990000005555555555555555555555555500001,}";
        JSONObject jsonObject = JSON.parseObject(jsonStr);

        // 默认行为
        // 没有小数部分一开始使用 Integer，超过一定范围使用 BigDecimal
        System.out.println(jsonObject.getBigInteger("pay_sum"));
        // 有小数部分直接使用 BigDecimal
        System.out.println(jsonObject.getBigDecimal("pay_total"));

        System.out.println(JSON.toJSONString(jsonObject));
        System.out.println(JSON.toJSONString(jsonObject, SerializerFeature.WriteBigDecimalAsPlain));
    }

    @Test
    public void testWriteBigDecimalAsPlain() {
        BigDecimal bigDecimal = new BigDecimal("1E+10");
        System.out.println(JSON.toJSONString(bigDecimal));
        // 不使用科学计数法
        System.out.println(JSON.toJSONString(bigDecimal, SerializerFeature.WriteBigDecimalAsPlain));
    }


}
