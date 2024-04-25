package com.kuze.bigdata.rengine.benchmark.complex;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface Processable {

    Boolean filter(int i);

    void process(int i) throws JsonProcessingException;

}
