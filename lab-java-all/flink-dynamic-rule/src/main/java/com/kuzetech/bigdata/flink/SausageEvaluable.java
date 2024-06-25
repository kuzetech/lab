package com.kuzetech.bigdata.flink;

public class SausageEvaluable implements Evaluable {
    @Override
    public Boolean eval(InputMessage inputMessage) {
        return true;
    }

    @Override
    public void process(InputMessage inputMessage) {
        inputMessage.setTime(inputMessage.getTime() + 1);
    }
}
