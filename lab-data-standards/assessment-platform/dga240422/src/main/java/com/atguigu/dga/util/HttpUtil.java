package com.atguigu.dga.util;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author WEIYUNHUI
 * @date 2024/9/3 14:07
 */
public class HttpUtil {

    private static OkHttpClient httpClient  = new OkHttpClient();

    public static String get(String url ){
        try {
            //创建Request对象
            Request.Builder builder = new Request.Builder();
            Request request = builder
                    .get()
                    .url(url)
                    .build();
            Call call = httpClient.newCall(request);
            Response response = call.execute();
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        System.out.println(get("http://hadoop102:18080/api/v1/applications/application_1684083580862_0012/1/stages/2"));
    }
}
