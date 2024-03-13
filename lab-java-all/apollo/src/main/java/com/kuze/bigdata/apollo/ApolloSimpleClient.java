package com.kuze.bigdata.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;

/**
 * Hello world!
 */
public class ApolloSimpleClient {
    public static void main(String[] args) throws InterruptedException {
        Config config = ConfigService.getAppConfig();
        String someKey = "timeout";

        config.addChangeListener(new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                System.out.println("Changes for namespace " + changeEvent.getNamespace());
                for (String key : changeEvent.changedKeys()) {
                    ConfigChange change = changeEvent.getChange(key);
                    System.out.println(String.format("Found change - key: %s, oldValue: %s, newValue: %s, changeType: %s", change.getPropertyName(), change.getOldValue(), change.getNewValue(), change.getChangeType()));
                }
            }
        });

        while (true) {
            Integer value = config.getIntProperty(someKey, 0);
            System.out.println(value);
            Thread.sleep(10000);
        }

    }
}
