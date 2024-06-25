package com.kuzetech.bigdata.flink;


import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class DynamicProducerRule implements Evaluable {

    private String code;

    private String targetTopic;

    private Evaluable evaluable;

    public void init(String id) {
        try {
            Class<Evaluable> clazz = JaninoUtils.genCodeAndGetClazz(id, targetTopic, code);
            this.evaluable = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean eval(InputMessage inputMessage) {
        return this.evaluable.eval(inputMessage);
    }

    @Override
    public void process(InputMessage inputMessage) {
        this.evaluable.process(inputMessage);
    }

}
