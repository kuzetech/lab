package com.kuzetech.bigdata.flink.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@NoArgsConstructor
@Setter
@Getter
public class CommonSourceMessage implements Serializable {
    public static final String SOURCE_KEY_KAFKA = "kafka";
    public static final String SOURCE_KEY_PULSAR = "pulsar";

    private String source;
    private Map<String, String> properties;
    private byte[] key;
    private byte[] data;

    public CommonSourceMessage(String source, byte[] data) {
        this.source = source;
        this.data = data;
    }

    public CommonSourceMessage(String source, Map<String, String> properties, byte[] key, byte[] data) {
        this.source = source;
        this.properties = properties;
        this.key = key;
        this.data = data;
    }

    public String getHeaderItem(String key) {
        if (properties != null) {
            properties.get(key);
        }
        return null;
    }

}
