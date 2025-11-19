package com.kuzetech.bigdata.flink.pulsar.domain;

import com.kuzetech.bigdata.flink.base.CommonSourceMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.pulsar.client.api.Message;

import java.io.Serializable;
import java.util.HashMap;

@NoArgsConstructor
@Setter
@Getter
public class PulsarSourceMessage extends CommonSourceMessage implements Serializable {

    public PulsarSourceMessage(Message<byte[]> message) {
        super(CommonSourceMessage.SOURCE_KEY_PULSAR, new HashMap<>(message.getProperties()), message.getKeyBytes(), message.getData());
    }

    public PulsarSourceMessage(byte[] data) {
        super(CommonSourceMessage.SOURCE_KEY_PULSAR, data);
    }
}
