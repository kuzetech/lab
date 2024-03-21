package com.kuzetech.bigdata.lab.url;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UrlEncodeTest {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String data = "{\"test\":1}";
        String encode = URLEncoder.encode(data, StandardCharsets.UTF_8.name());
        System.out.println(encode);
        // %7B%22test%22%3A1%7D
    }
}
