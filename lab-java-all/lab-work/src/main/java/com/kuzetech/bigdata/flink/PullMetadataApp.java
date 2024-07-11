package com.kuzetech.bigdata.flink;


import com.xmfunny.turbine.sdk.extension.config.ConfigService;
import com.xmfunny.turbine.sdk.extension.config.DefaultConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PullMetadataApp {
    private static final Log LOG = LogFactory.getLog(PullMetadataApp.class);

    public static void main(String[] args) throws Exception {
        ConfigService configService = new ConfigService("http://localhost:8090",
                "turbine",
                "turbine123456",
                5);

        String s = configService.loadConfig("funnydb", "metadata-funnydb", new DefaultConverter());
        LOG.info(s);

        configService.close();
    }
}
