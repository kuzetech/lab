package com.kuzetech.bigdata.flink.time;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");
}
