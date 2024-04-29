package com.kuze.bigdata.kstreams.funnypipe.metadata;

public class ProcessorMessages {
    private String type;
    private Processors[] processors;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Processors[] getProcessors() {
        return processors;
    }

    public void setProcessors(Processors[] processors) {
        this.processors = processors;
    }
}
