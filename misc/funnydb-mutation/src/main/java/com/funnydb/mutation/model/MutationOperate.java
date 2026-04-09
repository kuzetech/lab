package com.funnydb.mutation.model;

public enum MutationOperate {
    ADD("add"),
    SET("set"),
    SET_ONCE("setOnce");

    private final String value;

    MutationOperate(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MutationOperate fromValue(String value) {
        for (MutationOperate operate : values()) {
            if (operate.value.equalsIgnoreCase(value)) {
                return operate;
            }
        }
        return null;
    }
}
