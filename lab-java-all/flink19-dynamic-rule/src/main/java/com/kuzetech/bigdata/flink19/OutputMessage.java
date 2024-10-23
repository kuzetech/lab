package com.kuzetech.bigdata.flink19;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutputMessage {
    private static ObjectMapper mapper = new ObjectMapper();

    private String destTopic;
    private byte[] data;

    private OutputMessage() {

    }

    public static OutputMessage createFromInputMessage(String targetTopic, InputMessage inputMessage) throws JsonProcessingException {
        OutputMessage outputMessage = new OutputMessage();
        outputMessage.setDestTopic(targetTopic);
        outputMessage.setData(mapper.writeValueAsBytes(inputMessage));
        return outputMessage;
    }

}
