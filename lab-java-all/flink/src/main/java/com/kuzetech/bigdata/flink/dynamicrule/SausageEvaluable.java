package com.kuzetech.bigdata.flink.dynamicrule;

public class SausageEvaluable implements com.kuzetech.bigdata.flink.dynamicrule.Evaluable {
    @Override
    public Boolean eval(InputMessage inputMessage) {
        return true;
    }

    @Override
    public void process(InputMessage inputMessage) {
        inputMessage.setTime(inputMessage.getTime() + 1);
    }
}
