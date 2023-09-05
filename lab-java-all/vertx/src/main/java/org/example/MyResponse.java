package org.example;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyResponse {

    private String result;
    private int code;
    private String message;

    public MyResponse(String result, int code, String message) {
        this.result = result;
        this.code = code;
        this.message = message;
    }
}
