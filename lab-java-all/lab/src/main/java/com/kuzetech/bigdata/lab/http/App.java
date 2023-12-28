package com.kuzetech.bigdata.lab.http;

import com.kuzetech.bigdata.lab.gzip.GzipApp;
import com.kuzetech.bigdata.lab.hmac.Sha256;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class App {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String content = "{\"batchId\":\"123\",\"messages\":[{\"type\":\"event\",\"data\":{\"a\":3,\"b\":\"3\"}}]}";
        byte[] gzipContent = GzipApp.compress(content);
        RequestBody body = RequestBody.create(gzipContent, JSON);

        OkHttpClient client = new OkHttpClient.Builder().build();
        String accessId = "demo";
        String nonce = "123";
        String timestamp = "123";

        Request request = new Request.Builder()
                .post(body)
                .addHeader("X-Signature", Sha256.extracted(accessId, nonce, timestamp, gzipContent))
                .addHeader("X-Timestamp", timestamp)
                .addHeader("X-AccessKeyId", accessId)
                .addHeader("X-Nonce", nonce)
                .addHeader("Content-Encoding", "gzip")
                .url("http://localhost:8080/v1/collect")
                .build();

        Call call = client.newCall(request);
        Response res = call.execute();
        assert res.body() != null;
        if (res.isSuccessful()) {
            log.info("success, message is {}", res.body().string());
        } else {
            log.info("fail，code is {}, message is {}", res.code(), res.body().string());
        }

    }
}
