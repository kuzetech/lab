package com.kuzetech.bigdata.flink.base;

import com.kuzetech.bigdata.flink.fake.FakeUtil;
import com.kuzetech.bigdata.flink.json.JsonUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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

    public static CommonSourceMessage generateMessage() {
        CommonSourceMessage message = new CommonSourceMessage();
        Map<String, String> properties = new HashMap<>();
        properties.put("event", FakeUtil.generateEvent().toString());
        message.setProperties(properties);
        String msg = String.format(JsonUtil.FUNNYDB_MESSAGE_TEMP, System.currentTimeMillis());
        message.setData(msg.getBytes(StandardCharsets.UTF_8));
        return message;
    }

    public String getHeaderItem(String key) {
        if (properties != null) {
            properties.get(key);
        }
        return null;
    }

}
