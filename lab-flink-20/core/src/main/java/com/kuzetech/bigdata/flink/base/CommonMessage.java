package com.kuzetech.bigdata.flink.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;

@AllArgsConstructor
@Getter
public abstract class CommonMessage implements Serializable {

    private Map<String, String> headers;
    private byte[] key;
    private byte[] data;

    public String getHeaderItem(String key) {
        if (headers != null) {
            return headers.get(key);
        }
        return null;
    }

    public abstract byte[] getKey();
}
