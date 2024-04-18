package com.kuzetech.bigdata.flink.dynamicrule;


public interface Evaluable {

    Boolean eval(InputMessage inputMessage);

    void process(InputMessage inputMessage);
}
