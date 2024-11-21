package com.kuzetech.bigdata.sentry;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main(String[] args) {

        Sentry.init(options -> {
            options.setDsn("https://d0a240dd8ffdf335ff3954f43c77c20d@sentry.sausage.xd.com/72");
            options.setEnvironment("dev");
            options.setSampleRate(0.1);
        });

        log.error("发现异常", new Exception("test"));
    }
}
