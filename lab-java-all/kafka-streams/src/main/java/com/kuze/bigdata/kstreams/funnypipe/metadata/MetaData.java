package com.kuze.bigdata.kstreams.funnypipe.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties("access_keys")
public class MetaData {
    private String name;
    private ProcessorMessages[] messages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcessorMessages[] getMessages() {
        return messages;
    }

    public void setMessages(ProcessorMessages[] messages) {
        this.messages = messages;
    }
}
