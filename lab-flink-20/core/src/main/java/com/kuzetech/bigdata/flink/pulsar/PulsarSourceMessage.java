package com.kuzetech.bigdata.flink.pulsar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.pulsar.client.api.Message;

import java.io.Serializable;
import java.util.Map;

@NoArgsConstructor
@Setter
@Getter
public class PulsarSourceMessage implements Serializable {

    private Map<String, String> properties;
    private byte[] key;
    private byte[] data;

    public PulsarSourceMessage(Message<byte[]> message) {
        properties = message.getProperties();
        this.key = message.getKeyBytes();
        this.data = message.getData();
    }

    public PulsarSourceMessage(byte[] data) {
        this.data = data;
    }

    public String getHeaderItem(String key) {
        if (properties != null) {
            properties.get(key);
        }
        return null;
    }
}
