package com.kuze.bigdata.study;

public class NativeEvaluable implements Evaluable {
    @Override
    public Boolean eval(int i) {
        return String.valueOf(i).equals("1");
    }
}
