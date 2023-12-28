package com.kuzetech.bigdata.lab.okhttp;

import com.kuzetech.bigdata.lab.gzip.GzipApp;
import com.kuzetech.bigdata.lab.hmac.Sha256;
import okhttp3.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class App {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        //String content = "{\"batchId\":\"123\",\"messages\":[{\"type\":\"event\",\"data\":{\"a\":3,\"b\":\"3\"}}]}";
        String content = "{\"batchId\":\"123\",\"messages\":[{\"a\":3,\"b\":\"3\"}]}";
        byte[] gzipContent = GzipApp.compress(content);
        RequestBody body = RequestBody.create(gzipContent, JSON);

        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .post(body)
                .addHeader("X-Signature", Sha256.extracted(gzipContent))
                .addHeader("X-Timestamp", "123")
                .addHeader("X-AccessKeyId", "demo")
                .addHeader("X-Nonce", "123")
                .addHeader("Content-Encoding", "gzip")
                .url("http://localhost:8080/v1/collect")
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        assert response.body() != null;
        System.out.println(response.code());
        System.out.println(response.body().string());
    }
}
