package com.kuzetech.bigdata.flink.test;

import org.apache.flink.types.PojoTestUtils;
import org.junit.Test;

public class PojoSerializerTest {

    @Test
    public void test() {
        PojoTestUtils.assertSerializedAsPojoWithoutKryo(WordWithCount.class);
    }
}