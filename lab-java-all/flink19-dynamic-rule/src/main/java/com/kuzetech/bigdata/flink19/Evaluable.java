package com.kuzetech.bigdata.flink19;


public interface Evaluable {

    Boolean eval(InputMessage inputMessage);

    void process(InputMessage inputMessage);
}
