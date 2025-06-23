package com.kuzetech.bigdata.flink.pulsar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.pulsar.client.api.Message;

import java.io.Serializable;
import java.util.Properties;

@NoArgsConstructor
@Setter
@Getter
public class PulsarMessage implements Serializable {

    private Properties properties = new Properties();
    private byte[] key;
    private byte[] data;

    public PulsarMessage(Message<byte[]> message) {
        properties.putAll(message.getProperties());
        this.key = message.getKeyBytes();
        this.data = message.getData();
    }

    public String getHeaderItem(String key){
        return properties.getProperty(key);
    }
}
