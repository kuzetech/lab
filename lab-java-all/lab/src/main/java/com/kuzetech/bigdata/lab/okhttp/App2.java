package com.kuzetech.bigdata.lab.okhttp;

import com.kuzetech.bigdata.lab.gzip.GzipApp;
import okhttp3.*;

import java.io.IOException;

public class App2 {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void main(String[] args) throws IOException {

        String content = "{\"batchId\":\"123\",\"messages\":[{\"a\":3,\"b\":\"3\"}]}";
        byte[] gzipContent = GzipApp.compress(content);
        RequestBody body = RequestBody.create(gzipContent, JSON);

        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .post(body)
                .addHeader("Content-Encoding", "gzip")
                .url("http://localhost:9901/internal/v1/ingest-metadata-sync/ack")
                .build();

        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            assert response.body() != null;
            if (response.isSuccessful()) {
                System.out.println("请求成功");
            }else{
                System.out.println(response.message());
            }
        }
    }
}
