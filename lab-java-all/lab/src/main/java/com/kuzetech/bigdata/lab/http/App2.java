package com.kuzetech.bigdata.lab.http;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class App2 {


    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .get()
                .url("http://localhost:8080/v1/s")
                .build();

        Call call = client.newCall(request);
        call.execute();
        System.exit(1);
    }
}
