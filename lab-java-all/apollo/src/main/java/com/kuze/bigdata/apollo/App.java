package com.kuze.bigdata.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Config config = ConfigService.getAppConfig();
        String someKey = "timeout";
        Integer value = config.getIntProperty(someKey, 0);
        System.out.println(value);
    }
}
