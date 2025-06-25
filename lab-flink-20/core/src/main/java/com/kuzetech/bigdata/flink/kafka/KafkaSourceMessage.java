package com.kuzetech.bigdata.flink.kafka;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Setter
@Getter
public class KafkaSourceMessage implements Serializable {

    private Map<String, String> properties;
    private byte[] key;
    private byte[] data;

    public KafkaSourceMessage(ConsumerRecord<byte[], byte[]> record) {
        properties = parseHeader(record.headers());
        this.key = record.key();
        this.data = record.value();
    }

    public KafkaSourceMessage(byte[] data) {
        this.data = data;
    }

    public String getHeaderItem(String key) {
        if (properties != null) {
            properties.get(key);
        }
        return null;
    }

    public Map<String, String> parseHeader(Headers headers) {
        if (headers == null) {
            return null;
        }
        Map<String, String> headerMap = new HashMap<>();
        for (Header header : headers) {
            headerMap.put(header.key(), new String(header.value(), StandardCharsets.UTF_8));
        }
        return headerMap;
    }


}
