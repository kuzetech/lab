package com.kuzetech.bigdata.lab.hutool;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

public class SnowflakeTest {
    public static void main(String[] args) {
        Snowflake snowflake = IdUtil.getSnowflake(1, 3);
        System.out.println(Long.toBinaryString(snowflake.nextId()));
    }
}
