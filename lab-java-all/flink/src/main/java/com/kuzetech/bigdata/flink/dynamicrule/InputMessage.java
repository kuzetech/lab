package com.kuzetech.bigdata.flink.dynamicrule;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

@Getter
@Setter
public class InputMessage {
    public static ObjectMapper mapper = new ObjectMapper();

    private String app;
    private String event;
    private Long time;

    private InputMessage() {

    }

    public static InputMessage createFromRecord(ConsumerRecord<byte[], byte[]> record) {
        InputMessage inputMessage = null;
        try {
            inputMessage = mapper.readValue(record.value(), InputMessage.class);
        } catch (IOException ignored) {

        }
        return inputMessage;
    }
}
