package com.kuzetech.bigdata.flink;


public interface Evaluable {

    Boolean eval(InputMessage inputMessage);

    void process(InputMessage inputMessage);
}
