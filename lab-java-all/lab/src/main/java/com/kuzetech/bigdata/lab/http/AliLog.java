package com.kuzetech.bigdata.lab.http;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class AliLog {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        OkHttpClient client = new OkHttpClient.Builder().build();

        // 文件名
        String fileName = "wulalalite.data";
        int count = 0;

        // 使用类加载器获取资源文件
        try (InputStream inputStream = AliLog.class.getClassLoader().getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                System.err.println("文件未找到: " + fileName);
                return;
            }

            // 逐行读取文件内容

            String line;
            while ((line = reader.readLine()) != null) {
                count++;
                Request request = new Request.Builder()
                        .url(line)
                        .build();

                Call call = client.newCall(request);
                Response res = call.execute();
                try (ResponseBody body = res.body()) {
                    assert body != null;
                    if (!res.isSuccessful()) {
                        log.info("fail，code is {}, message is {}", res.code(), res.body().string());
                    }
                }
                System.out.println("已处理完数据，序号为：" + count);
            }
        } catch (Exception e) {
            System.out.println(count);
            e.printStackTrace();
        }

    }
}
