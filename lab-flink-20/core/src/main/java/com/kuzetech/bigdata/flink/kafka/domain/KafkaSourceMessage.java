package com.kuzetech.bigdata.flink.kafka.domain;

import com.kuzetech.bigdata.flink.base.CommonSourceMessage;
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
public class KafkaSourceMessage extends CommonSourceMessage implements Serializable {

    public KafkaSourceMessage(ConsumerRecord<byte[], byte[]> record) {
        super(CommonSourceMessage.SOURCE_KEY_KAFKA, parseHeader(record.headers()), record.key(), record.value());
    }

    public KafkaSourceMessage(byte[] data) {
        super(CommonSourceMessage.SOURCE_KEY_KAFKA, data);
    }


    private static Map<String, String> parseHeader(Headers headers) {
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
