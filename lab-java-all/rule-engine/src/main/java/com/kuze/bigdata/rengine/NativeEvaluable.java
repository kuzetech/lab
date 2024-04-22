package com.kuze.bigdata.rengine;

public class NativeEvaluable implements Evaluable {
    @Override
    public Boolean eval(int i) {
        return String.valueOf(i).equals("1");
    }
}
